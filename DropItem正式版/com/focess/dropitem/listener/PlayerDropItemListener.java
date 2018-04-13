package com.focess.dropitem.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.util.Array;

public class PlayerDropItemListener implements Listener {

	private boolean allowedPlayer = false;
	private final Array<String> BanItems = new Array<>();
	private final boolean naturalSpwan;

	public PlayerDropItemListener(final DropItem dropItem) {
		this.naturalSpwan = dropItem.getConfig().getBoolean("NaturalSpawn", true);
		if (!this.naturalSpwan)
			this.allowedPlayer = dropItem.getConfig().getBoolean("AllowedPlayer", false);
		this.getBanItems(dropItem);
	}

	private void getBanItems(final DropItem drop) {
		try {
			final String banItems = drop.getConfig().getString("BanItem");
			for (final String banItem : banItems.split(","))
				this.BanItems.add(banItem);
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

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		try {
			if (this.isNull(event.getItemDrop().getItemStack().getItemMeta().getDisplayName())
					&& !this.BanItems.contains(event.getItemDrop().getItemStack().getType().name())
					&& (event.getPlayer().hasPermission("dropitem.use") || this.allowedPlayer) && !this.naturalSpwan)
				CraftDropItem.spawnItem(event.getItemDrop());
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in calling Event ItemSpawnEvent.");
		}
	}
}
