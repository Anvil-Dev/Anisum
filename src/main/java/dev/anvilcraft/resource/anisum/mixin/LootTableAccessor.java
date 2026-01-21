package dev.anvilcraft.resource.anisum.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if FORGE
//$$ import java.util.List;
//#endif

@Mixin(LootTable.class)
public interface LootTableAccessor {
    @Accessor
    //#if FORGE
    //$$ List<LootPool> getPools();
    //#else
    LootPool[] getPools();
    //#endif
}
