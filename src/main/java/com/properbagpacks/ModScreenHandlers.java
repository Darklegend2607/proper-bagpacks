package com.properbagpacks;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ScreenHandlerType<BackpackScreenHandler> BACKPACK = Registry.register(
            Registries.SCREEN_HANDLER,
            ProperBagPacks.id("backpack"),
            new ExtendedScreenHandlerType<BackpackScreenHandler, BackpackOpenData>(
                    BackpackScreenHandler::new, BackpackOpenData.CODEC));

    public static void register() {
    }
}
