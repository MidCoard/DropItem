package com.focess.dropitem.listener;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.AnxiCode;

public class PlayerMoveListener implements Listener {
	private boolean allowedPlayer = false;
	private final int anxiCode;
	private final DropItem drop;
	private final boolean naturalSpawn;
	private final String PickForm;

	public PlayerMoveListener(final DropItem entity) {
		this.anxiCode = AnxiCode.getCode(PlayerMoveListener.class, entity);
		this.drop = entity;
		this.PickForm = entity.getConfig().getString("PickForm");
		if (!entity.getConfig().getBoolean("NaturalSpawn"))
			this.allowedPlayer = entity.getConfig().getBoolean("AllowedPlayer", false);
		this.naturalSpawn = entity.getConfig().getBoolean("NaturalSpawn", true);
	}

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		try {
			final Player player = event.getPlayer();
			final List<Entity> entities_load = player.getNearbyEntities(12, 12, 12);
			for (final Entity entity : entities_load)
				if (!CraftDropItem.include(entity) && !entity.isDead())
					if (this.drop.islower && (entity instanceof Zombie)) {
						if (!((Zombie) entity).hasPotionEffect(PotionEffectType.INVISIBILITY))
							CraftDropItem.loadItem(entity, this.anxiCode);
					} else if (!this.drop.islower && (entity instanceof ArmorStand))
						if (!((ArmorStand) entity).isVisible())
							CraftDropItem.loadItem(entity, this.anxiCode);
			if (!this.drop.islower && (player.getGameMode().compareTo(GameMode.SPECTATOR) == 0))
				return;
			final List<Entity> entities = player.getNearbyEntities(1.0D, 1.0D, 1.0D);
			for (final Entity entity : entities)
				if (CraftDropItem.include(entity) && player.isSneaking() && this.PickForm.equals("normal")
						&& (this.naturalSpawn || this.allowedPlayer || player.hasPermission("dropitem.use"))) {
					final EntityDropItem entityDropItem = CraftDropItem.getDropItem(entity);
					if (player.getInventory().firstEmpty() != -1) {
						final PlayerGottenEvent event_gotten = new PlayerGottenEvent(entityDropItem.getItemInHand(),
								player);
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
							final PlayerGottenEvent event_gotten = new PlayerGottenEvent(entityDropItem.getItemInHand(),
									player);
							this.drop.getServer().getPluginManager().callEvent(event_gotten);
							if (!event_gotten.isCancelled()) {
								player.getInventory().addItem(new ItemStack[] { entityDropItem.getItemInHand() });
								CraftDropItem.remove(entity, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
							}
						} else
							continue;
					}
				}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in calling Event PlayerMoveEvent.");
		}
	}
}
