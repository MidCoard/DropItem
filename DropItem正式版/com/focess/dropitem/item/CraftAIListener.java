package com.focess.dropitem.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.Array;

public class CraftAIListener {
	private class DropItemAI extends BukkitRunnable {
		private DropItemAI() {
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i < CraftDropItem.getDropItems(CraftAIListener.anxiCode).size(); i++) {
					final EntityDropItem dropItem = CraftDropItem.getDropItems(CraftAIListener.anxiCode).get(i);
					if (!dropItem.isDead()) {
						final List<Entity> entities = dropItem.getNearbyEntities(12.0D, 12.0D, 12.0D);
						boolean flag = false;
						for (final Entity entity : entities)
							if ((entity instanceof Player) && (CraftAIListener.this.drop.islower
									|| (((Player) entity).getGameMode().compareTo(GameMode.SPECTATOR) != 0)))
								flag = true;
						if (!flag) {
							CraftAIListener.ais.put(dropItem.getLocation(), dropItem.getItemInHand());
							CraftAIListener.aiInfos.put(dropItem.getLocation(), DropItemInfo.getDropItemInfo(dropItem));
							CraftDropItem.remove(dropItem, false);
						}
					}
				}
			} catch (final Exception e) {
				Debug.debug(e, "Something wrong in running Runnable DropItemAI.");
			}
		}
	}

	private class DropItemAIload extends BukkitRunnable {
		private DropItemAIload() {
		}

		@Override
		public void run() {
			try {
				final Array<Location> locations = new Array<>();
				final List<World> worlds = Bukkit.getWorlds();
				for (final World world : worlds) {
					final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
					for (final Entity player : players)
						if ((player instanceof Player) && (CraftAIListener.this.drop.islower
								|| (((Player) player).getGameMode().compareTo(GameMode.SPECTATOR) != 0)))
							for (final Location location : CraftAIListener.ais.keySet())
								if (location.getWorld().getName().equals(player.getWorld().getName())
										&& (location.distance(player.getLocation()) < 12.0D)) {
									final ItemStack itemStack = CraftAIListener.ais.get(location);
									if (CraftAIListener.aiInfos.get(location) != null) {
										final Location temp = new Location(location.getWorld(), location.getX(),
												location.getY() + 1.0D, location.getZ());
										if (itemStack != null) {
											final EntityDropItem dropItem = CraftDropItem.spawnItem(itemStack, temp,
													false);
											CraftAIListener.aiInfos.put(location,
													CraftAIListener.aiInfos.get(location).setDropItem(dropItem));
										}
									}
									locations.add(location);
								}
				}
				for (final Location location : locations)
					CraftAIListener.ais.remove(location);
			} catch (final Exception e) {
				Debug.debug(e, "Something wrong in running Runnable DropItemAIload.");
			}
		}

	}

	private static HashMap<Location, DropItemInfo> aiInfos = new HashMap<>();
	private static HashMap<Location, ItemStack> ais = new HashMap<>();
	private static int anxiCode;
	private static boolean isStart = false;

	public static void reload(final int anxiCode) {
		if (CraftAIListener.anxiCode == anxiCode)
			CraftAIListener.isStart = false;
		else
			AnxiCode.shut(AnxiCode.class);
	}

	protected static void remove(final DropItemInfo dropItemInfo) {
		Location l = null;
		for (final Location location : CraftAIListener.aiInfos.keySet())
			if (CraftAIListener.aiInfos.get(location) != null)
				if (CraftAIListener.aiInfos.get(location).equals(dropItemInfo))
					for (final Location loc : CraftAIListener.ais.keySet())
						if (loc.equals(location))
							l = loc;
		CraftAIListener.aiInfos.remove(l);
		CraftAIListener.ais.remove(l);
	}

	private DropItem drop;

	private BukkitTask loadTask;

	private BukkitTask startTask;

	public CraftAIListener(final DropItem dropItem, final int anxiCode) {
		try {
			CraftAIListener.anxiCode = AnxiCode.getCode(CraftAIListener.class, dropItem);
			if (anxiCode != CraftAIListener.anxiCode)
				AnxiCode.shut(CraftAIListener.class);
			if (CraftAIListener.isStart) {
				System.err.println("DropItemAI已经启动");
				AnxiCode.shut(CraftAIListener.class);
			}
			this.drop = dropItem;
			CraftAIListener.isStart = true;
			this.init();
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in creating CraftAIListener Instance.");
		}
	}

	public void clear(final int anxiCode) {
		try {
			if (CraftAIListener.anxiCode == anxiCode)
				CraftAIListener.ais.clear();
			else
				AnxiCode.shut(CraftAIListener.class);
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in clearing the AIs.");
		}
	}

	public HashMap<Location, ItemStack> getAIs(final int anxiCode) {
		try {
			if (CraftAIListener.anxiCode == anxiCode)
				return CraftAIListener.ais;
			AnxiCode.shut(CraftAIListener.class);
			return null;
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in getting AIs.");
			return null;
		}
	}

	public BukkitTask getLoadTask() {
		return this.loadTask;
	}

	public BukkitTask getStartTask() {
		return this.startTask;
	}

	private void init() {
		try {
			this.startTask = this.drop.getServer().getScheduler().runTaskTimer(this.drop, (Runnable) new DropItemAI(),
					0L, 20L);
			this.loadTask = this.drop.getServer().getScheduler().runTaskTimer(this.drop,
					(Runnable) new DropItemAIload(), 0L, 40L);
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in starting two runnables.");
		}
	}
}