package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.annotations.Side;
import dev.anvilcraft.resource.anisum.extension.IReloadableServerResourcesExtension;
import dev.anvilcraft.resource.anisum.feat.AnisumConfigManager;
import dev.anvilcraft.resource.anisum.utils.SideDist;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Side(SideDist.SERVER)
@Mixin(ReloadableServerResources.class)
abstract class ReloadableServerResourcesMixin implements IReloadableServerResourcesExtension {
    @Unique
    private final AnisumConfigManager anisum$configManager = new AnisumConfigManager((ReloadableServerResources) (Object) this);

    @Override
    public AnisumConfigManager anisum$getConfigManager() {
        return this.anisum$configManager;
    }
}
