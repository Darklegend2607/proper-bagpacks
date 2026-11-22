package com.properbagpacks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

/**
 * Backpack in the middle of the 3x3 grid, 8 of the next tier's material around it.
 * The new backpack keeps the contents, upgrades and color.
 */
public class TierUpgradeRecipe extends SpecialCraftingRecipe {
    public TierUpgradeRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (input.getWidth() != 3 || input.getHeight() != 3) return false;
        ItemStack center = input.getStackInSlot(4);
        if (!(center.getItem() instanceof BackpackItem backpack)) return false;
        BackpackTier next = backpack.tier().next();
        if (next == null) return false;
        Item material = next.ringMaterial();
        for (int i = 0; i < 9; i++) {
            if (i != 4 && !input.getStackInSlot(i).isOf(material)) return false;
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack center = input.getStackInSlot(4);
        if (!(center.getItem() instanceof BackpackItem backpack)) return ItemStack.EMPTY;
        BackpackTier next = backpack.tier().next();
        if (next == null) return ItemStack.EMPTY;
        return center.copyComponentsToNewStack(ModItems.BACKPACKS.get(next), 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TIER_UPGRADE;
    }
}
