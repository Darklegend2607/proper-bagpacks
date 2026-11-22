package com.properbagpacks.client;

import com.properbagpacks.BackpackLayout;
import com.properbagpacks.BackpackScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {
    private final BackpackLayout layout;

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.layout = handler.tier().layout();
        this.backgroundWidth = layout.width;
        this.backgroundHeight = layout.height;
        this.titleX = 7;
        this.titleY = 6;
        this.playerInventoryTitleX = layout.invX;
        this.playerInventoryTitleY = layout.invY - 12;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int w = backgroundWidth;
        int h = backgroundHeight;
        int x = this.x;
        int y = this.y;

        // panel
        context.fill(x, y, x + w, y + h, 0xFF000000);
        context.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFFC6C6C6);
        context.fill(x + 1, y + 1, x + w - 2, y + 2, 0xFFFFFFFF);
        context.fill(x + 1, y + 1, x + 2, y + h - 2, 0xFFFFFFFF);
        context.fill(x + 2, y + h - 2, x + w - 1, y + h - 1, 0xFF555555);
        context.fill(x + w - 2, y + 2, x + w - 1, y + h - 1, 0xFF555555);

        // slot cells
        for (Slot slot : handler.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            context.fill(sx, sy, sx + 18, sy + 18, 0xFF373737);
            context.fill(sx + 1, sy + 1, sx + 18, sy + 18, 0xFFFFFFFF);
            context.fill(sx + 1, sy + 1, sx + 17, sy + 17, handler.isLocked(slot) ? 0xFF8B6B6B : 0xFF8B8B8B);
        }
    }
}
