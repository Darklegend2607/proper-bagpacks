package com.properbagpacks;

public final class BackpackLayout {
    public final int upgradeX = 7;
    public final int gridX = 29;
    public final int gridY = 18;
    public final int width;
    public final int height;
    public final int invX;
    public final int invY;
    public final int hotbarY;

    public BackpackLayout(BackpackTier tier) {
        this.width = Math.max(176, gridX + tier.cols() * 18 + 7);
        this.invX = (width - 162) / 2;
        this.invY = gridY + tier.rows() * 18 + 14;
        this.hotbarY = invY + 58;
        this.height = hotbarY + 18 + 7;
    }
}
