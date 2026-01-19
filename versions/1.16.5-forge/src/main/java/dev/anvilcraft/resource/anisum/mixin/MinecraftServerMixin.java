package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.AnisumPlatform;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {
    @Shadow
    public abstract LootTables getLootTables();

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void endResourceReload(Collection<String> collection, @Nonnull CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().handleAsync(
            (value, throwable) -> {
                AnisumPlatform.endDataPackReload((MinecraftServer) (Object) this, this.getLootTables());
                return value;
            }, (MinecraftServer) (Object) this
        );
    }
}
