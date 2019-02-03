package com.focess.dropitem.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;

public class DropItemUtil {

    private static boolean allowedPlayer = false;

    private static List<String> allowedPlayers;

    @SuppressWarnings("unused")
    private static int anxiCode;

    private static final List<Material> BanItems = new ArrayList<>();

    private static String dropForm;

    private static double height;

    private static boolean naturalSpawn;

    private static String pickForm;

    private static boolean showItemInfo;

    public static boolean allowedPlayer() {
        return DropItemUtil.allowedPlayer;
    }

    public static boolean checkAllowedPlayer(final String name) {
        return DropItemUtil.allowedPlayers.contains(name);
    }

    public static boolean checkBanItems(final ItemStack itemStack) {
        if (DropItemUtil.BanItems.contains(itemStack.getType()))
            return false;
        return true;
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

    public static void fillPlayerInventory(final Player player, final EntityDropItem entityDropItem) {
        if (player.getInventory().firstEmpty() != -1) {
            final PlayerGottenEvent event_gotten = new PlayerGottenEvent(entityDropItem.getItemStack(), player);
            Bukkit.getServer().getPluginManager().callEvent(event_gotten);
            if (!event_gotten.isCancelled()) {
                player.getInventory().addItem(new ItemStack[] { entityDropItem.getItemStack() });
                CraftDropItem.remove(entityDropItem, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
            }
        } else if (!player.getInventory().contains(entityDropItem.getItemStack()))
            return;
        else {
            int count = 0;
            final ItemStack is = entityDropItem.getItemStack();
            int amount = 0;
            for (final ItemStack itemStack : player.getInventory())
                if (itemStack != null)
                    if (itemStack.isSimilar(is)) {
                        count += itemStack.getAmount();
                        amount++;
                    }
            if (((amount * entityDropItem.getItemStack().getMaxStackSize()) - count) != 0) {
                final PlayerGottenEvent event_gotten = new PlayerGottenEvent(entityDropItem.getItemStack(), player);
                Bukkit.getServer().getPluginManager().callEvent(event_gotten);
                if (!event_gotten.isCancelled()) {
                    player.getInventory().addItem(new ItemStack[] { entityDropItem.getItemStack() });
                    CraftDropItem.remove(entityDropItem, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
                }
            } else
                return;
        }
    }

    @SuppressWarnings("deprecation")
    private static void getBanItems(final DropItem drop) {
        try {
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
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in getting BanItems.");
        }
    }

    public static double getHeight() {
        return DropItemUtil.height;
    }

    public static void loadDefault(final DropItem drop) {
        DropItemUtil.anxiCode = AnxiCode.getCode(DropItemUtil.class, drop);
        DropItemUtil.height = drop.getConfig().getDouble("Height");
        DropItemUtil.pickForm = drop.getConfig().getString("PickForm");
        DropItemUtil.dropForm = drop.getConfig().getString("DropForm");
        DropItemUtil.showItemInfo = drop.getConfig().getBoolean("ShowItemInfo");
        DropItemUtil.naturalSpawn = drop.getConfig().getBoolean("NaturalSpawn", true);
        if (!DropItemUtil.naturalSpawn)
            DropItemUtil.allowedPlayer = drop.getConfig().getBoolean("AllowedPlayer", false);
        DropItemUtil.allowedPlayers = DropItemUtil.toList(drop.getConfig().getString("AllowedPlayers").split(","));
        DropItemUtil.getBanItems(drop);
    }

    public static boolean naturalSpawn() {
        return DropItemUtil.naturalSpawn;
    }

    public static boolean showItemInfo() {
        return DropItemUtil.showItemInfo;
    }

    public static List<String> toList(final String[] list) {
        final List<String> ret = new ArrayList<>();
        for (final String element : list)
            ret.add(element);
        return ret;
    }

}
