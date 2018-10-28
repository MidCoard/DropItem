package com.focess.dropitem.runnable;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.util.DropItemUtil;

public class SpawnDropItemRunnable extends BukkitRunnable {
    
	private final boolean naturalSpawn;

	public SpawnDropItemRunnable(final DropItem dropItem) {
		this.naturalSpawn = dropItem.getConfig().getBoolean("NaturalSpawn", true);
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
					if (this.naturalSpawn && (entity instanceof Item) && !entity.isDead() && entity.isOnGround()
							&& this.isNull(((Item) entity).getItemStack().getItemMeta().getDisplayName())
							&& DropItemUtil.checkBanItems(((Item) entity).getItemStack()))
						CraftDropItem.spawnItem((Item) entity);
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in running Runnable SpawnDropItem.");
		}
	}
}
