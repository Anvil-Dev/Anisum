package dev.anvilcraft.resource.anisum.network;

import net.minecraft.resources.Identifier;

public record RegistryItemHolder<T>(Identifier identifier, T value) {
}
