package com.properbagpacks;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModComponents {
    public static final ComponentType<BackpackContents> CONTENTS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            ProperBagPacks.id("contents"),
            ComponentType.<BackpackContents>builder().codec(BackpackContents.CODEC).build());

    public static void register() {
    }
}
