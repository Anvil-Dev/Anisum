package dev.anvilcraft.resource.anisum.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = CreativeModeTabRegistry.class, remap = false)
abstract class CreativeModeTabRegistryMixin {
    @WrapOperation(
        method = "sortTabs",
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
    )
    private static <E> boolean listAdd(List<E> instance, E e, Operation<Boolean> original) {
        if (!instance.contains(e)) {
            return original.call(instance, e);
        }
        return false;
    }
}
