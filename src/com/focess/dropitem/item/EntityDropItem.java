package com.focess.dropitem.item;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.NMSManager;

public abstract class EntityDropItem {

	protected static DropItem drop = null;
	private static Method f;

	private static Method e;

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

	public static EntityDropItem getEntityDropItem(final Entity entity) {
		if (!EntityDropItem.drop.islower)
			return new EntityDropItem1_8_p(entity);
		else
			return null;
	}

	protected static EntityDropItem getEntityDropItem(final Entity entity, final ItemStack itemStack) {
		if (!EntityDropItem.drop.islower)
			return new EntityDropItem1_8_p(entity, itemStack);
		else
			return null;
	}

	protected static void setNBT(final Entity entity, final String nbtName, final boolean value) {
		EntityDropItem.setNBT(entity, nbtName, value == true ? 1 : 0);
	}

	private static void setNBT(final Entity entity, final String nbtName, final int value) {
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

	protected Entity dropitem;

	protected ItemStack itemStack;

	EntityDropItem(final Entity entity) {
		this.dropitem = entity;
	}

	EntityDropItem(final Entity entity, final ItemStack itemStack) {
		this.dropitem = entity;
		this.itemStack = itemStack;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final EntityDropItem other = (EntityDropItem) obj;
		if (this.dropitem == null) {
			if (other.dropitem != null)
				return false;
		} else if (!this.dropitem.equals(other.dropitem))
			return false;
		return true;
	}

	public abstract ItemStack getBoots();

	public abstract ItemStack getChestplate();

	protected abstract String getCustomName();

	protected abstract Entity getEntity();

	public abstract int getFireTicks();

	public abstract ItemStack getHelmet();

	public abstract ItemStack getItemInHand();

	public abstract ItemStack getLeggings();

	public abstract Location getLocation();

	public abstract List<Entity> getNearbyEntities(final double d, final double e, final double g);

	public abstract UUID getUniqueId();

	public World getWorld() {
		return this.getLocation().getWorld();
	}

	public abstract boolean isDead();

	public abstract boolean isVisible();

	protected abstract void remove();

	public abstract void setBoots(final ItemStack itemStack);

	public abstract void setCanPickupItems(final boolean b);

	public abstract void setChestplate(final ItemStack itemStack);

	public abstract void setCustomName(final String customName);

	public abstract void setCustomNameVisible(boolean b);

	public abstract void setHelmet(final ItemStack itemStack);

	public abstract void setLeggings(final ItemStack itemStack);

	public abstract void setRightArmPose(final EulerAngle eulerAngle);

	protected abstract void setUp();

	public abstract void teleport(Location location);

}