package com.focess.dropitem.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.Util;

public class PlayerJoinListener implements Listener {

	private final boolean allowedPlayer;
	private final List<String> allowedPlayers;
	private final DropItem drop;

	public PlayerJoinListener(final DropItem drop) {
		this.allowedPlayer = drop.getConfig().getBoolean("AllowedPlayer", false);
		this.allowedPlayers = Util.toList(drop.getConfig().getString("AllowedPlayers").split(","));
		this.drop = drop;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		try {
			final Player player = event.getPlayer();
			if (this.allowedPlayer)
				player.addAttachment(this.drop).setPermission("dropitem.use", true);
			else if (this.allowedPlayers.contains(player.getName()))
				player.addAttachment(this.drop).setPermission("dropitem.use", true);
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in calling Event PlayerJoinEvent.");
		}
	}

}
