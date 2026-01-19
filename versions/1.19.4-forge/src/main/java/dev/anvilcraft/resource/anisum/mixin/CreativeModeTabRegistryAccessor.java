package dev.anvilcraft.resource.anisum.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeModeTabRegistry.class)
public interface CreativeModeTabRegistryAccessor {
    @Accessor
    static BiMap<ResourceLocation, CreativeModeTab> getCreativeModeTabs() {
        throw new AssertionError();
    }

    @Invoker
    static void invokeRecalculateItemCreativeModeTabs() {
        throw new AssertionError();
    }
}
