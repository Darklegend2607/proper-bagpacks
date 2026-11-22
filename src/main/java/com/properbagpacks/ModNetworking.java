package com.properbagpacks;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModNetworking {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(OpenWornPayload.ID, OpenWornPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenWornPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            if (player.currentScreenHandler != player.playerScreenHandler) return;
            ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof BackpackItem) {
                BackpackItem.open(player, chest, 38);
            }
        });
    }
}
