package com.properbagpacks;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final Map<BackpackTier, BackpackItem> BACKPACKS = new EnumMap<>(BackpackTier.class);
    public static final List<BackpackUpgradeItem> STACK_UPGRADES = new ArrayList<>();
    public static BackpackUpgradeItem AUTO_FEEDER;

    public static void register() {
        for (BackpackTier tier : BackpackTier.values()) {
            Item.Settings settings = new Item.Settings().maxCount(1);
            if (tier == BackpackTier.NETHERITE) {
                settings = settings.fireproof();
            }
            BACKPACKS.put(tier, Registry.register(Registries.ITEM,
                    ProperBagPacks.id(tier.id() + "_backpack"), new BackpackItem(tier, settings)));
        }

        for (int i = 1; i <= 4; i++) {
            STACK_UPGRADES.add(Registry.register(Registries.ITEM,
                    ProperBagPacks.id("stack_upgrade_" + i),
                    new BackpackUpgradeItem(UpgradeKind.STACK, 1 << i, new Item.Settings().maxCount(1))));
        }

        AUTO_FEEDER = Registry.register(Registries.ITEM, ProperBagPacks.id("auto_feeder_upgrade"),
                new BackpackUpgradeItem(UpgradeKind.FEEDER, 1, new Item.Settings().maxCount(1)));

        Registry.register(Registries.ITEM_GROUP, ProperBagPacks.id("main"), FabricItemGroup.builder()
                .icon(() -> new ItemStack(BACKPACKS.get(BackpackTier.GOLD)))
                .displayName(Text.translatable("itemGroup.properbagpacks.main"))
                .entries((context, entries) -> {
                    BACKPACKS.values().forEach(entries::add);
                    STACK_UPGRADES.forEach(entries::add);
                    entries.add(AUTO_FEEDER);
                })
                .build());
    }
}
