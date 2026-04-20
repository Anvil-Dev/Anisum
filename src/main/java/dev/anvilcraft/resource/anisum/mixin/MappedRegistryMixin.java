package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.extension.IMappedRegistryExtension;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(MappedRegistry.class)
@SuppressWarnings("UnstableApiUsage")
abstract class MappedRegistryMixin<T> extends BaseMappedRegistry<T> implements IMappedRegistryExtension {
    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow
    @Final
    private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> byValue;

    @Shadow
    @Final
    private ObjectList<Holder.Reference<T>> byId;

    @Shadow
    @Final
    private Reference2IntMap<T> toId;

    @Shadow
    @Final
    private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;

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
