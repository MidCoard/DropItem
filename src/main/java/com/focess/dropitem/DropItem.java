package com.focess.dropitem;

import com.focess.dropitem.commnad.DropItemCommand;
import com.focess.dropitem.item.CraftAIListener;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.DropItemInfo;
import com.focess.dropitem.listener.*;
import com.focess.dropitem.runnable.DropItemRunnable;
import com.focess.dropitem.runnable.SpawnDropItemRunnable;
import com.focess.dropitem.runnable.WaitingRunnable;
import com.focess.dropitem.util.NMSManager;
import com.focess.dropitem.util.command.Command;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import com.focess.dropitem.util.version.ConfigUpdater;
import com.focess.dropitem.util.version.Version;
import com.focess.dropitem.util.version.VersionUpdateReplacement;
import com.focess.dropitem.util.version.VersionUpdater;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

public class DropItem extends JavaPlugin {

    private static final List<BukkitTask> bukkitTasks = Lists.newArrayList();

    private final List<Thread> threads = Lists.newArrayList();

    private VersionUpdateReplacement versionUpdateReplacement;

    public Thread addThread(final Thread thread) {
        this.threads.add(thread);
        return thread;
    }

    private static DropItem instance;

    public static DropItem getInstance() {
        return DropItem.instance;
    }

    private final BukkitScheduler bukkitScheduler = this.getServer().getScheduler();

    private CraftAIListener craftAIListener;

    private final PluginManager pluginManager = this.getServer().getPluginManager();

    private Version version;

    private boolean isLoaded;

    public CraftAIListener getCraftAIListener() {
        return this.craftAIListener;
    }

    public void loadConfig() {
        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        final File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists())
            this.saveDefaultConfig();
        else if (!new Version(this.getConfig().getString("Version", "7.4")).isNew(this.version))
            ConfigUpdater.updateConfig(this, new Version(this.getConfig().getString("Version", "7.4")), this.version);
    }

    @Override
    public void onDisable() {
        if (!this.isLoaded)
            return;
        for (final BukkitTask bukkitTask : DropItem.bukkitTasks)
            bukkitTask.cancel();
        if (DropItemConfiguration.isDropItemAI())
            CraftAIListener.reload();
        DropItemInfo.reload();
        CraftDropItem.uploadItems();
        for (final Thread thread : this.threads)
            thread.stop();
        this.isLoaded = false;
        this.closeResource();
        VersionUpdater.update(this);
    }

    private void closeResource() {
        final URLClassLoader loader = (URLClassLoader) this.getClassLoader();
        try {
            loader.close();
            System.gc();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        if (NMSManager.getVersionInt() < 8)
            this.getServer().getPluginManager().disablePlugin(this);
        DropItem.instance = this;
        this.version = new Version(this.getDescription().getVersion());
        this.loadConfig();
        DropItemConfiguration.loadDefault(this);
        CraftDropItem.loadItem(this);
        this.pluginManager.registerEvents(new RemoveDropItemListener(), this);
        this.pluginManager.registerEvents(new PlayerMoveListener(), this);
        this.pluginManager.registerEvents(new SpawnDropItemListener(), this);
        this.pluginManager.registerEvents(new DropItemPermissionListener(this), this);
        this.pluginManager.registerEvents(new PlayerInteractListener(), this);
        this.registerPermission();
        DropItem.bukkitTasks.add(this.bukkitScheduler.runTaskTimer(this, new SpawnDropItemRunnable(), 0, 10L));
        DropItem.bukkitTasks
                .add(this.bukkitScheduler.runTaskTimer(this, (Runnable) new DropItemRunnable(this), 0L, 10L));
        DropItem.bukkitTasks.add(this.bukkitScheduler.runTaskTimer(this,new WaitingRunnable(),0L,1L));
        if (DropItemConfiguration.isDropItemAI())
            this.craftAIListener = new CraftAIListener(this);
        if (DropItemConfiguration.isRefresh())
            DropItemInfo.loadDefault(this);
        Command.register(new DropItemCommand(this));
        if (DropItemConfiguration.isVersionCheck())
            DropItem.this.addThread(new Thread() {
                public void run() {
                    VersionUpdater.checkForUpdate(DropItem.this);
                    if (VersionUpdater.isNeedUpdated() && !VersionUpdater.isDownloaded() && DropItemConfiguration.isVersionDownload())
                        VersionUpdater.downloadNewVersion(DropItem.this, this);
                }
            }).start();
        this.isLoaded = true;
    }

    private void registerPermission() {
        boolean isRegister = false;
        for (final Permission perm : this.pluginManager.getPermissions())
            if (perm.getName().equals("dropitem.command") || perm.getName().equals("dropitem.use"))
                isRegister = true;
        if (!isRegister) {
            this.pluginManager.addPermission(new Permission("dropitem.command"));
            this.pluginManager.addPermission(new Permission("dropitem.use"));
        }
        if (this.getConfig().getBoolean("AllowedPlayers", true)) {
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds) {
                final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
                for (final Entity e : players) {
                    final Player player = (Player) e;
                    player.addAttachment(this).setPermission("dropitem.use", true);
                }
            }
        } else {
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds) {
                final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
                for (final Entity e : players) {
                    final Player player = (Player) e;
                    player.addAttachment(this).setPermission("dropitem.use", false);
                }
            }
        }
        final List<String> allowedPlayers = Lists.newArrayList(this.getConfig().getString("AllowedPlayer").split(","));
        final List<World> worlds = Bukkit.getWorlds();
        for (final World world : worlds) {
            final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
            for (final Entity e : players) {
                final Player player = (Player) e;
                if (allowedPlayers.contains(player.getName()))
                    player.addAttachment(this).setPermission("dropitem.use", true);
            }
        }
    }

    public Version getVersion() {
        return this.version;
    }

    public VersionUpdateReplacement getVersionUpdateReplacement() {
        return this.versionUpdateReplacement;
    }

    public void setVersionUpdateReplacement(final VersionUpdateReplacement versionUpdateReplacement) {
        this.versionUpdateReplacement = versionUpdateReplacement;
    }
}
