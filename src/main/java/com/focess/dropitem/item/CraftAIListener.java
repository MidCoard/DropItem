package com.focess.dropitem.item;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CraftAIListener {
    private static BukkitTask loadTask;
    private static BukkitTask startTask;

    private static final Map<UUID, Pair<Location, ItemStack>> ais = new ConcurrentHashMap<>();
    private static boolean isStart;

    public CraftAIListener(final DropItem drop) {
        if (CraftAIListener.isStart)
            System.err.println("DropItemAI已经启动");
        this.drop = drop;
        CraftAIListener.isStart = true;
        this.init();
    }

    protected static void remove(final UUID uuid) {
        CraftAIListener.ais.remove(uuid);
    }

    private final DropItem drop;

    public static void reload() {
        CraftAIListener.isStart = false;
        loadTask.cancel();
        startTask.cancel();
    }

    public void clear() {
        CraftAIListener.ais.clear();
    }

    public Map<UUID, Pair<Location, ItemStack>> getAIs() {
        return CraftAIListener.ais;
    }

    private void init() {
        startTask = this.drop.getServer().getScheduler().runTaskTimer(this.drop, (Runnable) new DropItemAI(), 0L,
                20L);
        loadTask = this.drop.getServer().getScheduler().runTaskTimer(this.drop, (Runnable) new DropItemAILoad(),
                0L, 40L);
    }

    private static class DropItemAI extends BukkitRunnable {

        @Override
        public void run() {
            for (final EntityDropItem dropItem : CraftDropItem.getDropItems())
                if (!dropItem.isDead()) {
                    final List<Entity> entities = dropItem.getNearbyEntities(12.0D, 12.0D, 12.0D);
                    boolean flag = false;
                    for (final Entity entity : entities)
                        if (entity instanceof Player
                                && !((Player) entity).getGameMode().equals(GameMode.SPECTATOR)) {
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

    private static class DropItemAILoad extends BukkitRunnable {

        @Override
        public void run() {
            final List<World> worlds = Bukkit.getWorlds();
            for (final World world : worlds) {
                final Collection<Entity> players = world.getEntitiesByClasses(Player.class);
                for (final Entity player : players)
                    if (player instanceof Player && !((Player) player).getGameMode().equals(GameMode.SPECTATOR))
                        for (final UUID uuid : CraftAIListener.ais.keySet()) {
                            final Pair<Location, ItemStack> pair = CraftAIListener.ais.get(uuid);
                            final Location location = pair.getKey();
                            if (location.getWorld().getName().equals(player.getWorld().getName())
                                    && location.distance(player.getLocation()) < 12.0D) {
                                final ItemStack itemStack = pair.getValue();
                                final Location temp = new Location(location.getWorld(), location.getX(),
                                        location.getY() + 1.0D, location.getZ());
                                final EntityDropItem dropItem = CraftDropItem.spawnItem(itemStack, temp, false, false);
                                if (dropItem == null)
                                    continue;
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
}