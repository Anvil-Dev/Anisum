package dev.anvilcraft.resource.anisum.client.tab;

import com.mojang.blaze3d.platform.Window;
import dev.anvilcraft.resource.anisum.client.compat.BetterCreativeTabsCompat;
import dev.anvilcraft.resource.anisum.event.AnisumTabClearEvent;
import dev.anvilcraft.resource.anisum.event.AnisumTabLoadedEvent;
import dev.anvilcraft.resource.anisum.client.extension.ICreativeModeTabRegistryExtension;
import dev.anvilcraft.resource.anisum.network.pyload.AnisumTabSyncPayload;
import dev.anvilcraft.resource.anisum.client.util.RegistryItemHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CreativeModeTabManager {
    private final List<AnisumTabSyncPayload> payloads = new ArrayList<>();
    private final List<AnisumCreativeModeTab> creativeModeTabs = new ArrayList<>();
    @Getter
    private boolean loading = false;
    @Setter
    private int count = -1;

    public void addPayload(AnisumTabSyncPayload payload) {
        this.payloads.add(payload);
    }

    public boolean isSuccessful() {
        if (this.count < 0) return false;
        return this.payloads.size() >= this.count;
    }

    public void end() {
        this.loading = true;
        NeoForge.EVENT_BUS.post(new AnisumTabClearEvent(Collections.unmodifiableList(this.creativeModeTabs)));
        this.creativeModeTabs.clear();
        Registry<CreativeModeTab> tabRegistry = BuiltInRegistries.CREATIVE_MODE_TAB;
        List<RegistryItemHolder<CreativeModeTab>> lastedSortedTabs = new ArrayList<>();
        for (CreativeModeTab tab : CreativeModeTabRegistry.getSortedCreativeModeTabs()) {
            if (tab instanceof AnisumCreativeModeTab) continue;
            Identifier key = tabRegistry.getKey(tab);
            if (key == null) continue;
            lastedSortedTabs.add(new RegistryItemHolder<>(key, tab));
        }
        if (tabRegistry instanceof MappedRegistry<CreativeModeTab> mappedTabRegistry) {
            //noinspection deprecation
            mappedTabRegistry.unfreeze(true);
            List<RegistryItemHolder<CreativeModeTab>> tabs = new ArrayList<>();
            for (Identifier identifier : mappedTabRegistry.keySet()) {
                CreativeModeTab value = mappedTabRegistry.getValue(identifier);
                if (value == null || value instanceof AnisumCreativeModeTab) {
                    continue;
                }
                tabs.add(new RegistryItemHolder<>(identifier, value));
            }
            mappedTabRegistry.anisum$clear();
            for (RegistryItemHolder<CreativeModeTab> tab : tabs) {
                Registry.register(mappedTabRegistry, ResourceKey.create(Registries.CREATIVE_MODE_TAB, tab.identifier()), tab.value());
            }
            Minecraft minecraft = Minecraft.getInstance();
            for (AnisumTabSyncPayload payload : this.payloads) {
                AnisumCreativeModeTab tab = new AnisumCreativeModeTab(
                    CreativeModeTab.builder()
                        .title(payload.name())
                        .icon(payload.icon()::create)
                        .withTabsBefore(CreativeModeTabs.TOOLS_AND_UTILITIES),
                    payload.items()
                );
                this.creativeModeTabs.add(tab);
                log.info("Registering anisum creative mode tab: {} with {} items", payload.identifier(), payload.items().size());
                Registry.register(
                    mappedTabRegistry,
                    ResourceKey.create(Registries.CREATIVE_MODE_TAB, payload.identifier()),
                    tab
                );
            }
            mappedTabRegistry.freeze();
            ICreativeModeTabRegistryExtension.sortTabs(lastedSortedTabs);
            BetterCreativeTabsCompat.invalidateCreativeIndex();
            if (minecraft.screen instanceof CreativeModeInventoryScreen screen) {
                Window window = minecraft.getWindow();
                screen.init(window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
            NeoForge.EVENT_BUS.post(new AnisumTabLoadedEvent(Collections.unmodifiableList(this.creativeModeTabs)));
        }
        this.count = -1;
        this.payloads.clear();
        this.loading = false;
    }
}
