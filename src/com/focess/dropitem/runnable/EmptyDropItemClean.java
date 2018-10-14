package com.focess.dropitem.runnable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.Array;
import com.focess.dropitem.util.DropItemUtil;

public class EmptyDropItemClean extends BukkitRunnable {
	private static int anxiCode;

	public EmptyDropItemClean(final DropItem dropItem) {
		EmptyDropItemClean.anxiCode = AnxiCode.getCode(EmptyDropItemClean.class, dropItem);
	}

	@Override
	public void run() {
		try {
			final Array<EntityDropItem> dropItems = CraftDropItem.getDropItems(EmptyDropItemClean.anxiCode);
			for (int i = 0; i < dropItems.size(); i++) {
				final EntityDropItem dropItem = dropItems.get(i);
				final ItemStack head = dropItem.getHelmet();
				final ItemStack chest = dropItem.getChestplate();
				final ItemStack leg = dropItem.getLeggings();
				final ItemStack boots = dropItem.getBoots();
				final ItemStack hand = dropItem.getItemInHand();
				if ((head != null) && (head.getType().compareTo(Material.AIR) != 0)) {
					if (DropItemUtil.checkBanItems(head))
					    CraftDropItem.spawnItem(head, dropItem.getLocation());
					dropItem.setHelmet(new ItemStack(Material.AIR));
				}
				if ((chest != null) && (chest.getType().compareTo(Material.AIR) != 0)) {
				    if (DropItemUtil.checkBanItems(chest))
				        CraftDropItem.spawnItem(chest, dropItem.getLocation());
					dropItem.setChestplate(new ItemStack(Material.AIR));
				}
				if ((leg != null) && (leg.getType().compareTo(Material.AIR) != 0)) {
				    if (DropItemUtil.checkBanItems(leg))
				        CraftDropItem.spawnItem(leg, dropItem.getLocation());
					dropItem.setLeggings(new ItemStack(Material.AIR));
				}
				if ((boots != null) && (boots.getType().compareTo(Material.AIR) != 0)) {
					if (DropItemUtil.checkBanItems(boots))
					    CraftDropItem.spawnItem(boots, dropItem.getLocation());
					dropItem.setBoots(new ItemStack(Material.AIR));
				}
				if ((hand == null) || (hand.getType().compareTo(Material.AIR) == 0))
					CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in running Runnable EmptyDropItemClean.");
		}
	}
}
