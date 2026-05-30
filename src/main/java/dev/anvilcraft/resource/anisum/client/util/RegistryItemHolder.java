package dev.anvilcraft.resource.anisum.client.util;

import net.minecraft.resources.Identifier;

public record RegistryItemHolder<T>(Identifier identifier, T value) {
}
