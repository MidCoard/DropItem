package com.focess.dropitem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
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
import com.focess.dropitem.listener.EntityDeathListener;
import com.focess.dropitem.listener.PlayerDropItemListener;
import com.focess.dropitem.listener.PlayerInteractListener;
import com.focess.dropitem.listener.PlayerJoinListener;
import com.focess.dropitem.listener.PlayerMoveListener;
import com.focess.dropitem.runnable.DropItemRunnable;
import com.focess.dropitem.runnable.PlayerAroundDropItemRunnable;
import com.focess.dropitem.runnable.SpawnDropItemRunnable;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.DropItemUtil;

public class DropItem extends JavaPlugin {
    private static int anxiCode;
    private static List<BukkitTask> bukkitTasks = new ArrayList<>();
    public static HashMap<String, String> Slanguages = new HashMap<>();
    public static HashMap<String, String> Tlanguages = new HashMap<>();

    public static int getVersion() {
        final String v = Bukkit.getServer().getClass().getPackage().getName();
        final String version = v.substring(v.lastIndexOf('.') + 1).split("_")[1];
        return Integer.parseInt(version);
    }

    private final BukkitScheduler bukkitScheduler = this.getServer().getScheduler();
    public CommandMap commandMap;
    private CraftAIListener craftAIListener;
    public final boolean islower;

    private final PluginManager pluginManager = this.getServer().getPluginManager();

    {
        if (DropItem.getVersion() < 8)
            this.islower = true;
        else
            this.islower = false;
        try {
            this.getCommandMap();
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in getting CommandMap Instance.");
        }
    }

    private void getCommandMap() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Class<?> c = Bukkit.getServer().getClass();
        for (final Method method : c.getDeclaredMethods())
            if (method.getName().equals("getCommandMap"))
                this.commandMap = (CommandMap) method.invoke(this.getServer(), new Object[0]);
    }

    public CraftAIListener getCraftAIListener(final int anxiCode) {
        try {
            if (DropItem.anxiCode == anxiCode)
                return this.craftAIListener;
            AnxiCode.shut(DropItem.class);
            return null;
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in getting CraftAIListener Instance.");
            return null;
        }
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
        try {
            if (!this.getDataFolder().exists())
                this.getDataFolder().mkdir();
            final File file = new File(this.getDataFolder(), "config.yml");
            if (!file.exists()) {
                this.saveDefaultConfig();
                this.reloadConfig();
            }
            this.loadFile(new File(this.getDataFolder(), "message.yml"), "message.yml");
            this.loadFile(new File(this.getDataFolder(), "language-zhs.yml"), "language-zhs.yml");
            this.loadFile(new File(this.getDataFolder(), "language-zht.yml"), "language-zht.yml");
            final File drops = new File(this.getDataFolder(), "drops");
            if (!drops.exists())
                drops.mkdir();
            final File bugs = new File(this.getDataFolder(), "bugs");
            if (!bugs.exists())
                bugs.mkdir();
            this.getLanguage();
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in loading config.");
        }
    }

    private void loadFile(final File targetFile, final String loadingFile) {
        try {
            if (targetFile.exists())
                return;
            String jarFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            try {
                jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
                final JarFile jar = new JarFile(jarFilePath);
                InputStream is = null;
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    if (entry.getName().equals(loadingFile)) {
                        is = jar.getInputStream(entry);
                        break;
                    }
                }
                final FileOutputStream out = new FileOutputStream(targetFile);
                int c = 0;
                while ((c = is.read()) != -1)
                    out.write(c);
                out.close();
                jar.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in loading JarFile.");
        }
    }

    @Override
    public void onDisable() {
        try {
            this.craftAIListener.getLoadTask().cancel();
            this.craftAIListener.getStartTask().cancel();
            for (final BukkitTask bukkitTask : DropItem.bukkitTasks)
                bukkitTask.cancel();
            this.loadConfig();
            CraftAIListener.reload(DropItem.anxiCode);
            CraftDropItem.uploadItems(DropItem.anxiCode);
            Debug.reload(DropItem.anxiCode);
            AnxiCode.reload(DropItem.anxiCode);
            this.getLogger().info("DropItem插件载出成功");
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in disabling Plugin DropItem.");
        }
    }

    @Override
    public void onEnable() {
        new AnxiCode(this);
        DropItem.anxiCode = AnxiCode.getCode(DropItem.class, this);
        this.loadConfig();
        if (this.getConfig().getBoolean("Debug", false))
            Debug.debug(this, DropItem.anxiCode);
        this.getLogger().info("DropItem插件载入成功");
        DropItemUtil.loadDefault(this);
        DropItemInfo.register(this, DropItem.anxiCode);
        CraftDropItem.loadItem(this);
        this.pluginManager.registerEvents(new EntityDeathListener(this), this);
        this.pluginManager.registerEvents(new PlayerMoveListener(this), this);
        this.pluginManager.registerEvents(new PlayerDropItemListener(this), this);
        this.pluginManager.registerEvents(new PlayerJoinListener(this), this);
        this.pluginManager.registerEvents(new PlayerInteractListener(), this);
        this.registerPermission();
        DropItem.bukkitTasks
                .add(this.bukkitScheduler.runTaskTimer(this, (Runnable) new PlayerAroundDropItemRunnable(this), 0L, 10L));
        DropItem.bukkitTasks.add(this.bukkitScheduler.runTaskTimer(this, (Runnable) new SpawnDropItemRunnable(this), 0L, 5L));
        DropItem.bukkitTasks
                .add(this.bukkitScheduler.runTaskTimer(this, (Runnable) new DropItemRunnable(this), 0L, 5L));
        this.craftAIListener = new CraftAIListener(this, DropItem.anxiCode);
        this.commandMap.register(this.getDescription().getName(), new DropItemCommand("", "", DropItem.anxiCode, this));
    }

    private void registerPermission() {
        try {
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
            final List<String> allowedPlayers = DropItemUtil.toList(this.getConfig().getString("AllowedPlayer").split(","));
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds) {
                final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
                for (final Entity player : players)
                    if (allowedPlayers.contains(((Player) player).getName()))
                        ((Player) player).addAttachment(this).setPermission("dropitem.use", true);
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in registering permissions.");
        }
    }
}
