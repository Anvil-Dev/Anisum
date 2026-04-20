package dev.anvilcraft.resource.anisum.network;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import javax.annotation.Nullable;

public record RegistryItemHolder<T>(@Nullable ResourceLocation identifier, T value) {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegistryItemHolder<?>(ResourceLocation identifier1, Object value1))) return false;
        return Objects.equals(this.identifier(), identifier1) && this.value().getClass() == value1.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier()) & Objects.hashCode(this.value().getClass());
    }
}
