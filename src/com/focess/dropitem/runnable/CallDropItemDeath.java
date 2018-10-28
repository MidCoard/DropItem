package com.focess.dropitem.runnable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.HopperGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.AnxiCode;

public class CallDropItemDeath extends BukkitRunnable {
	private static int anxiCode;
	private final DropItem drop;

	public CallDropItemDeath(final DropItem dropItem) {
		this.drop = dropItem;
		CallDropItemDeath.anxiCode = AnxiCode.getCode(CallDropItemDeath.class, this.drop);
	}

	@Override
	public void run() {
		try {
			for (EntityDropItem dropItem:CraftDropItem.getDropItems(CallDropItemDeath.anxiCode)) {
				final Location location = dropItem.getLocation();
				location.setY(location.getY() - 1.0D);
				if ((dropItem != null) && !dropItem.isDead())
					if (location.getBlock().getType().compareTo(Material.HOPPER) == 0) {
						final Hopper hopper = (Hopper) location.getBlock().getState();
						final HopperGottenEvent event = new HopperGottenEvent(dropItem.getItemInHand(), hopper);
						this.drop.getServer().getPluginManager().callEvent(event);
						if (!event.isCancelled()) {
							CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.HOPPER_GOTTEN);
							hopper.getInventory().addItem(new ItemStack[] { dropItem.getItemInHand() });
						}
					} else if (dropItem.getFireTicks() > 0)
						CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.FIRE_TICK);
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in running Runnable CallDropItemDeath.");
		}
	}
}
