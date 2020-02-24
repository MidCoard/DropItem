package com.focess.dropitem.item;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.Pair;

public class CraftAIListener {
    private static class DropItemAI extends BukkitRunnable {
        private DropItemAI() {
        }

        @Override
        public void run() {
            for (final EntityDropItem dropItem : CraftDropItem.getDropItems(CraftAIListener.anxiCode))
                if (!dropItem.isDead()) {
                    final List<Entity> entities = dropItem.getNearbyEntities(12.0D, 12.0D, 12.0D);
                    boolean flag = false;
                    for (final Entity entity : entities)
                        if (entity instanceof Player
                                && ((Player) entity).getGameMode().compareTo(GameMode.SPECTATOR) != 0) {
                            flag = true;
                            break;
                        }
                    if (!flag) {
                        final DropItemInfo dropItemInfo = DropItemInfo.getDropItemInfo(dropItem.getUniqueId());
                        dropItemInfo.setAlive(false);
                        CraftAIListener.ais.put(dropItem.getUniqueId(),
                                Pair.of(dropItem.getLocation(), dropItem.getItemStack()));
                        CraftDropItem.hide(dropItem);
                    }
                }
        }
    }

    private static class DropItemAIload extends BukkitRunnable {
        private DropItemAIload() {
        }

        @Override
        public void run() {
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds) {
                final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
                for (final Entity player : players)
                    if (player instanceof Player && ((Player) player).getGameMode().compareTo(GameMode.SPECTATOR) != 0)
                        for (final UUID uuid : CraftAIListener.ais.keySet()) {
                            final Pair<Location, ItemStack> pair = CraftAIListener.ais.get(uuid);
                            final Location location = pair.getKey();
                            if (location.getWorld().getName().equals(player.getWorld().getName())
                                    && location.distance(player.getLocation()) < 12.0D) {
                                final ItemStack itemStack = pair.getValue();
                                final Location temp = new Location(location.getWorld(), location.getX(),
                                        location.getY() + 1.0D, location.getZ());
                                final EntityDropItem dropItem = CraftDropItem.spawnItem(itemStack, temp, false, false);
                                final DropItemInfo dropItemInfo = DropItemInfo.getDropItemInfo(uuid);
                                dropItemInfo.setDropItem(dropItem);
                                dropItemInfo.setAlive(true);
                                DropItemInfo.remove(uuid);
                                CraftAIListener.ais.remove(uuid);
                            }
                        }
            }
        }

    }

    private static final Map<UUID, Pair<Location, ItemStack>> ais = new ConcurrentHashMap<>();
    private static int anxiCode;
    private static boolean isStart;

    public static void reload(final int anxiCode) {
        if (CraftAIListener.anxiCode == anxiCode)
            CraftAIListener.isStart = false;
        else
            AnxiCode.shut(AnxiCode.class);
    }

    protected static void remove(final UUID uuid) {
        CraftAIListener.ais.remove(uuid);
    }

    private final DropItem drop;

    private BukkitTask loadTask;

    private BukkitTask startTask;

    public CraftAIListener(final DropItem dropItem, final int anxiCode) {
        CraftAIListener.anxiCode = AnxiCode.getCode(CraftAIListener.class, dropItem);
        if (anxiCode != CraftAIListener.anxiCode)
            AnxiCode.shut(CraftAIListener.class);
        if (CraftAIListener.isStart) {
            System.err.println("DropItemAI已经启动");
            AnxiCode.shut(CraftAIListener.class);
        }
        this.drop = dropItem;
        CraftAIListener.isStart = true;
        this.init();
    }

    public void clear(final int anxiCode) {
        if (CraftAIListener.anxiCode == anxiCode)
            CraftAIListener.ais.clear();
        else
            AnxiCode.shut(CraftAIListener.class);
    }

    public Map<UUID, Pair<Location, ItemStack>> getAIs(final int anxiCode) {
        if (CraftAIListener.anxiCode == anxiCode)
            return CraftAIListener.ais;
        AnxiCode.shut(CraftAIListener.class);
        return null;
    }

    public BukkitTask getLoadTask() {
        return this.loadTask;
    }

    public BukkitTask getStartTask() {
        return this.startTask;
    }

    private void init() {
        this.startTask = this.drop.getServer().getScheduler().runTaskTimer(this.drop, (Runnable) new DropItemAI(), 0L,
                20L);
        this.loadTask = this.drop.getServer().getScheduler().runTaskTimer(this.drop, (Runnable) new DropItemAIload(),
                0L, 40L);
    }
}