package dev.anvilcraft.resource.anisum.network;

import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.utils.AnisumItem;
import dev.anvilcraft.resource.anisum.utils.ByteBufCodecsUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record AnisumTabSyncPayload(
    ResourceLocation identifier,
    Component name,
    ItemStack icon,
    Set<AnisumItem> items
) implements CustomPacketPayload {
    public static final Type<AnisumTabSyncPayload> TYPE = new Type<>(Anisum.of("tab_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnisumTabSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        AnisumTabSyncPayload::identifier,
        ComponentSerialization.STREAM_CODEC,
        AnisumTabSyncPayload::name,
        ItemStack.STREAM_CODEC,
        AnisumTabSyncPayload::icon,
        AnisumItem.STREAM_CODEC.apply(ByteBufCodecsUtil.set()),
        AnisumTabSyncPayload::items,
        AnisumTabSyncPayload::new
    );

    @Override
    public Type<AnisumTabSyncPayload> type() {
        return AnisumTabSyncPayload.TYPE;
    }
}
