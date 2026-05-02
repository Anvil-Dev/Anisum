package dev.anvilcraft.resource.anisum.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CreativeModeTabRegistry.class)
public interface CreativeModeTabRegistryAccessor {
    @Accessor("DEFAULT_TABS")
    static List<CreativeModeTab> getDefaultTabs() {
        throw new AssertionError("Not implemented");
    }

    @Invoker("setCreativeModeTabOrder")
    static void setCreativeModeTabOrder(List<CreativeModeTab> tierList) {
        throw new AssertionError("Not implemented");
    }
}
