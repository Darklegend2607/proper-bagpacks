package com.properbagpacks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory view over a backpack item. Indexes [0, slots) are storage,
 * the rest are upgrade slots. Every change is written straight back to the item.
 */
public class BackpackInventory implements Inventory {
    private final BackpackTier tier;
    private final DefaultedList<ItemStack> items;
    private final DefaultedList<ItemStack> upgrades;
    private final ItemStack owner;

    private BackpackInventory(BackpackTier tier, ItemStack owner) {
        this.tier = tier;
        this.owner = owner;
        this.items = DefaultedList.ofSize(tier.slots(), ItemStack.EMPTY);
        this.upgrades = DefaultedList.ofSize(tier.upgradeSlots(), ItemStack.EMPTY);
    }

    /** Client-side / detached inventory. */
    public static BackpackInventory empty(BackpackTier tier) {
        return new BackpackInventory(tier, ItemStack.EMPTY);
    }

    public static BackpackInventory load(ItemStack owner, BackpackTier tier) {
        BackpackInventory inv = new BackpackInventory(tier, owner);
        BackpackContents c = owner.get(ModComponents.CONTENTS);
        if (c != null) {
            fill(inv.items, c.items());
            fill(inv.upgrades, c.upgrades());
        }
        return inv;
    }

    private static void fill(DefaultedList<ItemStack> target, List<BackpackContents.Entry> entries) {
        for (BackpackContents.Entry e : entries) {
            if (e.slot() >= 0 && e.slot() < target.size() && e.count() > 0) {
                target.set(e.slot(), e.stack().copyWithCount(e.count()));
            }
        }
    }

    private static List<BackpackContents.Entry> entries(DefaultedList<ItemStack> list) {
        List<BackpackContents.Entry> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemStack s = list.get(i);
            if (!s.isEmpty()) {
                out.add(new BackpackContents.Entry(i, s.getCount(), s.copyWithCount(1)));
            }
        }
        return out;
    }

    public void save() {
        if (owner.isEmpty()) return;
        List<BackpackContents.Entry> it = entries(items);
        List<BackpackContents.Entry> up = entries(upgrades);
        if (it.isEmpty() && up.isEmpty()) {
            owner.remove(ModComponents.CONTENTS);
        } else {
            owner.set(ModComponents.CONTENTS, new BackpackContents(it, up));
        }
    }

    public BackpackTier tier() { return tier; }
    public int storageSize() { return items.size(); }

    // ---- upgrades ----

    public boolean hasUpgrade(UpgradeKind kind) {
        for (ItemStack s : upgrades) {
            if (s.getItem() instanceof BackpackUpgradeItem u && u.kind() == kind) return true;
        }
        return false;
    }

    /** True if another upgrade slot (not {@code exceptIndex}) already holds this kind. */
    public boolean hasUpgradeKind(UpgradeKind kind, int exceptIndex) {
        for (int i = 0; i < upgrades.size(); i++) {
            if (items.size() + i == exceptIndex) continue;
            if (upgrades.get(i).getItem() instanceof BackpackUpgradeItem u && u.kind() == kind) return true;
        }
        return false;
    }

    public int stackMultiplier() {
        int m = 1;
        for (ItemStack s : upgrades) {
            if (s.getItem() instanceof BackpackUpgradeItem u && u.kind() == UpgradeKind.STACK) {
                m = Math.max(m, u.multiplier());
            }
        }
        return m;
    }

    /** True if every stored stack fits in a normal (non-upgraded) stack. */
    public boolean fitsWithoutStackUpgrade() {
        for (ItemStack s : items) {
            if (!s.isEmpty() && s.getCount() > s.getMaxCount()) return false;
        }
        return true;
    }

    @Override
    public int getMaxCount(ItemStack stack) {
        int base = stack.getMaxCount();
        return base <= 1 ? base : base * stackMultiplier();
    }

    @Override
    public int getMaxCountPerStack() {
        return 64 * 16;
    }

    // ---- Inventory ----

    private DefaultedList<ItemStack> listFor(int slot) {
        return slot < items.size() ? items : upgrades;
    }

    private int local(int slot) {
        return slot < items.size() ? slot : slot - items.size();
    }

    @Override
    public int size() {
        return items.size() + upgrades.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        for (ItemStack s : upgrades) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= size()) return ItemStack.EMPTY;
        return listFor(slot).get(local(slot));
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 0 || slot >= size()) return ItemStack.EMPTY;
        ItemStack r = Inventories.splitStack(listFor(slot), local(slot), amount);
        if (!r.isEmpty()) markDirty();
        return r;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= size()) return ItemStack.EMPTY;
        ItemStack r = Inventories.removeStack(listFor(slot), local(slot));
        markDirty();
        return r;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= size()) return;
        listFor(slot).set(local(slot), stack);
        markDirty();
    }

    @Override
    public void markDirty() {
        save();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
        upgrades.clear();
        for (int i = 0; i < tier.slots(); i++) items.add(ItemStack.EMPTY);
        for (int i = 0; i < tier.upgradeSlots(); i++) upgrades.add(ItemStack.EMPTY);
        markDirty();
    }
}
