package dev.anvilcraft.resource.anisum;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ClassCanBeRecord")
public class AnisumConfig implements Comparable<AnisumConfig> {
    public final boolean inline;
    public final Component name;
    public final @Nullable ItemStack icon;
    /**
     * 要包含的战利品表
     * </p>
     * 支持以下格式：
     * 完整路径：namespace:path1/path2/location
     * 通配符：namespace:path1/path2/*_cell
     * 正则表达式：namespace:path1/path2/.*_cell
     */
    public final List<String> include;
    /**
     * 战利品表的顺序定义
     * </p>
     * 支持以下格式：
     * 完整路径：namespace:path1/path2/location
     * 通配符：namespace:path1/path2/*_cell
     * 正则表达式：namespace:path1/path2/.*_cell
     */
    public final List<String> sort;

    public AnisumConfig(Component name, @Nullable ItemStack icon, List<String> include, List<String> sort) {
        this(false, name, icon, include, sort);
    }

    public AnisumConfig(boolean inline, Component name, @Nullable ItemStack icon, List<String> include, List<String> sort) {
        this.inline = inline;
        this.name = name;
        this.icon = icon;
        this.include = include;
        this.sort = sort;
    }

    public boolean includeNamespace(ResourceLocation location) {
        for (String s : this.include) {
            String[] split = s.split(":");
            String namespace;
            if (split.length == 1) {
                namespace = "minecraft";
            } else {
                namespace = split[0];
            }
            if (namespace.equals(location.getNamespace())) {
                return true;
            }
        }
        return false;
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

    private static boolean matches(@Nonnull String pattern, @Nonnull ResourceLocation location) {
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

    public static @Nonnull AnisumConfig createInlineConfig(@Nonnull ResourceLocation location) {
        ArrayList<String> include = new ArrayList<>();
        include.add(String.format("%s:*", location.getNamespace()));
        //noinspection Java9CollectionFactory
        return new AnisumConfig(
            true,
            new TranslatableComponent(String.format("itemGroup.anisum.%s", location.getNamespace())),
            null,
            Collections.unmodifiableList(include),
            Collections.unmodifiableList(new ArrayList<>())
        );
    }

    @Override
    @SuppressWarnings("PatternVariableCanBeUsed")
    public boolean equals(Object obj) {
        if (!(obj instanceof AnisumConfig)) return false;
        AnisumConfig config = (AnisumConfig) obj;
        return this.inline == config.inline
               && this.name.equals(config.name)
               && Objects.equals(this.icon, config.icon)
               && this.include.equals(config.include)
               && this.sort.equals(config.sort);
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(this.inline)
               ^ this.name.hashCode()
               ^ Objects.hashCode(this.icon)
               ^ this.include.hashCode()
               ^ this.sort.hashCode();
    }

    @Override
    public int compareTo(@NotNull AnisumConfig obj) {
        if (this.inline != obj.inline) {
            return this.inline ? 1 : -1;
        }
        int i = this.name.toString().compareTo(obj.name.toString());
        if (i != 0) return i;
        if (this.equals(obj)) return 0;
        return this.hashCode() - obj.hashCode();
    }
}
