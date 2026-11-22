package com.properbagpacks;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Zero-defense, layer-less "armor" so the backpack can sit in the chest slot without drawing armor. */
public class ModArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> BACKPACK = create();

    private static RegistryEntry<ArmorMaterial> create() {
        Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type t : ArmorItem.Type.values()) {
            defense.put(t, 0);
        }
        return Registry.registerReference(
                Registries.ARMOR_MATERIAL,
                ProperBagPacks.id("backpack"),
                new ArmorMaterial(defense, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                        () -> Ingredient.EMPTY, List.of(), 0.0f, 0.0f));
    }
}
