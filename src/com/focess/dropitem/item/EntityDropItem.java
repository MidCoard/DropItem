package com.focess.dropitem.item;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public abstract class EntityDropItem {

    
    public static EntityDropItem createEntityDropItem(final LivingEntity entity) {
        return new EntityDropItem1_8_p(entity);
    }

    protected static EntityDropItem createEntityDropItem(final LivingEntity entity, final ItemStack itemStack) {
        return new EntityDropItem1_8_p(entity, itemStack);
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