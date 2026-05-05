package dev.anvilcraft.resource.anisum.utils;

import net.minecraft.resources.Identifier;

public record RegistryItemHolder<T>(Identifier identifier, T value) {
}
