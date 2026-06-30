package dev.anvilcraft.resource.anisum.client.compat;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@Slf4j
public final class BetterCreativeTabsCompat {
    private static final String CREATIVE_TAB_GROUP = "dev.xkmc.better_creative_tabs.creative.CreativeTabGroup";
    private static final String CREATIVE_TAB_LIST = "dev.xkmc.better_creative_tabs.creative.CreativeTabList";
    private static final String CREATIVE_INDEX_SCREEN = "dev.xkmc.better_creative_tabs.creative.CreativeIndexScreen";

    private BetterCreativeTabsCompat() {
    }

    public static void invalidateCreativeIndex() {
        try {
            setStaticFieldToNull(CREATIVE_TAB_GROUP, "GROUPS");
            setStaticFieldToNull(CREATIVE_TAB_LIST, "TABS");
            refreshOpenIndexScreen();
        } catch (ClassNotFoundException ignored) {
            // Better Creative Tabs is optional.
        } catch (ReflectiveOperationException | LinkageError e) {
            log.warn("Failed to invalidate Better Creative Tabs creative index cache", e);
        }
    }

    private static void setStaticFieldToNull(String className, String fieldName) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(className);
        Field field = clazz.getField(fieldName);
        field.set(null, null);
    }

    private static void refreshOpenIndexScreen() throws ReflectiveOperationException {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen == null || !screen.getClass().getName().equals(CREATIVE_INDEX_SCREEN)) {
            return;
        }

        Class<?> screenClass = Class.forName(CREATIVE_INDEX_SCREEN);
        Constructor<?> constructor = screenClass.getConstructor(int.class);
        minecraft.setScreen((Screen) constructor.newInstance(0));
    }
}
