package dev.anvilcraft.resource.anisum.utils;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record AnisumItem(ResourceLocation identifier, ItemStack itemStack) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AnisumItem> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        AnisumItem::identifier,
        ItemStack.STREAM_CODEC,
        AnisumItem::itemStack,
        AnisumItem::new
    );
}
