package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.extension.IMinecraftExtension;
import dev.anvilcraft.resource.anisum.item.CreativeModeTabManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        CreativeModeTabManager tabManager = ((IMinecraftExtension) Minecraft.getInstance()).anisum$getCreativeModeTabManager();
        if (tabManager.isLoaded()) {
            tabManager.setLoaded(false);
        }
    }
}
