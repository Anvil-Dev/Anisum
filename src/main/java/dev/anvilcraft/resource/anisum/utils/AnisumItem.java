package dev.anvilcraft.resource.anisum.utils;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record AnisumItem(Identifier identifier, ItemStack itemStack) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AnisumItem> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        AnisumItem::identifier,
        ItemStack.STREAM_CODEC,
        AnisumItem::itemStack,
        AnisumItem::new
    );
}
