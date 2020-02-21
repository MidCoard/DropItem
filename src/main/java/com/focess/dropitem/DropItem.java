package com.focess.dropitem;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.focess.dropitem.commnad.DropItemCommand;
import com.focess.dropitem.item.CraftAIListener;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.DropItemInfo;
import com.focess.dropitem.listener.DropItemPermissionListener;
import com.focess.dropitem.listener.PlayerInteractListener;
import com.focess.dropitem.listener.PlayerMoveListener;
import com.focess.dropitem.listener.RemoveDropItemListener;
import com.focess.dropitem.listener.SpawnDropItemListener;
import com.focess.dropitem.runnable.DropItemRunnable;
import com.focess.dropitem.runnable.SpawnDropItemRunnable;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.Command;
import com.focess.dropitem.util.DropItemUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DropItem extends JavaPlugin {

    private static int anxiCode;
    private static List<BukkitTask> bukkitTasks = Lists.newArrayList();
    private static DropItem instance;
    private static Map<String, String> messages = Maps.newHashMap();
    public static Map<String, String> Slanguages = Maps.newHashMap();
    public static Map<String, String> Tlanguages = Maps.newHashMap();

    public static DropItem getInstance() {
        return DropItem.instance;
    }

    public static String getMessage(final String message) {
        final String ret = DropItem.messages.get(message);
        if (ret == null)
            return "";
        else
            return ret;
    }

    private final BukkitScheduler bukkitScheduler = this.getServer().getScheduler();

    private CraftAIListener craftAIListener;

    private final PluginManager pluginManager = this.getServer().getPluginManager();

    public CraftAIListener getCraftAIListener(final int anxiCode) {
        if (DropItem.anxiCode == anxiCode)
            return this.craftAIListener;
        AnxiCode.shut(DropItem.class);
        return null;
    }

    private void getLanguage() {
        final File Slanguage = new File(this.getDataFolder(), "language-zhs.yml");
        final YamlConfiguration Syaml = YamlConfiguration.loadConfiguration(Slanguage);
        final File Tlanguage = new File(this.getDataFolder(), "language-zht.yml");
        final YamlConfiguration Tyaml = YamlConfiguration.loadConfiguration(Tlanguage);
        for (final String key : Syaml.getKeys(false))
            DropItem.Slanguages.put(key, Syaml.getString(key));
        for (final String key : Tyaml.getKeys(false))
            DropItem.Tlanguages.put(key, Tyaml.getString(key));
    }

    public void loadConfig() {
        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        final File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.saveDefaultConfig();
            this.reloadConfig();
        }
        this.saveResource("message.yml", false);
        this.saveResource("language-zhs.yml", false);
        this.saveResource("language-zht.yml", false);
        final File drops = new File(this.getDataFolder(), "drops");
        if (!drops.exists())
            drops.mkdir();
        final File bugs = new File(this.getDataFolder(), "bugs");
        if (!bugs.exists())
            bugs.mkdir();
        final File messFile = new File(this.getDataFolder(), "message.yml");
        final YamlConfiguration yml = YamlConfiguration.loadConfiguration(messFile);
        final Set<String> keys = yml.getKeys(false);
        for (final String key : keys)
            DropItem.messages.put(key, yml.getString(key));
        this.getLanguage();
    }

    @Override
    public void onDisable() {
        this.craftAIListener.getLoadTask().cancel();
        this.craftAIListener.getStartTask().cancel();
        for (final BukkitTask bukkitTask : DropItem.bukkitTasks)
            bukkitTask.cancel();
        CraftAIListener.reload(DropItem.anxiCode);
        CraftDropItem.uploadItems(DropItem.anxiCode);
        AnxiCode.reload(DropItem.anxiCode);
        this.getLogger().info("DropItem插件载出成功");
    }

    @Override
    public void onEnable() {
        DropItem.instance = this;
        new AnxiCode(this);
        DropItem.anxiCode = AnxiCode.getCode(DropItem.class, this);
        this.loadConfig();
        this.getLogger().info("DropItem插件载入成功");
        DropItemUtil.loadDefault(this);
        DropItemInfo.register(this, DropItem.anxiCode);
        CraftDropItem.loadItem(this);
        this.pluginManager.registerEvents(new RemoveDropItemListener(), this);
        this.pluginManager.registerEvents(new PlayerMoveListener(this), this);
        this.pluginManager.registerEvents(new SpawnDropItemListener(), this);
        this.pluginManager.registerEvents(new DropItemPermissionListener(this), this);
        this.pluginManager.registerEvents(new PlayerInteractListener(this), this);
        this.registerPermission();
        DropItem.bukkitTasks.add(this.bukkitScheduler.runTaskTimer(this, new SpawnDropItemRunnable(), 0, 10l));
        DropItem.bukkitTasks
                .add(this.bukkitScheduler.runTaskTimer(this, (Runnable) new DropItemRunnable(this), 0L, 10L));
        this.craftAIListener = new CraftAIListener(this, DropItem.anxiCode);
        Command.register(new DropItemCommand(DropItem.anxiCode, this));
    }

    private void registerPermission() {
        boolean isregister = false;
        for (final Permission perm : this.pluginManager.getPermissions())
            if (perm.getName().equals("dropitem.command") || perm.getName().equals("dropitem.use"))
                isregister = true;
        if (!isregister) {
            this.pluginManager.addPermission(new Permission("dropitem.command"));
            this.pluginManager.addPermission(new Permission("dropitem.use"));
        }
        if (this.getConfig().getBoolean("AllowedPlayers", true)) {
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds) {
                final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
                for (final Entity player : players)
                    ((Player) player).addAttachment(this).setPermission("dropitem.use", true);
            }
        } else {
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds) {
                final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
                for (final Entity player : players)
                    ((Player) player).addAttachment(this).setPermission("dropitem.use", false);
            }
        }
        final List<String> allowedPlayers = Lists.newArrayList(this.getConfig().getString("AllowedPlayer").split(","));
        final List<World> worlds = Bukkit.getWorlds();
        for (final World world : worlds) {
            final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
            for (final Entity player : players)
                if (allowedPlayers.contains(((Player) player).getName()))
                    ((Player) player).addAttachment(this).setPermission("dropitem.use", true);
        }
    }
}
