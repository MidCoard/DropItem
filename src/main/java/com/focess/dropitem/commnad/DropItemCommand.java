package com.focess.dropitem.commnad;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.DropItemInfo;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.Command;
import com.focess.dropitem.util.DropItemConfiguration;
import com.focess.dropitem.util.DropItemUtil;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DropItemCommand extends Command {

    public DropItemCommand(final DropItem dropItem) {
        super("DropItem", DropItemCommand.getAliases(dropItem), "dropitem.command");
        this.drop = dropItem;
    }

    private final DropItem drop;

    private static List<String> getAliases(final DropItem dropItem) {
        if (DropItemConfiguration.isEnableAliases())
            return Lists.newArrayList("di");
        return new ArrayList<>();
    }

    @Override
    protected List<String> getCompleteLists(final CommandSender sender, final String cmd, final String[] args) {
        if (args == null || args.length == 0)
            return Lists.newArrayList();
        else if (args.length == 1)
            return Lists.newArrayList("clean", "cleanall", "disable");
        return Lists.newArrayList();
    }

    @Override
    public void init() {
        this.addExecutor(0, (sender, args) -> {
            final Collection<EntityDropItem> dropItems = CraftDropItem.getDropItems();
            for (final EntityDropItem dropItem : dropItems)
                CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.SYSTEM_CLEAN);
            final File drops = new File(this.drop.getDataFolder(), "drops");
            for (final File file : drops.listFiles())
                file.delete();
            if (DropItemConfiguration.isDropItemAI())
                this.drop.getCraftAIListener().clear();
            DropItemInfo.clear();
            sender.sendMessage(DropItem.getMessage("AfterClean"));

        }, "clean");
        this.addExecutor(0, (sender, args) -> {
            sender.sendMessage(DropItem.getMessage("Disabling"));
            this.drop.getPluginLoader().disablePlugin(this.drop);
            final Collection<EntityDropItem> dropItems = CraftDropItem.getDropItems();
            for (final EntityDropItem dropItem : dropItems) {
                dropItem.getLocation().getWorld().dropItem(dropItem.getLocation(), dropItem.getItemStack());
                CraftDropItem.remove(dropItem, false);
            }
            this.unregister();
            DropItemUtil.forceDelete(this.drop.getDataFolder());
        }, "disable");
        this.addExecutor(0, (sender, args) -> {
            final Collection<EntityDropItem> dropItems = CraftDropItem.getDropItems();
            for (final EntityDropItem dropItem : dropItems)
                CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.SYSTEM_CLEAN);
            final File drops = new File(this.drop.getDataFolder(), "drops");
            for (final File file : drops.listFiles())
                file.delete();
            if (DropItemConfiguration.isDropItemAI())
                this.drop.getCraftAIListener().clear();
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds)
                for (final Entity entity : world.getEntities())
                    if (entity instanceof ArmorStand && !((ArmorStand) entity).isVisible())
                        entity.remove();
            sender.sendMessage(DropItem.getMessage("AfterCleanAll"));
        }, "cleanall");
        this.addExecutor(0, (sender, args) -> {
        }, "test");
    }

    @Override
    public void usage(final CommandSender commandSender) {
        commandSender.sendMessage(DropItem.getMessage("InvalidArg"));
        commandSender.sendMessage(DropItem.getMessage("Welcome"));
        commandSender.sendMessage(DropItem.getMessage("CommandClean"));
        commandSender.sendMessage(DropItem.getMessage("CommandCleanAll"));
        commandSender.sendMessage(DropItem.getMessage("CommandDisable"));
    }
}
