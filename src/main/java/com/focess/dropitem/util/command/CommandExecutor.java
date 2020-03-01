package com.focess.dropitem.util.command;

import org.bukkit.command.CommandSender;

public interface CommandExecutor {

    void execute(CommandSender sender, String[] args);

}
