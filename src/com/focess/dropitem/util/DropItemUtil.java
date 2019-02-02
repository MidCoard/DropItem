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
    
    private static List<String> allowedPlayers;

    private static final List<Material> BanItems = new ArrayList<>();
    
    private static String pickForm;
    
    private static String dropForm;

    @SuppressWarnings("unused")
    private static int anxiCode;

    private static boolean naturalSpawn;

    private static boolean allowedPlayer = false;

    private static boolean showItemInfo;
    
    private static double height;

    public static void loadDefault(DropItem drop) {
        anxiCode = AnxiCode.getCode(DropItemUtil.class, drop);
        height = drop.getConfig().getDouble("Height");
        pickForm = drop.getConfig().getString("PickForm");
        dropForm = drop.getConfig().getString("DropForm");
        showItemInfo = drop.getConfig().getBoolean("ShowItemInfo");
        naturalSpawn = drop.getConfig().getBoolean("NaturalSpawn", true);
        if (!naturalSpawn)
            allowedPlayer  = drop.getConfig().getBoolean("AllowedPlayer", false);
        allowedPlayers = toList(drop.getConfig().getString("AllowedPlayers").split(","));
        getBanItems(drop);
    }

    @SuppressWarnings("deprecation")
    private static void getBanItems(final DropItem drop) {
        try {
            final String banItems = drop.getConfig().getString("BanItem");
            for (final String banItem : banItems.split(",")) {
                try {
                    int id = Integer.parseInt(banItem);
                    if (Material.getMaterial(id) == null)
                        continue;
                    BanItems.add(Material.getMaterial(id));
                } catch (Exception e) {
                    if (Material.getMaterial(banItem) == null)
                        continue;
                    BanItems.add(Material.getMaterial(banItem));
                }
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in getting BanItems.");
        }
    }

    public static List<String> toList(String[] list) {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < list.length; i++)
            ret.add(list[i]);
        return ret;
    }

    public static boolean checkBanItems(ItemStack itemStack) {
        if (BanItems.contains(itemStack.getType()))
            return false;
        return true;
    }

    public static void fillPlayerInventory(Player player, EntityDropItem entityDropItem) {
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

    public static boolean checkPickForm(String form) {
        return pickForm.equals(form);
    }

    public static boolean naturalSpawn() {
        return naturalSpawn;
    }
    
    public static boolean allowedPlayer() {
        return allowedPlayer;
    }

    public static boolean checkPlayerPermission(Player player) {
        return player.hasPermission("dropitem.use");
    }
    
    public static boolean checkNull(String name) {
        if (name != null) {
            if (!name.startsWith(ChatColor.RED + "QuickShop") && !name.contains("GSCompleXMoneyFromMobzzzzzzzzz")
                    && !name.contains("XXXPlayer777MoneyXXX"))
                return true;
            return false;
        }
        return true;
    }

    public static boolean checkAllowedPlayer(String name) {
        return allowedPlayers.contains(name);
    }

    public static boolean showItemInfo() {
        return showItemInfo;
    }
    
    public static double getHeight() {
        return height;
    }

}
