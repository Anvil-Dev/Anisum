package dev.anvilcraft.resource.anisum;

import com.mojang.datafixers.util.Pair;
import dev.anvilcraft.resource.anisum.mixin.LootPoolAccessor;
import dev.anvilcraft.resource.anisum.mixin.LootTableAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class AnisumFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(AnisumFabric::endDataPackReload);
        ServerLifecycleEvents.SERVER_STARTED.register(AnisumFabric::serverStarted);
    }

    private static void serverStarted(MinecraftServer server) {
        AnisumFabric.lootLoaded(server, server.getLootTables());
    }


    private static void endDataPackReload(
        @Nonnull MinecraftServer server,
        @Nonnull ServerResources serverResourceManager,
        boolean success
    ) {
        LootTables lootTables = serverResourceManager.getLootTables();
        AnisumFabric.lootLoaded(server, lootTables);
    }

    private static void lootLoaded(@Nonnull MinecraftServer server, @Nonnull LootTables lootTables) {
        LootContext context = new LootContext.Builder(server.overworld()).create(new LootContextParamSet.Builder().build());
        Set<ResourceLocation> ids = lootTables.getIds();
        Map<String, List<Pair<ResourceLocation, ItemStack>>> lootTableResults = new HashMap<>();
        for (ResourceLocation id : ids) {
            try {
                if (id.getPath().contains("/")) continue;
                LootTable lootTable = lootTables.get(id);
                LootTableAccessor tableAccessor = (LootTableAccessor) lootTable;
                LootPool[] pools = tableAccessor.getPools();
                if (pools.length != 1) continue;
                LootPool pool = pools[0];
                LootPoolAccessor poolAccessor = (LootPoolAccessor) pool;
                LootPoolEntryContainer[] entries = poolAccessor.getEntries();
                if (entries.length != 1) continue;
                LootPoolEntryContainer entry = entries[0];
                if (!(entry instanceof LootItem)) continue;
                lootTable.getRandomItems(
                    context,
                    stack -> lootTableResults.computeIfAbsent(id.getNamespace(), k -> new ArrayList<>()).add(Pair.of(id, stack))
                );
            } catch (Exception e) {
                Anisum.LOGGER.error("Anisum: Error while processing loot table {}", id, e);
                throw e;
            }
        }
        for (Map.Entry<String, List<Pair<ResourceLocation, ItemStack>>> entry : lootTableResults.entrySet()) {
            ResourceLocation location = Anisum.location(entry.getKey());
            String displayName = String.format("itemGroup.%s.%s", location.getNamespace(), location.getPath());
            CreativeModeTab tab = null;
            for (CreativeModeTab check : CreativeModeTab.TABS) {
                if (!(check.getDisplayName() instanceof TranslatableComponent)) continue;
                //noinspection PatternVariableCanBeUsed
                TranslatableComponent translatableComponent = (TranslatableComponent) check.getDisplayName();
                if (translatableComponent.getKey().equals(displayName)) {
                    tab = check;
                    break;
                }
            }
            entry.getValue().sort(Comparator.comparing(a -> a.getFirst().toString()));
            if (tab == null) {
                //noinspection SequencedCollectionMethodCanBeUsed
                tab = FabricItemGroupBuilder.create(location)
                    .icon(() -> entry.getValue().get(0).getSecond())
                    .appendItems(items -> items.addAll(entry.getValue()
                        .stream()
                        .map(Pair::getSecond)
                        .collect(Collectors.toCollection(ArrayList::new))))
                    .build();
            }
        }
    }
}
