package com.focess.dropitem.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.HopperGottenEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.google.common.collect.Lists;

public class DropItemUtil {

    private static boolean allowedPlayer = false;

    private static List<String> allowedPlayers;

    @SuppressWarnings("unused")
    private static int anxiCode;

    private static final List<Material> BanItems = new ArrayList<>();

    private static boolean debug;

    private static String dropForm;

    private static boolean enableAliases;

    private static boolean enableCoverBlock;

    private static double height;

    private static boolean naturalSpawn;

    private static String pickForm;

    private static boolean showItemInfo;

    public static boolean allowedPlayer() {
        return DropItemUtil.allowedPlayer;
    }

    public static boolean checkAliases() {
        return DropItemUtil.enableAliases;
    }

    public static boolean checkAllowedPlayer(final String name) {
        return DropItemUtil.allowedPlayers.contains(name);
    }

    public static boolean checkBanItems(final ItemStack itemStack) {
        if (DropItemUtil.BanItems.contains(itemStack.getType()))
            return false;
        return true;
    }

    public static boolean checkCoverBlock() {
        return DropItemUtil.enableCoverBlock;
    }

    public static boolean checkDebug() {
        return DropItemUtil.debug;
    }

    public static boolean checkDropForm(final String form) {
        return DropItemUtil.dropForm.equals(form);
    }

    public static boolean checkNull(final String name) {
        if (name != null) {
            if (!name.startsWith(ChatColor.RED + "QuickShop") && !name.contains("GSCompleXMoneyFromMobzzzzzzzzz")
                    && !name.contains("XXXPlayer777MoneyXXX"))
                return true;
            return false;
        }
        return true;
    }

    public static boolean checkPickForm(final String form) {
        return DropItemUtil.pickForm.equals(form);
    }

    public static boolean checkPlayerPermission(final Player player) {
        return player.hasPermission("dropitem.use");
    }

    public static void fillHopperInventory(final Hopper hopper, final EntityDropItem entityDropItem) {
        if (!hopper.getInventory().containsAtLeast(entityDropItem.getItemStack(), 1)
                && hopper.getInventory().firstEmpty() == -1)
            return;
        final Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER);
        for (int i = 0; i < hopper.getInventory().getContents().length; i++) {
            if (hopper.getInventory().getItem(i) == null)
                continue;
            final ItemStack itemStack = hopper.getInventory().getItem(i).clone();
            if (itemStack != null)
                inventory.setItem(i, itemStack);
        }
        final Map<Integer, ItemStack> itemStacks = inventory.addItem(entityDropItem.getItemStack().clone());
        final ItemStack item = entityDropItem.getItemStack().clone();
        if (!itemStacks.isEmpty())
            item.setAmount(item.getAmount() - itemStacks.get(0).getAmount());
        if (item.getAmount() == 0)
            return;
        final HopperGottenEvent event = new HopperGottenEvent(item, hopper);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            CraftDropItem.remove(entityDropItem, DropItemDeathEvent.DeathCause.HOPPER_GOTTEN);
            hopper.getInventory().addItem(event.getItemStack());
            if (item.getAmount() != entityDropItem.getItemStack().getAmount())
                CraftDropItem.spawnItem(itemStacks.get(0), entityDropItem.getLocation().clone().add(0, 1, 0), false);
        }
    }

    public static void fillPlayerInventory(final Player player, final EntityDropItem entityDropItem) {
        if (!player.getInventory().containsAtLeast(entityDropItem.getItemStack(), 1)
                && player.getInventory().firstEmpty() == -1)
            return;
        final Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER);
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            if (player.getInventory().getItem(i) == null)
                continue;
            final ItemStack itemStack = player.getInventory().getItem(i).clone();
            if (itemStack != null)
                inventory.setItem(i, itemStack);
        }
        final Map<Integer, ItemStack> itemStacks = inventory.addItem(entityDropItem.getItemStack().clone());
        final ItemStack item = entityDropItem.getItemStack().clone();
        if (!itemStacks.isEmpty())
            item.setAmount(item.getAmount() - itemStacks.get(0).getAmount());
        if (item.getAmount() == 0)
            return;
        final PlayerGottenEvent event = new PlayerGottenEvent(item, player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            CraftDropItem.remove(entityDropItem, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
            player.getInventory().addItem(event.getItemStack());
            if (item.getAmount() != entityDropItem.getItemStack().getAmount())
                CraftDropItem.spawnItem(itemStacks.get(0), entityDropItem.getLocation().clone().add(0, 1, 0), false);
        }
    }

    public static void forceDelete(final File file) {
        if (file.isFile())
            file.delete();
        else
            for (final File f : file.listFiles())
                DropItemUtil.forceDelete(f);
    }

    @SuppressWarnings("deprecation")
    private static void getBanItems(final DropItem drop) {
        final String banItems = drop.getConfig().getString("BanItem");
        for (final String banItem : banItems.split(","))
            try {
                final int id = Integer.parseInt(banItem);
                if (Material.getMaterial(id) == null)
                    continue;
                DropItemUtil.BanItems.add(Material.getMaterial(id));
            } catch (final Exception e) {
                if (Material.getMaterial(banItem) == null)
                    continue;
                DropItemUtil.BanItems.add(Material.getMaterial(banItem));
            }
    }

    public static double getHeight() {
        return DropItemUtil.height;
    }

    public static void loadDefault(final DropItem drop) {
        DropItemUtil.anxiCode = AnxiCode.getCode(DropItemUtil.class, drop);
        DropItemUtil.height = drop.getConfig().getDouble("Height", 0.3d);
        DropItemUtil.pickForm = drop.getConfig().getString("PickForm", "normal");
        DropItemUtil.dropForm = drop.getConfig().getString("DropForm", "normal");
        DropItemUtil.showItemInfo = drop.getConfig().getBoolean("ShowItemInfo", true);
        DropItemUtil.naturalSpawn = drop.getConfig().getBoolean("NaturalSpawn", true);
        if (!DropItemUtil.naturalSpawn)
            DropItemUtil.allowedPlayer = drop.getConfig().getBoolean("AllowedPlayer", false);
        DropItemUtil.allowedPlayers = Lists.newArrayList(drop.getConfig().getString("AllowedPlayers").split(","));
        DropItemUtil.getBanItems(drop);
        DropItemUtil.enableCoverBlock = drop.getConfig().getBoolean("EnableCoverBlock", false);
        DropItemUtil.enableAliases = drop.getConfig().getBoolean("EnableAliases", true);
        DropItemUtil.debug = drop.getConfig().getBoolean("Debug2", false);
    }

    public static boolean naturalSpawn() {
        return DropItemUtil.naturalSpawn;
    }

    public static boolean showItemInfo() {
        return DropItemUtil.showItemInfo;
    }

}
