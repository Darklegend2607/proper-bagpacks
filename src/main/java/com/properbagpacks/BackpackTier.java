package com.properbagpacks;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum BackpackTier {
    LEATHER("leather", 9, 3, 1, 0x8B5A2B, 0xB98B55),
    COPPER("copper", 9, 4, 2, 0xB4684A, 0xE9A07A),
    IRON("iron", 9, 6, 3, 0xA8A8A8, 0xE8E8E8),
    GOLD("gold", 12, 6, 4, 0xE6B422, 0xFFE680),
    DIAMOND("diamond", 15, 6, 5, 0x3CC8C0, 0xA0FFF4),
    NETHERITE("netherite", 18, 6, 6, 0x3F3A44, 0x7A6F85);

    private final String id;
    private final int cols;
    private final int rows;
    private final int upgradeSlots;
    private final int bodyColor;
    private final int trimColor;

    BackpackTier(String id, int cols, int rows, int upgradeSlots, int bodyColor, int trimColor) {
        this.id = id;
        this.cols = cols;
        this.rows = rows;
        this.upgradeSlots = upgradeSlots;
        this.bodyColor = bodyColor;
        this.trimColor = trimColor;
    }

    public String id() { return id; }
    public int cols() { return cols; }
    public int rows() { return rows; }
    public int slots() { return cols * rows; }
    public int upgradeSlots() { return upgradeSlots; }
    public int bodyColor() { return bodyColor; }
    public int trimColor() { return trimColor; }

    public BackpackLayout layout() { return new BackpackLayout(this); }

    public BackpackTier next() {
        BackpackTier[] all = values();
        return ordinal() + 1 < all.length ? all[ordinal() + 1] : null;
    }

    /** Material ring used (around the previous tier backpack) to craft this tier. */
    public Item ringMaterial() {
        return switch (this) {
            case LEATHER -> Items.LEATHER;
            case COPPER -> Items.COPPER_INGOT;
            case IRON -> Items.IRON_INGOT;
            case GOLD -> Items.GOLD_INGOT;
            case DIAMOND -> Items.DIAMOND;
            case NETHERITE -> Items.NETHERITE_INGOT;
        };
    }
}
