package dev.anvilcraft.resource.anisum.network.payload;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.extension.IMinecraftExtension;
import dev.anvilcraft.resource.anisum.item.CreativeModeTabManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record AnisumSyncStartPayload(int count) implements IClientboundPacket {
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

    @Override
    public void handleOnClient(Player player) {
        CreativeModeTabManager creativeModeTabManager = ((IMinecraftExtension) Minecraft.getInstance()).anisum$getCreativeModeTabManager();
        creativeModeTabManager.setCount(this.count());
        if (creativeModeTabManager.isSuccessful()) {
            creativeModeTabManager.end();
        }
    }
}
