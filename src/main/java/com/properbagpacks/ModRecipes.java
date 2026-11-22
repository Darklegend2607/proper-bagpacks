package com.properbagpacks;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    public static final RecipeSerializer<BackpackDyeRecipe> DYE = Registry.register(
            Registries.RECIPE_SERIALIZER, ProperBagPacks.id("backpack_dye"),
            new SpecialCraftingRecipe.SpecialRecipeSerializer<BackpackDyeRecipe>(BackpackDyeRecipe::new));

    public static final RecipeSerializer<TierUpgradeRecipe> TIER_UPGRADE = Registry.register(
            Registries.RECIPE_SERIALIZER, ProperBagPacks.id("tier_upgrade"),
            new SpecialCraftingRecipe.SpecialRecipeSerializer<TierUpgradeRecipe>(TierUpgradeRecipe::new));

    public static void register() {
    }
}
