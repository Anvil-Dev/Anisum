package dev.anvilcraft.resource.anisum.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListArrayUtil {
    public static <T> List<T> of(List<T> tList) {
        return tList;
    }

    public static <T> List<T> of(T[] tList) {
        return new ArrayList<>(Arrays.asList(tList));
    }
}
