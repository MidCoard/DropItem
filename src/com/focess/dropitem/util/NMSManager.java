package com.focess.dropitem.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.focess.dropitem.DropItem;

public class NMSManager {

    // basic
    private final static Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<>();
    private final static Map<String, Class<?>> loadedNMSClasses = new HashMap<>();

    // basic class
    public final static Class<?> CraftServer;
    public final static Class<?> CraftWorld;
    public final static Class<?> EntityPlayer;
    public final static Class<?> MinecraftServer;
    public final static Class<?> World;
    public final static Class<?> WorldServer;
    public final static Class<?> CraftEntity;
    public final static Class<?> NBTTagCompound;
    public final static Class<?> Entity;

    private static String versionString;

    // NBT Builder
    private final static Method e;
    // getNBT from an entity
    private final static Method f;
    // setNBT to an entity
    private final static Method getHandle;
    private final static Method setInt;
    private final static Method setBoolean;
    private final static Method hasKey;
    private final static Method getBoolean;
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
        getHandle = NMSManager.getMethod(CraftEntity, "getHandle", new Class[] {});
        setInt = NMSManager.getMethod(NBTTagCompound, "setInt", new Class[] { String.class, Integer.TYPE });
        setBoolean = NMSManager.getMethod(NBTTagCompound, "setBoolean", new Class[] { String.class, Boolean.TYPE });
        hasKey = NMSManager.getMethod(NBTTagCompound, "hasKey", new Class[] { String.class });
        getBoolean = NMSManager.getMethod(NBTTagCompound, "getBoolean", new Class[] { String.class });
        f = NMSManager.getMethod(Entity, "f", new Class[] { NBTTagCompound });
        if (DropItem.getVersion() < 12)
            e = NMSManager.getMethod(Entity, "e", new Class[] { NBTTagCompound });
        else
            e = NMSManager.getMethod(Entity, "save", new Class[] { NBTTagCompound });
    }

    private static Object getNBT(LivingEntity entity) {
        Object tag = null;
        try {
            final Object nmsEntity = getHandle.invoke(entity);
            tag = NBTTagCompound.newInstance();
            e.invoke(nmsEntity, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

    private static void saveNBT(LivingEntity entity, Object tag) {
        try {
            final Object nmsEntity = getHandle.invoke(entity);
            f.invoke(nmsEntity, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNBTBoolean(final LivingEntity entity, final String nbtName, final boolean value) {
        try {
            Object tag = getNBT(entity);
            setBoolean.invoke(tag, nbtName, value);
            saveNBT(entity, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNBTInt(final LivingEntity entity, final String nbtName, final int value) {
        try {
            Object tag = getNBT(entity);
            setInt.invoke(tag, nbtName, value);
            saveNBT(entity, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasNBT(LivingEntity entity, String nbtName) {
        try {
            Object tag = getNBT(entity);
            return (boolean) hasKey.invoke(tag, nbtName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getNBTBoolean(LivingEntity entity, String nbtName) {
        try {
            Object tag = getNBT(entity);
            return (boolean) getBoolean.invoke(tag, nbtName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        final String clazzName = "org.bukkit.craftbukkit." + NMSManager.getVersion() + nmsClassName;
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

    public static Class<?> getNMSClass(final String nmsClassName) {
        if (NMSManager.loadedNMSClasses.containsKey(nmsClassName))
            return NMSManager.loadedNMSClasses.get(nmsClassName);
        final String clazzName = "net.minecraft.server." + NMSManager.getVersion() + nmsClassName;
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

    public static String getVersion() {
        if (NMSManager.versionString == null) {
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            NMSManager.versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
        }
        return NMSManager.versionString;
    }
}