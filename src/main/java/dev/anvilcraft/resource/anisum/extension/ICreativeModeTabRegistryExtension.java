package dev.anvilcraft.resource.anisum.extension;

import dev.anvilcraft.resource.anisum.mixin.CreativeModeTabRegistryAccessor;
import dev.anvilcraft.resource.anisum.utils.RegistryItemHolder;
import dev.anvilcraft.resource.anisum.utils.SortingTabHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public interface ICreativeModeTabRegistryExtension {
    static void sortTabs(List<RegistryItemHolder<CreativeModeTab>> lastedSortedTabs) {
        Map<String, SortingTabHolder> sortingMap = new TreeMap<>(ICreativeModeTabRegistryExtension::sortingMapSort);
        for (RegistryItemHolder<CreativeModeTab> lastedSortedTab : lastedSortedTabs) {
            ResourceLocation identifier = lastedSortedTab.identifier();
            String namespace = identifier.getNamespace();
            SortingTabHolder sortingTabHolder = sortingMap.computeIfAbsent(namespace, SortingTabHolder::create);
            sortingTabHolder.addTab(lastedSortedTab);
        }
        Registry<CreativeModeTab> tabRegistry = BuiltInRegistries.CREATIVE_MODE_TAB;
        for (Map.Entry<ResourceKey<CreativeModeTab>, CreativeModeTab> entry : tabRegistry.entrySet()) {
            ResourceLocation identifier = entry.getKey().location();
            RegistryItemHolder<CreativeModeTab> tabHolder = new RegistryItemHolder<>(entry.getKey().location(), entry.getValue());
            String namespace = identifier.getNamespace();
            SortingTabHolder sortingTabHolder = sortingMap.computeIfAbsent(namespace, SortingTabHolder::create);
            sortingTabHolder.addTab(tabHolder);
        }

        for (IModInfo mod : ModList.get().getMods()) {
            String namespace = mod.getNamespace();
            SortingTabHolder sortingTabHolder = sortingMap.computeIfAbsent(namespace, SortingTabHolder::create);
            for (IModInfo.ModVersion dependency : mod.getDependencies()) {
                String modId = dependency.getModId();
                if (dependency.getType() != IModInfo.DependencyType.REQUIRED) continue;
                SortingTabHolder sortingTabHolder1 = sortingMap.computeIfAbsent(modId, SortingTabHolder::create);
                sortingTabHolder1.addChildren(sortingTabHolder);
            }
        }


        Set<CreativeModeTab> sortedTabs = new LinkedHashSet<>();
        sortingMap.forEach((ignored, tabHolder) -> tabHolder.addAll(sortedTabs));
        List<CreativeModeTab> resultTabs = new ArrayList<>(sortedTabs);
        CreativeModeTabRegistryAccessor.setCreativeModeTabOrder(resultTabs);
    }

    static int sortingMapSort(String modId1, String modId2) {
        if (Objects.equals(modId1, ResourceLocation.DEFAULT_NAMESPACE) && !Objects.equals(modId2, ResourceLocation.DEFAULT_NAMESPACE)) {
            return -1;
        }
        if (Objects.equals(modId2, ResourceLocation.DEFAULT_NAMESPACE) && !Objects.equals(modId1, ResourceLocation.DEFAULT_NAMESPACE)) {
            return 1;
        }
        return Objects.compare(modId1, modId2, String::compareTo);
    }
}
