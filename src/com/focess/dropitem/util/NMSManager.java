package com.focess.dropitem.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NMSManager {

    public final static Class<?> CraftEntity;
    // basic class
    public final static Class<?> CraftServer;
    public final static Class<?> CraftWorld;

    // NBT Builder
    private final static Method e;
    public final static Class<?> Entity;
    public final static Class<?> EntityPlayer;
    // getNBT from an entity
    private final static Method f;
    private final static Method getBoolean;
    // setNBT to an entity
    private final static Method getHandle;
    private final static Method hasKey;
    // basic
    private final static Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<>();

    private final static Map<String, Class<?>> loadedNMSClasses = new HashMap<>();
    public final static Class<?> MinecraftServer;

    public final static Class<?> NBTTagCompound;
    private final static Method setBoolean;
    private final static Method setInt;
    private static int versionInt = -1;
    private static String versionString;
    public final static Class<?> World;
    public final static Class<?> WorldServer;

    static {
        World = NMSManager.getNMSClass("World");
        MinecraftServer = NMSManager.getNMSClass("MinecraftServer");
        WorldServer = NMSManager.getNMSClass("WorldServer");
        CraftWorld = NMSManager.getCraftClass("CraftWorld");
        CraftServer = NMSManager.getCraftClass("CraftServer");
        EntityPlayer = NMSManager.getNMSClass("EntityPlayer");
        CraftEntity = NMSManager.getCraftClass("entity.CraftEntity");
        Entity = NMSManager.getNMSClass("Entity");
        NBTTagCompound = NMSManager.getNMSClass("NBTTagCompound");
        getHandle = NMSManager.getMethod(NMSManager.CraftEntity, "getHandle", new Class[] {});
        setInt = NMSManager.getMethod(NMSManager.NBTTagCompound, "setInt", new Class[] { String.class, Integer.TYPE });
        setBoolean = NMSManager.getMethod(NMSManager.NBTTagCompound, "setBoolean",
                new Class[] { String.class, Boolean.TYPE });
        hasKey = NMSManager.getMethod(NMSManager.NBTTagCompound, "hasKey", new Class[] { String.class });
        getBoolean = NMSManager.getMethod(NMSManager.NBTTagCompound, "getBoolean", new Class[] { String.class });
        f = NMSManager.getMethod(NMSManager.Entity, "f", new Class[] { NMSManager.NBTTagCompound });
        if (NMSManager.getVersionInt() < 12)
            e = NMSManager.getMethod(NMSManager.Entity, "e", new Class[] { NMSManager.NBTTagCompound });
        else
            e = NMSManager.getMethod(NMSManager.Entity, "save", new Class[] { NMSManager.NBTTagCompound });
    }

    public static Object getConnection(final Player player) {
        final Method getHandleMethod = NMSManager.getMethod(player.getClass(), "getHandle", new Class[0]);

        if (getHandleMethod != null)
            try {
                final Object nmsPlayer = getHandleMethod.invoke(player, new Object[0]);
                final Field playerConField = NMSManager.getField(nmsPlayer.getClass(), "playerConnection");
                return playerConField.get(nmsPlayer);
            } catch (final Exception e) {
                e.printStackTrace();
            }

        return null;
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>[] params) {
        try {
            return clazz.getConstructor(params);
        } catch (final NoSuchMethodException e) {
        }
        return null;
    }

    public static Class<?> getCraftClass(final String nmsClassName) {
        if (NMSManager.loadedNMSClasses.containsKey(nmsClassName))
            return NMSManager.loadedNMSClasses.get(nmsClassName);
        final String clazzName = "org.bukkit.craftbukkit." + NMSManager.getVersionString() + nmsClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (final Throwable t) {
            t.printStackTrace();
            return NMSManager.loadedNMSClasses.put(nmsClassName, null);
        }
        NMSManager.loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        if (!NMSManager.loadedFields.containsKey(clazz))
            NMSManager.loadedFields.put(clazz, new HashMap<String, Field>());
        final Map<String, Field> fields = NMSManager.loadedFields.get(clazz);
        if (fields.containsKey(fieldName))
            return fields.get(fieldName);
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            fields.put(fieldName, field);
            NMSManager.loadedFields.put(clazz, fields);
            return field;
        } catch (final Exception e) {
            e.printStackTrace();
            fields.put(fieldName, null);
            NMSManager.loadedFields.put(clazz, fields);
        }
        return null;
    }

    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>[] params) {
        if (!NMSManager.loadedMethods.containsKey(clazz))
            NMSManager.loadedMethods.put(clazz, new HashMap<String, Method>());
        final Map<String, Method> methods = NMSManager.loadedMethods.get(clazz);
        if (methods.containsKey(methodName) && methods.get(methodName).getParameterTypes().equals(params))
            return methods.get(methodName);
        try {
            final Method method = clazz.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            methods.put(methodName, method);
            NMSManager.loadedMethods.put(clazz, methods);
            return method;
        } catch (final Exception e) {
            e.printStackTrace();
            methods.put(methodName, null);
            NMSManager.loadedMethods.put(clazz, methods);
        }
        return null;
    }

    private static Object getNBT(final LivingEntity entity) {
        Object tag = null;
        try {
            final Object nmsEntity = NMSManager.getHandle.invoke(entity);
            tag = NMSManager.NBTTagCompound.newInstance(); 
            NMSManager.e.invoke(nmsEntity, tag);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

    public static boolean getNBTBoolean(final LivingEntity entity, final String nbtName) {
        try {
            final Object tag = NMSManager.getNBT(entity);
            return (boolean) NMSManager.getBoolean.invoke(tag, nbtName);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Class<?> getNMSClass(final String nmsClassName) {
        if (NMSManager.loadedNMSClasses.containsKey(nmsClassName))
            return NMSManager.loadedNMSClasses.get(nmsClassName);
        final String clazzName = "net.minecraft.server." + NMSManager.getVersionString() + nmsClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (final Throwable t) {
            t.printStackTrace();
            return NMSManager.loadedNMSClasses.put(nmsClassName, null);
        }
        NMSManager.loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static int getVersionInt() {
        if (NMSManager.versionInt == -1) {
            final String v = Bukkit.getServer().getClass().getPackage().getName();
            NMSManager.versionInt = Integer.parseInt(v.substring(v.lastIndexOf('.') + 1).split("_")[1]);
        }
        return NMSManager.versionInt;
    }

    public static String getVersionString() {
        if (NMSManager.versionString == null) {
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            NMSManager.versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
        }
        return NMSManager.versionString;
    }

    public static boolean hasNBT(final LivingEntity entity, final String nbtName) {
        try {
            final Object tag = NMSManager.getNBT(entity);
            return (boolean) NMSManager.hasKey.invoke(tag, nbtName);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void saveNBT(final LivingEntity entity, final Object tag) {
        try {
            final Object nmsEntity = NMSManager.getHandle.invoke(entity);
            NMSManager.f.invoke(nmsEntity, tag);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNBTBoolean(final LivingEntity entity, final String nbtName, final boolean value) {
        try {
            final Object tag = NMSManager.getNBT(entity);
            NMSManager.setBoolean.invoke(tag, nbtName, value);
            NMSManager.saveNBT(entity, tag);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNBTInt(final LivingEntity entity, final String nbtName, final int value) {
        try {
            final Object tag = NMSManager.getNBT(entity);
            NMSManager.setInt.invoke(tag, nbtName, value);
            NMSManager.saveNBT(entity, tag);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}