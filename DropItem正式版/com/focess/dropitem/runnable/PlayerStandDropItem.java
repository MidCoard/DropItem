package com.focess.dropitem.runnable;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;

public class PlayerStandDropItem extends BukkitRunnable {
	private boolean allowedPlayer = false;
	private final DropItem drop;
	private final boolean naturalSpawn;
	private final String PickForm;

	public PlayerStandDropItem(final DropItem dropItem) {
		this.PickForm = dropItem.getConfig().getString("PickForm");
		this.drop = dropItem;
		if (!dropItem.getConfig().getBoolean("NaturalSpawn"))
			this.allowedPlayer = dropItem.getConfig().getBoolean("AllowedPlayer", false);
		this.naturalSpawn = dropItem.getConfig().getBoolean("NaturalSpawn", true);
	}

	@Override
	public void run() {
		try {
			final List<World> worlds = Bukkit.getWorlds();
			for (final World world : worlds) {
				final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
				for (final Entity player_entity : players) {
					final Player player = (Player) player_entity;
					final List<Entity> entities = player.getNearbyEntities(0.75D, 0.75D, 0.75D);
					for (final Entity entity : entities)
						if (CraftDropItem.include(entity) && this.PickForm.equals("w-move")
								&& (this.naturalSpawn || this.allowedPlayer || player.hasPermission("dropitem.use"))) {
							final EntityDropItem entityDropItem = CraftDropItem.getDropItem(entity);
							if (player.getInventory().firstEmpty() != -1) {
								final PlayerGottenEvent event_gotten = new PlayerGottenEvent(
										entityDropItem.getItemInHand(), player);
								this.drop.getServer().getPluginManager().callEvent(event_gotten);
								if (!event_gotten.isCancelled()) {
									player.getInventory().addItem(new ItemStack[] { entityDropItem.getItemInHand() });
									CraftDropItem.remove(entity, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
								}
							} else if (!player.getInventory().contains(entityDropItem.getItemInHand()))
								continue;
							else {
								int count = 0;
								final ItemStack is = entityDropItem.getItemInHand();
								int amount = 0;
								for (final ItemStack itemStack : player.getInventory())
									if (itemStack != null)
										if (itemStack.isSimilar(is)) {
											count += itemStack.getAmount();
											amount++;
										}
								if (((amount * entityDropItem.getItemInHand().getMaxStackSize()) - count) != 0) {
									final PlayerGottenEvent event_gotten = new PlayerGottenEvent(
											entityDropItem.getItemInHand(), player);
									this.drop.getServer().getPluginManager().callEvent(event_gotten);
									if (!event_gotten.isCancelled()) {
										player.getInventory()
												.addItem(new ItemStack[] { entityDropItem.getItemInHand() });
										CraftDropItem.remove(entity, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
									}
								} else
									continue;
							}
						}
				}
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in running Runnable PlayerStandDropItem.");
		}
	}
}