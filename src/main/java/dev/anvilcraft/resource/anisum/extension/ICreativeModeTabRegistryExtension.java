package dev.anvilcraft.resource.anisum.extension;

import dev.anvilcraft.resource.anisum.mixin.CreativeModeTabRegistryAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public interface ICreativeModeTabRegistryExtension {
    Map<String, IModInfo> MOD_INFO_MAP = new HashMap<>();

    static void sortTabs() {
        Registry<CreativeModeTab> tabRegistry = BuiltInRegistries.CREATIVE_MODE_TAB;
        List<CreativeModeTab> sortedTabs = new ArrayList<>();
        tabRegistry.forEach(sortedTabs::add);
        sortedTabs.removeIf(CreativeModeTabRegistryAccessor.getDefaultTabs()::contains);
        sortedTabs.sort(ICreativeModeTabRegistryExtension::sort);
        CreativeModeTabRegistryAccessor.setCreativeModeTabOrder(sortedTabs);
    }

    static int sort(CreativeModeTab tab1, CreativeModeTab tab2) {
        if (tab1 == tab2) return 0;
        Registry<CreativeModeTab> tabRegistry = BuiltInRegistries.CREATIVE_MODE_TAB;
        ResourceLocation key1 = tabRegistry.getKey(tab1);
        ResourceLocation key2 = tabRegistry.getKey(tab2);
        if (key1 == null || key2 == null) {
            return -1;
        }

        String namespace1 = key1.getNamespace();
        String namespace2 = key2.getNamespace();

        // 优先级1：Minecraft官方模组排在最前
        if (namespace1.equals("minecraft") && !namespace2.equals("minecraft")) {
            return -1;
        }
        if (!namespace1.equals("minecraft") && namespace2.equals("minecraft")) {
            return 1;
        }

        // 优先级2：显式的tabsAfter/tabsBefore关系
        if (tab1.tabsAfter.contains(key2) || tab2.tabsBefore.contains(key1)) {
            return -1;
        }
        if (tab2.tabsAfter.contains(key1) || tab1.tabsBefore.contains(key2)) {
            return 1;
        }

        // 优先级3：模组依赖关系 - 附属模组排在父模组后面
        int depResult = compareByModDependency(namespace1, namespace2);
        if (depResult != 0) {
            return depResult;
        }

        // 优先级4：按名字字母顺序排序
        String name1 = key1.toString();
        String name2 = key2.toString();
        if (Objects.equals(name1, name2)) return 0;
        return Objects.compare(name1, name2, String::compareTo);
    }

    /**
     * 根据模组依赖关系比较
     * 如果mod1是mod2的附属模组（mod1依赖mod2），则返回1（mod1排在后面）
     * 如果mod2是mod1的附属模组（mod2依赖mod1），则返回-1（mod2排在后面）
     * 否则返回0
     */
    static int compareByModDependency(String modId1, String modId2) {
        if (MOD_INFO_MAP.isEmpty()) {
            ModList.get().getMods().forEach(iModInfo -> MOD_INFO_MAP.put(iModInfo.getModId(), iModInfo));
        }

        IModInfo modInfo1 = MOD_INFO_MAP.get(modId1);
        IModInfo modInfo2 = MOD_INFO_MAP.get(modId2);

        if (modInfo1 == null || modInfo2 == null) {
            return 0;
        }

        // 检查modId1是否依赖modId2
        boolean mod1DependsOnMod2 = modInfo1.getDependencies().stream()
            .anyMatch(ICreativeModeTabRegistryExtension.isSubMod(modId2));

        // 检查modId2是否依赖modId1
        boolean mod2DependsOnMod1 = modInfo2.getDependencies().stream()
            .anyMatch(ICreativeModeTabRegistryExtension.isSubMod(modId1));

        // mod1是mod2的附属模组 -> mod1排后面（返回1）
        if (mod1DependsOnMod2) {
            return 1;
        }

        // mod2是mod1的附属模组 -> mod2排后面（返回-1）
        if (mod2DependsOnMod1) {
            return -1;
        }

        return 0;
    }

    static Predicate<IModInfo.ModVersion> isSubMod(String id) {
        return dep -> Objects.equals(dep.getModId(), id) &&
                      dep.getType() == IModInfo.DependencyType.REQUIRED;
    }
}
