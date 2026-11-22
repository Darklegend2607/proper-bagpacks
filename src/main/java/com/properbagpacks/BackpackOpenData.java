package com.properbagpacks;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BackpackOpenData(int tier, int lockedSlot) {
    public static final PacketCodec<RegistryByteBuf, BackpackOpenData> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, BackpackOpenData::tier,
            PacketCodecs.VAR_INT, BackpackOpenData::lockedSlot,
            BackpackOpenData::new);
}
