package dev.anvilcraft.resource.anisum.network.payload;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.extension.IMinecraftExtension;
import dev.anvilcraft.resource.anisum.item.CreativeModeTabManager;
import dev.anvilcraft.resource.anisum.utils.AnisumItem;
import dev.anvilcraft.resource.anisum.utils.ByteBufCodecsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record AnisumTabSyncPayload(
    ResourceLocation identifier,
    Component name,
    ItemStack icon,
    Set<AnisumItem> items
) implements IClientboundPacket {
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

    @Override
    public void handleOnClient(Player player) {
        CreativeModeTabManager creativeModeTabManager = ((IMinecraftExtension) Minecraft.getInstance()).anisum$getCreativeModeTabManager();
        creativeModeTabManager.addPayload(this);
        if (creativeModeTabManager.isSuccessful()) {
            creativeModeTabManager.end();
        }
    }
}
