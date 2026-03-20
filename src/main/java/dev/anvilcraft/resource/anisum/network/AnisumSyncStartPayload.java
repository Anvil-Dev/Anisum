package dev.anvilcraft.resource.anisum.network;

import dev.anvilcraft.resource.anisum.Anisum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AnisumSyncStartPayload(int count) implements CustomPacketPayload {
    public static final Type<AnisumSyncStartPayload> TYPE = new Type<>(Anisum.of("sync_start"));
    public static final StreamCodec<FriendlyByteBuf, AnisumSyncStartPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        AnisumSyncStartPayload::count,
        AnisumSyncStartPayload::new
    );

    @Override
    public Type<AnisumSyncStartPayload> type() {
        return AnisumSyncStartPayload.TYPE;
    }
}
