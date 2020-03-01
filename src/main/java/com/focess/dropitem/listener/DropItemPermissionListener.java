package com.focess.dropitem.listener;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import com.focess.dropitem.util.version.VersionUpdater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DropItemPermissionListener implements Listener {
    private final DropItem drop;

    public DropItemPermissionListener(final DropItem drop) {
        this.drop = drop;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (DropItemConfiguration.isAllowedPlayer() || DropItemConfiguration.checkAllowedPlayers(player.getName()) || DropItemConfiguration.isNaturalSpawn())
            player.addAttachment(this.drop).setPermission("dropitem.use", true);
        if (player.isOp() && VersionUpdater.isNeedUpdated())
            player.sendMessage(DropItemConfiguration.getMessage("LowVersion",VersionUpdater.getVersion()));
    }

}
