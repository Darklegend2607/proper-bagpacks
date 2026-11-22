package com.properbagpacks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Saved backpack data. Stack counts are stored separately from the item so
 * that counts above 99 (stack upgrades) are saved correctly.
 */
public record BackpackContents(List<Entry> items, List<Entry> upgrades) {

    public record Entry(int slot, int count, ItemStack stack) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("slot").forGetter(Entry::slot),
                Codec.INT.fieldOf("count").forGetter(Entry::count),
                ItemStack.CODEC.fieldOf("item").forGetter(Entry::stack)
        ).apply(i, Entry::new));

        @Override
        public boolean equals(Object o) {
            return o instanceof Entry e && slot == e.slot && count == e.count && ItemStack.areEqual(stack, e.stack);
        }

        @Override
        public int hashCode() {
            int h = 31 * slot + count;
            h = 31 * h + stack.getItem().hashCode();
            h = 31 * h + stack.getComponents().hashCode();
            return h;
        }
    }

    public static final Codec<BackpackContents> CODEC = RecordCodecBuilder.create(i -> i.group(
            Entry.CODEC.listOf().fieldOf("items").forGetter(BackpackContents::items),
            Entry.CODEC.listOf().fieldOf("upgrades").forGetter(BackpackContents::upgrades)
    ).apply(i, BackpackContents::new));
}
