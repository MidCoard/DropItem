package com.focess.dropitem.commnad;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.DropItemInfo;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.command.Command;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import com.focess.dropitem.util.version.VersionUpdater;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DropItemCommand extends Command {

    public DropItemCommand(final DropItem dropItem) {
        super("dropitem", DropItemCommand.getAliases(dropItem), "dropitem.command");
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
            return Lists.newArrayList("clean", "cleanall", "disable", "update", "download", "updatenow", "reload");
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
            sender.sendMessage(DropItemConfiguration.getMessage("AfterClean"));

        }, "clean");
        this.addExecutor(0, (sender, args) -> {
            sender.sendMessage(DropItemConfiguration.getMessage("Disabling"));
            Bukkit.getPluginManager().disablePlugin(this.drop);
            final Collection<EntityDropItem> dropItems = CraftDropItem.getDropItems();
            for (final EntityDropItem dropItem : dropItems) {
                CraftDropItem.remove(dropItem, false);
                dropItem.getLocation().getWorld().dropItem(dropItem.getLocation().add(0, 1 - DropItemConfiguration.getHeight(), 0), dropItem.getItemStack());
            }
            this.unregister();
        }, "disable");
        this.addExecutor(0, (sender, args) -> {
            final Collection<EntityDropItem> dropItems = CraftDropItem.getDropItems();
            for (final EntityDropItem dropItem : dropItems)
                CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.SYSTEM_CLEAN);
            if (DropItemConfiguration.isDropItemAI())
                this.drop.getCraftAIListener().clear();
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds)
                for (final Entity entity : world.getEntities())
                    if (entity instanceof ArmorStand && !((ArmorStand) entity).isVisible())
                        entity.remove();
            sender.sendMessage(DropItemConfiguration.getMessage("AfterCleanAll"));
        }, "cleanall");
        this.addExecutor(0, (sender, args) -> {
            this.drop.addThread(new Thread() {
                @Override
                public void run() {
                    VersionUpdater.checkForUpdate(DropItemCommand.this.drop);
                }
            }).start();
        }, "update");
        this.addExecutor(0, (sender, args) -> {
                    this.drop.addThread(new Thread() {
                        @Override
                        public void run() {
                            VersionUpdater.checkForUpdate(DropItemCommand.this.drop);
                            if (VersionUpdater.isNeedUpdated()) {
                                VersionUpdater.downloadNewVersion(DropItemCommand.this.drop, this,true);
                                if (VersionUpdater.isDownloaded())
                                    sender.sendMessage(DropItemConfiguration.getMessage("HaveDownloaded"));
                                else
                                    sender.sendMessage(DropItemConfiguration.getMessage("DownloadFail"));
                            }
                        }
                    }).start();
        }, "download");
        this.addExecutor(0, (sender, args) -> {
            if (VersionUpdater.isNeedUpdated())
                if (VersionUpdater.isDownloaded()) {
                    sender.sendMessage(DropItemConfiguration.getMessage("Updating"));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dropitem reload");
                } else sender.sendMessage(DropItemConfiguration.getMessage("NoDownload"));
            else sender.sendMessage(DropItemConfiguration.getMessage("NoUpdateCheck"));
        }, "updatenow");
        this.addExecutor(0, (sender, args) -> {
            sender.sendMessage(DropItemConfiguration.getMessage("Reloading"));
            final File file;
            if (VersionUpdater.isNeedUpdated() && VersionUpdater.isDownloaded())
                file = VersionUpdater.getUpdatedFile(this.drop);
            else file = DropItemUtil.getPluginFile(this.drop);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dropitem disable");
            try {
                final Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
                plugin.onLoad();
                Bukkit.getPluginManager().enablePlugin(plugin);
            } catch (final InvalidPluginException e) {
                e.printStackTrace();
            } catch (final InvalidDescriptionException e) {
                e.printStackTrace();
            }
        }, "reload");
        this.addExecutor(0, (sender, args) -> {
        }, "test");
    }

    @Override
    public void usage(final CommandSender commandSender) {
        commandSender.sendMessage(DropItemConfiguration.getMessage("InvalidArg"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("Welcome"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("CommandClean"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("CommandCleanAll"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("CommandDisable"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("CommandUpdate"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("CommandDownload"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("CommandUpdateNow"));
        commandSender.sendMessage(DropItemConfiguration.getMessage("CommandReload"));
    }
}
