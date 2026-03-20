package dev.anvilcraft.resource.anisum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.resource.anisum.utils.VersionUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @param include 要包含的战利品表
 *                </p>
 *                支持以下格式：
 *                完整路径：namespace:path1/path2/location
 *                通配符：namespace:path1/path2/*_cell
 *                正则表达式：namespace:path1/path2/.*_cell
 * @param sort    战利品表的顺序定义
 *                </p>
 *                支持以下格式：
 *                完整路径：namespace:path1/path2/location
 *                通配符：namespace:path1/path2/*_cell
 *                正则表达式：namespace:path1/path2/.*_cell
 */
public record AnisumConfig(
    boolean inline,
    Identifier location,
    Component name,
    ItemStack icon,
    List<String> include,
    List<String> sort
) implements Comparable<AnisumConfig> {
    public static final MapCodec<AnisumConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Identifier.CODEC.fieldOf("location").forGetter(AnisumConfig::location),
        ComponentSerialization.CODEC.fieldOf("name").forGetter(AnisumConfig::name),
        ItemStack.CODEC.optionalFieldOf("icon", ItemStack.EMPTY).forGetter(AnisumConfig::icon),
        Codec.list(Codec.STRING).optionalFieldOf("include", VersionUtil.listOf()).forGetter(AnisumConfig::include),
        Codec.list(Codec.STRING).optionalFieldOf("sort", VersionUtil.listOf()).forGetter(AnisumConfig::sort)
    ).apply(instance, AnisumConfig::new));

    public static final Codec<AnisumConfig> CODEC = AnisumConfig.MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, AnisumConfig> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        AnisumConfig::location,
        ComponentSerialization.STREAM_CODEC,
        AnisumConfig::name,
        ItemStack.STREAM_CODEC,
        AnisumConfig::icon,
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
        AnisumConfig::include,
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
        AnisumConfig::sort,
        AnisumConfig::new
    );

    public AnisumConfig(Identifier location, Component name, ItemStack icon, List<String> include, List<String> sort) {
        this(false, location, name, icon, include, sort);
    }

    public boolean includeNamespace(Identifier location) {
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

    public boolean include(Identifier location) {
        for (String pattern : this.include) {
            if (matches(pattern, location)) {
                return true;
            }
        }
        return false;
    }

    public int sort(Identifier location1, Identifier location2) {
        if (location1.equals(location2)) return 0;
        int index1 = findMatchIndex(location1);
        int index2 = findMatchIndex(location2);
        if (index1 != index2) {
            return Integer.compare(index1, index2);
        }
        return location1.toString().compareTo(location2.toString());
    }

    private int findMatchIndex(Identifier location) {
        for (int i = 0; i < this.sort.size(); i++) {
            if (matches(this.sort.get(i), location)) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    private static boolean matches(String pattern, Identifier location) {
        String locStr = location.toString();
        if (pattern.equals(locStr)) return true;
        int patternPathCount = pattern.split("/").length - 1;
        int locPathCount = locStr.split("/").length - 1;
        if (patternPathCount != locPathCount) return false;
        try {
            String regex;
            if ((pattern.contains("*") || pattern.contains("?")) && !(pattern.contains(".*") || pattern.contains("."))) {
                regex = Pattern.quote(pattern).replace("*", "\\E.*\\Q").replace("?", "\\E.\\Q");
            } else {
                regex = pattern;
            }
            return locStr.matches(regex);
        } catch (Exception e) {
            return false;
        }
    }

    public static AnisumConfig createInlineConfig(Identifier location) {
        location = VersionUtil.fromNamespaceAndPath(location.getNamespace(), location.getNamespace());
        return new AnisumConfig(
            true,
            location,
            VersionUtil.translatable(String.format("itemGroup.%s.%s", location.getNamespace(), location.getPath())),
            ItemStack.EMPTY,
            VersionUtil.listOf(String.format("%s:*", location.getNamespace())),
            VersionUtil.listOf()
        );
    }

    @Override
    public int compareTo(AnisumConfig obj) {
        if (this.inline != obj.inline) {
            return this.inline ? 1 : -1;
        }
        int i = this.name.toString().compareTo(obj.name.toString());
        if (i != 0) return i;
        if (this.equals(obj)) return 0;
        return this.hashCode() - obj.hashCode();
    }
}
