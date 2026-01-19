package dev.anvilcraft.resource.anisum.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class ListArrayUtil {
    public static @Nonnull <T> List<T> of(@Nonnull List<T> tList) {
        return tList;
    }

    public static @Nonnull <T> List<T> of(@Nonnull T[] tList) {
        return new ArrayList<>(Arrays.asList(tList));
    }
}
