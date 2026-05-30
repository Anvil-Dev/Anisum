package dev.anvilcraft.resource.anisum.mixin.client;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CreativeModeTabRegistry.class)
public interface CreativeModeTabRegistryAccessor {
    @Invoker("setCreativeModeTabOrder")
    static void setCreativeModeTabOrder(List<CreativeModeTab> tierList) {
        throw new AssertionError("Not implemented");
    }
}
