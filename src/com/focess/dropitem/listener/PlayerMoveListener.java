package com.focess.dropitem.listener;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.DropItemUtil;

public class PlayerMoveListener implements Listener {
	private final int anxiCode;

	public PlayerMoveListener(final DropItem drop) {
		this.anxiCode = AnxiCode.getCode(PlayerMoveListener.class, drop);
	}

	private void onLoadDropItem(final Player player) {
		final List<Entity> entities_load = player.getNearbyEntities(12, 12, 12);
		for (final Entity entity : entities_load)
			if (!CraftDropItem.include(entity) && !entity.isDead())
				if (entity instanceof ArmorStand)
					if (!((ArmorStand) entity).isVisible())
						CraftDropItem.loadItem(entity, this.anxiCode);
	}

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		this.onLoadDropItem(player);
		this.onPlayerPickUpItem(player);
	}

	private void onPlayerPickUpItem(final Player player) {
		if (player.getGameMode().compareTo(GameMode.SPECTATOR) == 0)
			return;
		final List<Entity> entities = player.getNearbyEntities(1.0D, 1.0D, 1.0D);
		for (final Entity entity : entities)
			if (CraftDropItem.include(entity) && player.isSneaking() && DropItemUtil.checkPickForm("normal")
					&& (DropItemUtil.naturalSpawn() || DropItemUtil.allowedPlayer()
							|| DropItemUtil.checkPlayerPermission(player)))
				DropItemUtil.fillPlayerInventory(player, CraftDropItem.getDropItem(entity));

	}
}
