package dev.anvilcraft.resource.anisum.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC==11605 && FORGE
//$$ import java.util.List;
//#endif

@Mixin(LootPool.class)
public interface LootPoolAccessor {
    @Accessor
    //#if MC==11605 && FORGE
    //$$ List<LootPoolEntryContainer> getEntries();
    //#else
    LootPoolEntryContainer[] getEntries();
    //#endif
}
