package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.extension.IMinecraftServerExtension;
import dev.anvilcraft.resource.anisum.extension.IReloadableServerResourcesExtension;
import dev.anvilcraft.resource.anisum.feat.AnisumConfigManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServerExtension {
    @Shadow
    public abstract MinecraftServer.ReloadableResources getServerResources();

    @Override
    public AnisumConfigManager anisum$getConfigManager() {
        return ((IReloadableServerResourcesExtension) this.getServerResources().managers()).anisum$getConfigManager();
    }
}
