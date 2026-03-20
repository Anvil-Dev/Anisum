package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.extension.ILootPoolExtension;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPool.class)
abstract class LootPoolMixin implements ILootPoolExtension {
    @Override
    @Accessor("entries")
    public abstract List<LootPoolEntryContainer> anisum$getEntries();
}
