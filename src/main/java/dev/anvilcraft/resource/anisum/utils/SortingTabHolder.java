package dev.anvilcraft.resource.anisum.utils;

import dev.anvilcraft.resource.anisum.extension.ICreativeModeTabRegistryExtension;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public record SortingTabHolder(
    String identifier,
    Set<RegistryItemHolder<CreativeModeTab>> tabs,
    Set<SortingTabHolder> children
) {
    public void addTab(RegistryItemHolder<CreativeModeTab> tab) {
        if (CreativeModeTabRegistry.getDefaultTabs().contains(tab.value())) return;
        this.tabs().add(tab);
    }

    public void addChildren(SortingTabHolder child) {
        this.children().add(child);
    }

    public void addAll(Collection<CreativeModeTab> tabs) {
        this.tabs().forEach(holder -> tabs.add(holder.value()));
        this.children().forEach(child -> child.addAll(tabs));
    }

    public static SortingTabHolder create(String identifier) {
        if (Objects.equals(identifier, Identifier.DEFAULT_NAMESPACE)) {
            return SortingTabHolder.createLinked(identifier);
        }
        return SortingTabHolder.createTree(identifier);
    }

    public static SortingTabHolder createTree(String identifier) {
        return new SortingTabHolder(
            identifier,
            new TreeSet<>(SortingTabHolder::sort),
            new TreeSet<>(SortingTabHolder::sortingById)
        );
    }

    public static SortingTabHolder createLinked(String identifier) {
        return new SortingTabHolder(
            identifier,
            new LinkedHashSet<>(),
            new TreeSet<>(SortingTabHolder::sortingById)
        );
    }

    public static int sort(RegistryItemHolder<CreativeModeTab> tabHolder1, RegistryItemHolder<CreativeModeTab> tabHolder2) {
        if (Objects.equals(tabHolder1, tabHolder2)) return 0;
        CreativeModeTab tab1 = tabHolder1.value();
        CreativeModeTab tab2 = tabHolder2.value();
        if (Objects.equals(tab1, tab2)) return 0;
        Identifier key1 = tabHolder1.identifier();
        Identifier key2 = tabHolder2.identifier();

        // 优先级1：显式的tabsAfter/tabsBefore关系
        if (tab1.tabsAfter.contains(key2) || tab2.tabsBefore.contains(key1)) {
            return -1;
        }
        if (tab2.tabsAfter.contains(key1) || tab1.tabsBefore.contains(key2)) {
            return 1;
        }

        // 优先级2：按名字字母顺序排序
        String name1 = key1.toString();
        String name2 = key2.toString();
        if (Objects.equals(name1, name2)) return 0;
        return Objects.compare(name1, name2, String::compareTo);
    }

    public static int sortingById(SortingTabHolder holder1, SortingTabHolder holder2) {
        String modId1 = holder1.identifier(), modId2 = holder2.identifier();
        return ICreativeModeTabRegistryExtension.sortingMapSort(modId1, modId2);
    }
}
