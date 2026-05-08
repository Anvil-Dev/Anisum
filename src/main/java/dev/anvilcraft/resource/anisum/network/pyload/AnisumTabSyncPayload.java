package dev.anvilcraft.resource.anisum.network.pyload;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.item.CreativeModeTabManager;
import dev.anvilcraft.resource.anisum.utils.AnisumItem;
import dev.anvilcraft.resource.anisum.utils.ByteBufCodecsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Set;

public record AnisumTabSyncPayload(
    Identifier identifier,
    Component name,
    ItemStackTemplate icon,
    Set<AnisumItem> items
) implements IClientboundPacket {
    public static final Type<AnisumTabSyncPayload> TYPE = new Type<>(Anisum.of("tab_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnisumTabSyncPayload> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        AnisumTabSyncPayload::identifier,
        ComponentSerialization.STREAM_CODEC,
        AnisumTabSyncPayload::name,
        ItemStackTemplate.STREAM_CODEC,
        AnisumTabSyncPayload::icon,
        AnisumItem.STREAM_CODEC.apply(ByteBufCodecsUtil.set()),
        AnisumTabSyncPayload::items,
        AnisumTabSyncPayload::new
    );

    @Override
    public Type<AnisumTabSyncPayload> type() {
        return AnisumTabSyncPayload.TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        CreativeModeTabManager creativeModeTabManager = Minecraft.getInstance().anisum$getCreativeModeTabManager();
        creativeModeTabManager.addPayload(this);
        if (creativeModeTabManager.isSuccessful()) {
            creativeModeTabManager.end();
        }
    }
}
