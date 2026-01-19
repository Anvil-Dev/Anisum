package dev.anvilcraft.resource.anisum;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AnisumConfig extends ArrayList<AnisumConfig.AnisumConfigObject> {
    public static class AnisumConfigObject {
        public Component name;
        public ItemStack icon;
        /**
         * 要包含的战利品表
         * </p>
         * 支持以下格式：
         * 完整路径：namespace:path1/path2/location
         * 通配符：namespace:path1/path2/*_cell
         * 正则表达式：namespace:path1/path2/.*_cell
         */
        public List<String> include;
        /**
         * 战利品表的顺序定义
         * </p>
         * 支持以下格式：
         * 完整路径：namespace:path1/path2/location
         * 通配符：namespace:path1/path2/*_cell
         * 正则表达式：namespace:path1/path2/.*_cell
         */
        public List<String> sort;

        public List<String> getIncludeNamespace() {
            List<String> namespace = new ArrayList<>();
            for (String s : this.include) {
                String[] split = s.split(":");
                if (split.length == 1) {
                    namespace.add("minecraft");
                } else {
                    namespace.add(split[0]);
                }
            }
            return namespace;
        }

        public boolean include(ResourceLocation location) {
            if (this.include == null) return false;
            for (String pattern : this.include) {
                if (matches(pattern, location)) {
                    return true;
                }
            }
            return false;
        }

        public int sort(ResourceLocation location1, ResourceLocation location2) {
            int index1 = findMatchIndex(location1);
            int index2 = findMatchIndex(location2);
            if (index1 != index2) {
                return Integer.compare(index1, index2);
            }
            return location1.toString().compareTo(location2.toString());
        }

        private int findMatchIndex(ResourceLocation location) {
            if (this.sort == null) return Integer.MAX_VALUE;
            for (int i = 0; i < this.sort.size(); i++) {
                if (matches(this.sort.get(i), location)) {
                    return i;
                }
            }
            return Integer.MAX_VALUE;
        }

        private static boolean matches(@NotNull String pattern, @NotNull ResourceLocation location) {
            String locStr = location.toString();
            if (pattern.equals(locStr)) return true;

            try {
                String regex;
                if (pattern.contains("*") && !pattern.contains(".*")) {
                    regex = Pattern.quote(pattern).replace("*", "\\E.*\\Q");
                } else {
                    regex = pattern;
                }
                return locStr.matches(regex);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
