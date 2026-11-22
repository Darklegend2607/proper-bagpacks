package com.properbagpacks.client;

import com.properbagpacks.BackpackItem;
import com.properbagpacks.ModItems;
import com.properbagpacks.ModScreenHandlers;
import com.properbagpacks.OpenWornPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import org.lwjgl.glfw.GLFW;

public class ProperBagPacksClient implements ClientModInitializer {
    public static KeyBinding OPEN_KEY;

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.BACKPACK, BackpackScreen::new);

        OPEN_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.properbagpacks.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.inventory"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_KEY.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    ClientPlayNetworking.send(new OpenWornPayload());
                }
            }
        });

        // dye color: layer 0 is the tintable body, layer 1 is the fixed trim
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex != 0 || !(stack.getItem() instanceof BackpackItem item)) return -1;
            return DyedColorComponent.getColor(stack, 0xFF000000 | item.tier().bodyColor());
        }, ModItems.BACKPACKS.values().toArray(new Item[0]));

        EntityModelLayerRegistry.registerModelLayer(BackpackFeatureRenderer.LAYER, BackpackFeatureRenderer::createModelData);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                registrationHelper.register(new BackpackFeatureRenderer(playerRenderer, context.getModelLoader()));
            }
        });
    }
}
