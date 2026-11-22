package com.properbagpacks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ProperBagPacks implements ModInitializer {
    public static final String MOD_ID = "properbagpacks";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModComponents.register();
        ModItems.register();
        ModScreenHandlers.register();
        ModRecipes.register();
        ModNetworking.register();
        AutoFeeder.register();
    }
}
