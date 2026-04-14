package dev.anvilcraft.resource.anisum.network;

import net.minecraft.resources.ResourceLocation;

public record RegistryItemHolder<T>(ResourceLocation identifier, T value) {
}
