package com.focess.dropitem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerBuildBlockListener implements Listener {
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent event) {
//		System.out.println(event.canBuild());
	}

}
