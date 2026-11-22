package com.properbagpacks;

import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/** Backpack + one or more dyes in a crafting grid = recolored backpack (contents are kept). */
public class BackpackDyeRecipe extends SpecialCraftingRecipe {
    public BackpackDyeRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        int backpacks = 0;
        int dyes = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getStackInSlot(i);
            if (s.isEmpty()) continue;
            if (s.getItem() instanceof BackpackItem) backpacks++;
            else if (s.getItem() instanceof DyeItem) dyes++;
            else return false;
        }
        return backpacks == 1 && dyes >= 1;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack backpack = ItemStack.EMPTY;
        List<DyeItem> dyes = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getStackInSlot(i);
            if (s.getItem() instanceof BackpackItem) backpack = s;
            else if (s.getItem() instanceof DyeItem dye) dyes.add(dye);
        }
        if (backpack.isEmpty() || dyes.isEmpty()) return ItemStack.EMPTY;
        return DyedColorComponent.setColor(backpack, dyes);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DYE;
    }
}
