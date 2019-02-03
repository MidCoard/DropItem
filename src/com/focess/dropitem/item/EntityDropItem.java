package com.focess.dropitem.item;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.NMSManager;

public abstract class EntityDropItem {

    protected static DropItem drop = null;
    private static Method e;

    private static Method f;

    // NBT builder
    private static Method getHandle;
    private static Class<?> nbtTagClass;
    private static Class<?> nmsEntityClass;
    private static Method setInt;
    static {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("DropItem");
        if (plugin != null)
            EntityDropItem.drop = (DropItem) plugin;
        try {
            final Class<?> craftEntity = NMSManager.getCraftClass("entity.CraftEntity");
            EntityDropItem.getHandle = NMSManager.getMethod(craftEntity, "getHandle", new Class[] {});
            EntityDropItem.nmsEntityClass = NMSManager.getNMSClass("Entity");
            EntityDropItem.nbtTagClass = NMSManager.getNMSClass("NBTTagCompound");
            EntityDropItem.setInt = NMSManager.getMethod(EntityDropItem.nbtTagClass, "setInt",
                    new Class[] { String.class, Integer.TYPE });
            EntityDropItem.f = NMSManager.getMethod(EntityDropItem.nmsEntityClass, "f",
                    new Class[] { EntityDropItem.nbtTagClass });
            if (DropItem.getVersion() < 12)
                EntityDropItem.e = NMSManager.getMethod(EntityDropItem.nmsEntityClass, "e",
                        new Class[] { EntityDropItem.nbtTagClass });
            else
                EntityDropItem.e = NMSManager.getMethod(EntityDropItem.nmsEntityClass, "save",
                        new Class[] { EntityDropItem.nbtTagClass });
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in preparing NBT builder.");
        }
    }

    public static EntityDropItem createEntityDropItem(final LivingEntity entity) {
        return new EntityDropItem1_8_p(entity);
    }

    protected static EntityDropItem createEntityDropItem(final LivingEntity entity, final ItemStack itemStack) {
        return new EntityDropItem1_8_p(entity, itemStack);
    }

    public static void setNBT(final LivingEntity entity, final String nbtName, final boolean value) {
        EntityDropItem.setNBT(entity, nbtName, value == true ? 1 : 0);
    }

    private static void setNBT(final LivingEntity entity, final String nbtName, final int value) {
        try {
            final Object nmsEntity = EntityDropItem.getHandle.invoke(entity);
            final Object tag = EntityDropItem.nbtTagClass.newInstance();
            EntityDropItem.e.invoke(nmsEntity, tag);
            EntityDropItem.setInt.invoke(tag, nbtName, value);
            EntityDropItem.f.invoke(nmsEntity, tag);
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in NBT of " + entity.getCustomName() + " building(" + nbtName + ") .");
        }
    }

    private final LivingEntity dropitem;

    private ItemStack itemStack;

    EntityDropItem(final LivingEntity entity) {
        this.dropitem = entity;
    }

    EntityDropItem(final LivingEntity entity, final ItemStack itemStack) {
        this.dropitem = entity;
        this.itemStack = itemStack;
    }

    protected String getCustomName() {
        return this.dropitem.getCustomName();
    }

    public LivingEntity getEntity() {
        return this.dropitem;
    }

    public int getFireTicks() {
        return this.dropitem.getFireTicks();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    };

    public Location getLocation() {
        return this.dropitem.getLocation();
    }

    public List<Entity> getNearbyEntities(final double d, final double e, final double g) {
        return this.dropitem.getNearbyEntities(d, e, g);
    }

    public UUID getUniqueId() {
        return this.dropitem.getUniqueId();
    }

    public World getWorld() {
        return this.getLocation().getWorld();
    }

    public boolean isDead() {
        return this.dropitem.isDead();
    }

    public abstract boolean isVisible();

    protected void remove() {
        this.dropitem.remove();
    }

    public void setCustomName(final String customName) {
        this.dropitem.setCustomName(customName);
    }

    public void setCustomNameVisible(final boolean flag) {
        this.dropitem.setCustomNameVisible(flag);
    }

    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public abstract void setRightArmPose(final EulerAngle eulerAngle);

    protected abstract void setUp();

    public void teleport(final Location location) {
        this.dropitem.teleport(location);
    }

}