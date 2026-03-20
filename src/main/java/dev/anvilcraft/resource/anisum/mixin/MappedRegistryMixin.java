package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.extension.IMappedRegistryExtension;
import dev.anvilcraft.resource.anisum.network.RegistryItemHolder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@Mixin(MappedRegistry.class)
@SuppressWarnings("UnstableApiUsage")
abstract class MappedRegistryMixin<T> extends BaseMappedRegistry<T> implements IMappedRegistryExtension<T> {
    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow
    @Final
    private Map<Identifier, Holder.Reference<T>> byLocation;

    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> byValue;

    @Shadow
    @Final
    private ObjectList<Holder.Reference<T>> byId;

    @Shadow
    public abstract Optional<Holder.Reference<T>> get(int p_205907_);

    @Shadow
    @Final
    private Reference2IntMap<T> toId;

    @Shadow
    public abstract int getId(@Nullable T p_122706_);

    @Shadow
    @Final
    private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;

    @Override
    public void anisum$remove(RegistryItemHolder<T> holder) {
        Identifier identifier = holder.identifier();
        T value = holder.value();
        Optional<Holder.Reference<T>> reference = this.get(identifier);
        if (reference.isEmpty()) return;
        int id = this.getId(value);
        ResourceKey<T> key = ResourceKey.create(this.key(), identifier);
        this.byKey.remove(key);
        this.byLocation.remove(identifier);
        this.byValue.remove(value);
        this.byId.remove(reference.get());
        this.toId.remove(value, id);
        this.registrationInfos.remove(key);
    }

    @Override
    public void anisum$clear() {
        this.byKey.clear();
        this.byLocation.clear();
        this.byValue.clear();
        this.byId.clear();
        this.toId.clear();
        this.registrationInfos.clear();
    }
}
