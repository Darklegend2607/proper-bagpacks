package com.properbagpacks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BackpackScreenHandler extends ScreenHandler {
    private final BackpackTier tier;
    private final BackpackInventory inventory;
    private final int lockedSlot;
    private final ItemStack backpackStack;
    private final PlayerInventory playerInventory;

    /** Client constructor. */
    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, BackpackOpenData data) {
        this(syncId, playerInventory, BackpackTier.values()[data.tier()],
                BackpackInventory.empty(BackpackTier.values()[data.tier()]), data.lockedSlot(), ItemStack.EMPTY);
    }

    /** Server constructor. */
    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, BackpackTier tier,
                                 BackpackInventory inventory, int lockedSlot, ItemStack backpackStack) {
        super(ModScreenHandlers.BACKPACK, syncId);
        this.tier = tier;
        this.inventory = inventory;
        this.lockedSlot = lockedSlot;
        this.backpackStack = backpackStack;
        this.playerInventory = playerInventory;

        BackpackLayout l = tier.layout();
        int cols = tier.cols();
        for (int i = 0; i < tier.slots(); i++) {
            addSlot(new StorageSlot(inventory, i, l.gridX + (i % cols) * 18 + 1, l.gridY + (i / cols) * 18 + 1));
        }
        for (int i = 0; i < tier.upgradeSlots(); i++) {
            addSlot(new UpgradeSlot(inventory, tier.slots() + i, l.upgradeX + 1, l.gridY + i * 18 + 1));
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                addSlot(new PlayerSlot(playerInventory, 9 + r * 9 + c, l.invX + c * 18 + 1, l.invY + r * 18 + 1, lockedSlot));
            }
        }
        for (int c = 0; c < 9; c++) {
            addSlot(new PlayerSlot(playerInventory, c, l.invX + c * 18 + 1, l.hotbarY + 1, lockedSlot));
        }
    }

    public BackpackTier tier() { return tier; }
    public ItemStack backpackStack() { return backpackStack; }

    public boolean isLocked(Slot slot) {
        return slot instanceof PlayerSlot p && p.locked;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        if (player.getWorld().isClient) return true;
        return !backpackStack.isEmpty() && player.getInventory().getStack(lockedSlot) == backpackStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.markDirty();
    }

    /** Stops drag-splitting from shrinking stacks that are bigger than the vanilla stack size. */
    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        if (slot instanceof StorageSlot) {
            ItemStack s = slot.getStack();
            if (!s.isEmpty() && s.getCount() > s.getMaxCount()) return false;
        }
        return super.canInsertIntoSlot(slot);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasStack()) return ItemStack.EMPTY;
        ItemStack original = slot.getStack();
        ItemStack copy = original.copy();

        int storage = tier.slots();
        int upgrades = tier.upgradeSlots();
        int playerStart = storage + upgrades;

        if (index < playerStart) {
            if (!insertItem(original, playerStart, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            boolean moved = false;
            if (original.getItem() instanceof BackpackUpgradeItem) {
                moved = insertItem(original, storage, playerStart, false);
            }
            if (!moved) {
                moved = insertItem(original, 0, storage, false);
            }
            if (!moved) return ItemStack.EMPTY;
        }

        if (original.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        return copy;
    }

    // ---------------------------------------------------------------- slots

    public static class StorageSlot extends Slot {
        private final BackpackInventory backpack;

        public StorageSlot(BackpackInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.backpack = inventory;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem);
        }

        @Override
        public int getMaxItemCount() {
            return backpack.getMaxCountPerStack();
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return backpack.getMaxCount(stack);
        }

        /** Never lift more than one vanilla stack at a time. */
        @Override
        public ItemStack takeStack(int amount) {
            return super.takeStack(Math.min(amount, getStack().getMaxCount()));
        }
    }

    public static class UpgradeSlot extends Slot {
        private final BackpackInventory backpack;

        public UpgradeSlot(BackpackInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.backpack = inventory;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof BackpackUpgradeItem u && !backpack.hasUpgradeKind(u.kind(), getIndex());
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return 1;
        }

        /** A stack upgrade cannot be removed while stacks are too big for the vanilla limit. */
        @Override
        public boolean canTakeItems(PlayerEntity player) {
            if (getStack().getItem() instanceof BackpackUpgradeItem u && u.kind() == UpgradeKind.STACK) {
                return backpack.fitsWithoutStackUpgrade();
            }
            return true;
        }
    }

    public static class PlayerSlot extends Slot {
        public final boolean locked;

        public PlayerSlot(Inventory inventory, int index, int x, int y, int lockedIndex) {
            super(inventory, index, x, y);
            this.locked = index == lockedIndex;
        }

        @Override
        public boolean canTakeItems(PlayerEntity player) {
            return !locked;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !locked;
        }
    }
}
