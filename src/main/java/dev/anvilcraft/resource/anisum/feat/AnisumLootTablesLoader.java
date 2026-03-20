package dev.anvilcraft.resource.anisum.feat;

import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.annotations.Side;
import dev.anvilcraft.resource.anisum.utils.SideDist;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Side(SideDist.SERVER)
public class AnisumLootTablesLoader {
    public static final Map<Identifier, Set<ItemStack>> ITEMS = new HashMap<>();

    public static void lootLoaded(MinecraftServer server) {
        Optional<? extends HolderLookup.RegistryLookup<LootTable>> lookup = server.reloadableRegistries()
            .lookup()
            .lookup(Registries.LOOT_TABLE);
        if (lookup.isEmpty()) return;
        Anisum.LOGGER.info("Processing loot tables");
        lookup.get().listElements().forEach(reference -> {
            ResourceKey<LootTable> key = reference.getKey();
            if (key == null) return;
            Identifier identifier = key.identifier();
            LootTable lootTable = reference.value();
        });
    }
}
