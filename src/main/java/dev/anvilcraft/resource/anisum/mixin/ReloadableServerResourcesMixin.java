package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.extension.IReloadableServerResourcesExtension;
import dev.anvilcraft.resource.anisum.feat.AnisumConfigManager;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ReloadableServerResources.class)
abstract class ReloadableServerResourcesMixin implements IReloadableServerResourcesExtension {
    @Unique
    private final AnisumConfigManager anisum$configManager = new AnisumConfigManager((ReloadableServerResources) (Object) this);

    @Override
    public AnisumConfigManager anisum$getConfigManager() {
        return this.anisum$configManager;
    }
}
