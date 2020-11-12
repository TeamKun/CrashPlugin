package net.teamfruit.crashplugin;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSUtils {
    private static final String NMSVersion = getNMSVersion();

    public static String getNMSVersion() {
        String v = Bukkit.getServer().getClass().getPackage().getName();
        return v.substring(v.lastIndexOf('.') + 1);
    }

    public static Class<?> getNMSServerClass(String path) {
        try {
            return Class.forName(String.format("net.minecraft.server.%s.%s", NMSVersion, path));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getNMSBukkitClass(String path) {
        try {
            return Class.forName(String.format("org.bukkit.craftbukkit.%s.%s", NMSVersion, path));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getPrivateField(Class<?> clazz, String name) {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
