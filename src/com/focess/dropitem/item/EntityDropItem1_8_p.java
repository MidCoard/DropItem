package com.focess.dropitem.item;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class EntityDropItem1_8_p extends EntityDropItem {

	EntityDropItem1_8_p(final Entity armorStand) {
		super(armorStand);
		this.check(armorStand);
		this.itemStack = ((ArmorStand) armorStand).getItemInHand();
	}

	EntityDropItem1_8_p(final Entity armorStand, final ItemStack itemStack) {
		super(armorStand, itemStack);
		this.check(armorStand);
		((ArmorStand) this.dropitem).setItemInHand(itemStack);
		EntityDropItem.setNBT(this.dropitem, "NoGravity", true);
	}

	private void check(final Entity armorStand) {
		if (!(armorStand instanceof ArmorStand)) {
			try {
				this.finalize();
			} catch (final Throwable e) {
				e.printStackTrace();
			}
			throw new ClassCastException();
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final EntityDropItem1_8_p other = (EntityDropItem1_8_p) obj;
		if (this.dropitem == null) {
			if (other.dropitem != null)
				return false;
		} else if (!this.dropitem.equals(other.dropitem))
			return false;
		return true;
	}

	@Override
	public ItemStack getBoots() {
		return ((ArmorStand) this.dropitem).getBoots();
	}

	@Override
	public ItemStack getChestplate() {
		return ((ArmorStand) this.dropitem).getChestplate();
	}

	@Override
	protected String getCustomName() {
		return this.dropitem.getCustomName();
	}

	@Override
	protected Entity getEntity() {
		return this.dropitem;
	}

	@Override
	public int getFireTicks() {
		return this.dropitem.getFireTicks();
	}

	@Override
	public ItemStack getHelmet() {
		return ((ArmorStand) this.dropitem).getHelmet();
	}

	@Override
	public ItemStack getItemInHand() {
		return ((ArmorStand) this.dropitem).getItemInHand();
	}

	@Override
	public ItemStack getLeggings() {
		return ((ArmorStand) this.dropitem).getLeggings();
	}

	@Override
	public Location getLocation() {
		return this.dropitem.getLocation();
	}

	@Override
	public List<Entity> getNearbyEntities(final double d, final double e, final double g) {
		return this.dropitem.getNearbyEntities(d, e, g);
	}

	@Override
    public UUID getUniqueId() {
		return this.dropitem.getUniqueId();
	}

	@Override
	public boolean isDead() {
		return this.dropitem.isDead();
	}

	@Override
	public boolean isVisible() {
		return ((ArmorStand) this.dropitem).isVisible();
	}

	@Override
	protected void remove() {
		this.dropitem.remove();
	}

	@Override
	public void setBoots(final ItemStack itemStack) {
		((ArmorStand) this.dropitem).setBoots(itemStack);
	}

	@Override
    protected void setCanPickupItems(final boolean b) {
		((LivingEntity) this.dropitem).setCanPickupItems(b);

	}

	@Override
	public void setChestplate(final ItemStack itemStack) {
		((ArmorStand) this.dropitem).setChestplate(itemStack);
	}

	@Override
    public void setCustomName(final String customName) {
		this.dropitem.setCustomName(customName);

	}

	@Override
	public void setCustomNameVisible(final boolean b) {
		this.dropitem.setCustomNameVisible(b);
	}

	@Override
	public void setHelmet(final ItemStack itemStack) {
		((ArmorStand) this.dropitem).setHelmet(itemStack);
	}

	@Override
	public void setLeggings(final ItemStack itemStack) {
		((ArmorStand) this.dropitem).setLeggings(itemStack);
	}

	@Override
    public void setRightArmPose(final EulerAngle eulerAngle) {
		((ArmorStand) this.dropitem).setRightArmPose(eulerAngle);
	}

	@Override
	protected void setUp() {
		((ArmorStand) this.dropitem).setVisible(false);
		((LivingEntity) this.dropitem).setRemoveWhenFarAway(false);
		this.dropitem.setCustomNameVisible(false);
	}

	@Override
	public void teleport(final Location location) {
		this.dropitem.teleport(location);
	}

}