package com.focess.dropitem.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.focess.dropitem.DropItem;

public interface PlayerCommandExecuter extends CommandExecuter {

	@Override
	default void execute(final CommandSender sender, final String[] args) {
		if (sender instanceof Player && sender.isOp())
			this.execute((Player) sender, args);
		else
			this.executeError(sender, args);
	}

	void execute(Player player, String[] args);

	default void executeError(final CommandSender sender, final String[] args) {
		sender.sendMessage(DropItem.getMessage("SenderNotPlayer"));
	}

}
