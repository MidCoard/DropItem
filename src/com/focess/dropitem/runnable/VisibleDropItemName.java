package com.focess.dropitem.runnable;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.item.CraftDropItem;

public class VisibleDropItemName extends BukkitRunnable {
	private final boolean showItemInfo;

	public VisibleDropItemName(final DropItem dropItem) {
		this.showItemInfo = dropItem.getConfig().getBoolean("ShowItemInfo");
	}

	@Override
	public void run() {
		try {
			final List<World> worlds = Bukkit.getWorlds();
			for (final World world : worlds) {
				final List<Entity> entities = world.getEntities();
				for (final Entity entity : entities)
					if (entity instanceof Player) {
						final Player player = (Player) entity;
						final Location location = player.getEyeLocation();
						for (final Entity entity_chunk : location.getChunk().getEntities())
							if (CraftDropItem.include(entity_chunk)) {
								final Location loc = entity_chunk.getLocation();
								if ((loc.distance(location) <= 5.0D) && this.showItemInfo)
									CraftDropItem.getDropItem(entity_chunk).setCustomNameVisible(true);
							}
					}
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in running Runnable VisibleDropItemName.");
		}
	}
}
