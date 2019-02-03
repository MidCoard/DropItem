package com.focess.dropitem.event;

import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;

public class HopperGottenEvent extends DropItemGottenEvent {
    private final Hopper hopper;

    public HopperGottenEvent(final ItemStack itemStack, final Hopper hopper) {
        super(itemStack);
        this.hopper = hopper;
    }

    public Hopper getHopper() {
        return this.hopper;
    }

}
