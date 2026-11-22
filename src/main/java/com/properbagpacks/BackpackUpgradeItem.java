package com.properbagpacks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BackpackUpgradeItem extends Item {
    private final UpgradeKind kind;
    private final int multiplier;

    public BackpackUpgradeItem(UpgradeKind kind, int multiplier, Settings settings) {
        super(settings);
        this.kind = kind;
        this.multiplier = multiplier;
    }

    public UpgradeKind kind() { return kind; }
    public int multiplier() { return multiplier; }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (kind == UpgradeKind.STACK) {
            tooltip.add(Text.translatable("tooltip.properbagpacks.stack_multiplier", multiplier).formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.properbagpacks.feeder").formatted(Formatting.GRAY));
        }
    }
}
