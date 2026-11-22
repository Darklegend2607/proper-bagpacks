package com.properbagpacks;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record OpenWornPayload() implements CustomPayload {
    public static final CustomPayload.Id<OpenWornPayload> ID = new CustomPayload.Id<>(ProperBagPacks.id("open_worn"));
    public static final PacketCodec<RegistryByteBuf, OpenWornPayload> CODEC = PacketCodec.unit(new OpenWornPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
