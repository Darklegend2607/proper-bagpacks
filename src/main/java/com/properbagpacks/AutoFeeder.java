package com.properbagpacks;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

/** Eats food from a backpack with an Auto Feeder Upgrade when the player is hungry (never wastes food). */
public class AutoFeeder {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 10 != 0) return;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tick(player);
            }
        });
    }

    private static void tick(ServerPlayerEntity player) {
        if (!player.isAlive() || player.isSpectator() || player.isCreative()) return;
        if (player.getHungerManager().getFoodLevel() >= 20) return;

        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!(stack.getItem() instanceof BackpackItem item)) continue;
            if (!stack.contains(ModComponents.CONTENTS)) continue;
            // don't touch a backpack the player currently has open
            if (player.currentScreenHandler instanceof BackpackScreenHandler open && open.backpackStack() == stack) continue;

            BackpackInventory backpack = BackpackInventory.load(stack, item.tier());
            if (!backpack.hasUpgrade(UpgradeKind.FEEDER)) continue;
            if (feed(player, backpack)) return;
        }
    }

    private static boolean feed(ServerPlayerEntity player, BackpackInventory backpack) {
        int missing = 20 - player.getHungerManager().getFoodLevel();
        int best = -1;
        int bestNutrition = 0;
        for (int i = 0; i < backpack.storageSize(); i++) {
            ItemStack s = backpack.getStack(i);
            if (s.isEmpty()) continue;
            FoodComponent food = s.get(DataComponentTypes.FOOD);
            if (food == null) continue;
            // skip anything with side effects, leftovers, or odd behavior
            if (!food.effects().isEmpty() || food.usingConvertsTo().isPresent() || s.isOf(Items.CHORUS_FRUIT)) continue;
            if (food.nutrition() <= missing && food.nutrition() > bestNutrition) {
                best = i;
                bestNutrition = food.nutrition();
            }
        }
        if (best < 0) return false;

        ItemStack food = backpack.getStack(best);
        food.finishUsing(player.getWorld(), player);
        if (food.isEmpty()) {
            backpack.setStack(best, ItemStack.EMPTY);
        } else {
            backpack.markDirty();
        }
        return true;
    }
}
