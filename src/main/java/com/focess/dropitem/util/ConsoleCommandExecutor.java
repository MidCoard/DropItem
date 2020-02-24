package com.focess.dropitem.util;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public interface ConsoleCommandExecutor extends CommandExecutor {

    @Override
    default void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ConsoleCommandSender)
            this.execute((ConsoleCommandSender) sender, args);
        else
            this.executeError(sender, args);
    }

    void execute(ConsoleCommandSender sender, String[] args);

    default void executeError(final CommandSender sender, final String[] args) {
    }

}
