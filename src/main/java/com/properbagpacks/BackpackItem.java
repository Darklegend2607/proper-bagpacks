package com.properbagpacks;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class BackpackItem extends ArmorItem {
    private final BackpackTier tier;

    public BackpackItem(BackpackTier tier, Item.Settings settings) {
        super(ModArmorMaterials.BACKPACK, ArmorItem.Type.CHESTPLATE, settings);
        this.tier = tier;
    }

    public BackpackTier tier() {
        return tier;
    }

    /** Right-click opens the backpack, sneak + right-click wears it. */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            return super.use(world, user, hand);
        }
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            open(player, stack, hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40);
        }
        return TypedActionResult.success(stack, world.isClient);
    }

    /** invSlot = index of the backpack in the player's inventory (0-35 main, 38 chest, 40 offhand). */
    public static void open(ServerPlayerEntity player, ItemStack stack, int invSlot) {
        if (!(stack.getItem() instanceof BackpackItem item)) return;
        BackpackTier tier = item.tier();
        player.openHandledScreen(new ExtendedScreenHandlerFactory<BackpackOpenData>() {
            @Override
            public BackpackOpenData getScreenOpeningData(ServerPlayerEntity p) {
                return new BackpackOpenData(tier.ordinal(), invSlot);
            }

            @Override
            public Text getDisplayName() {
                return stack.getName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                return new BackpackScreenHandler(syncId, inv, tier, BackpackInventory.load(stack, tier), invSlot, stack);
            }
        });
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.properbagpacks.slots", tier.slots(), tier.upgradeSlots())
                .formatted(Formatting.GRAY));
    }
}
