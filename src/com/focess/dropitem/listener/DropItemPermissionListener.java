package com.focess.dropitem.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.DropItemUtil;

public class DropItemPermissionListener implements Listener {
	private final DropItem drop;

	public DropItemPermissionListener(final DropItem drop) {
		this.drop = drop;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (DropItemUtil.allowedPlayer())
			player.addAttachment(this.drop).setPermission("dropitem.use", true);
		else if (DropItemUtil.checkAllowedPlayer(player.getName()))
			player.addAttachment(this.drop).setPermission("dropitem.use", true);
	}

}
