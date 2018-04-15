package com.focess.dropitem.runnable;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.Array;

public class SpawnDropItem extends BukkitRunnable {
	private final Array<Material> BanItems = new Array<>();
	private final boolean naturalSpawn;

	public SpawnDropItem(final DropItem dropItem) {
		this.naturalSpawn = dropItem.getConfig().getBoolean("NaturalSpawn", true);
		this.getBanItems(dropItem);
	}

	@SuppressWarnings("deprecation")
    private void getBanItems(final DropItem drop) {
		try {
			final String banItems = drop.getConfig().getString("BanItem");
			for (final String banItem : banItems.split(",")){
			    try {
			        int id = Integer.parseInt(banItem);
			        if (Material.getMaterial(id) == null)
			            continue;
			        this.BanItems.add(Material.getMaterial(id));
			    }
			    catch(Exception e) {
			        if (Material.getMaterial(banItem) == null)
			            continue;
			        this.BanItems.add(Material.getMaterial(banItem));
			    }
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in getting BanItems.");
		}
	}

	private boolean isNull(final String string) {
		if (string != null) {
			if (!string.startsWith(ChatColor.RED + "QuickShop") && !string.contains("GSCompleXMoneyFromMobzzzzzzzzz")
					&& !string.contains("XXXPlayer777MoneyXXX"))
				return true;
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		try {
			final List<World> worlds = Bukkit.getWorlds();
			for (final World world : worlds) {
				final List<Entity> entities = world.getEntities();
				for (final Entity entity : entities)
					if (CraftDropItem.include(entity)) {
						final EntityDropItem entityDropItem = CraftDropItem.getDropItem(entity);
						final Block block = entityDropItem.getLocation().getBlock();
						if (block.getType().compareTo(Material.AIR) == 0) {
							final Location location = new Location(entityDropItem.getWorld(),
									entityDropItem.getLocation().getX(), entityDropItem.getLocation().getY() - 1.0D,
									entityDropItem.getLocation().getZ());
							entityDropItem.teleport(location);
						}
						final Location loc = entityDropItem.getLocation();
						loc.setY(loc.getBlockY() + 1);
						if (loc.getBlock().getType().compareTo(Material.AIR) != 0)
							entityDropItem.teleport(loc);
						final List<Entity> entities_near = entityDropItem.getNearbyEntities(2.0D, 2.0D, 2.0D);
						boolean flag = false;
						for (final Entity entity_near : entities_near)
							if (entity_near instanceof Player) {
								flag = true;
								break;
							}
						if (!flag)
							entityDropItem.setCustomNameVisible(false);
					} else if (this.naturalSpawn && (entity instanceof Item) && !entity.isDead() && entity.isOnGround()
							&& this.isNull(((Item) entity).getItemStack().getItemMeta().getDisplayName())
							&& !this.BanItems.contains(((Item) entity).getItemStack().getType().name()))
						CraftDropItem.spawnItem((Item) entity);
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in running Runnable SpawnDropItem.");
		}
	}
}
