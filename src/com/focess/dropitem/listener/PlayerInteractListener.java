package com.focess.dropitem.listener;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.NMSManager;

public class PlayerInteractListener implements Listener {

	private final int anxiCode;

	public PlayerInteractListener(final DropItem drop) {
		this.anxiCode = AnxiCode.getCode(PlayerInteractListener.class, drop);
	}

	private boolean buildBlock(final Player player, final ItemStack itemStack) {
		try {
			if (DropItemUtil.checkDebug())
				player.sendMessage("111111111111");
			final BlockIterator i;
			if (player.getGameMode().equals(GameMode.SURVIVAL))
				i = new BlockIterator(player, 4);
			else
				i = new BlockIterator(player, 5);
			Block last = null;
			Block now = null;
			while (i.hasNext())
				if (!(now = i.next()).getType().equals(Material.AIR))
					break;
				else
					last = now;
			if (last == null)
				return false;
			else
				this.placeBlock(last, player, itemStack);
			return true;
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in building block.");
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private boolean buildBlock2(final Player player, final ItemStack itemStack) {
		try {
			if (DropItemUtil.checkDebug())
				player.sendMessage("22222222222");
			final BlockIterator i;
			if (player.getGameMode().equals(GameMode.SURVIVAL))
				i = new BlockIterator(player, 4);
			else
				i = new BlockIterator(player, 5);
			Block last = null;
			Block now = null;
			while (i.hasNext())
				if (!(now = i.next()).getType().equals(Material.AIR))
					break;
				else
					last = now;
			if (last == null)
				return false;
			boolean flag = false;
			for (final EntityDropItem entityDropItem : CraftDropItem.getDropItems(this.anxiCode))
				if (entityDropItem.getLocation().distance(last.getLocation()) < 1.0) {
					flag = true;
					break;
				}
			if (DropItemUtil.checkDebug())
				player.sendMessage(flag + "");
			if (!flag)
				return false;
			last.setType(itemStack.getType());
			last.setData((byte) itemStack.getDurability());
			if (!player.getGameMode().equals(GameMode.CREATIVE))
				if (itemStack.getAmount() == 1)
					itemStack.setType(Material.AIR);
				else
					itemStack.setAmount(itemStack.getAmount() - 1);
			player.setItemInHand(itemStack);
			player.updateInventory();
			this.playSound(player, last);
			return true;
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in building block2.");
		}
		return true;
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		try {
			if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
					&& event.getPlayer().getItemInHand() != null
					&& event.getPlayer().getItemInHand().getType().isBlock() && DropItemUtil.checkCoverBlock())
				if (this.buildBlock2(event.getPlayer(), event.getPlayer().getItemInHand())) {
					event.setCancelled(true);
					if (DropItemUtil.checkDebug())
						event.getPlayer().sendMessage("fuck");
				}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in calling Event PlayerInteractEvent.");
		}
	}

	@EventHandler
	public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {
		try {
			if (CraftDropItem.include(event.getRightClicked()))
				if (event.getPlayer().getItemInHand() == null
						|| event.getPlayer().getItemInHand().getType().equals(Material.AIR)) {
					event.setCancelled(true);
					if (DropItemUtil.naturalSpawn() || DropItemUtil.allowedPlayer()
							|| DropItemUtil.checkPlayerPermission(event.getPlayer())) {
						final EntityDropItem dropItem = CraftDropItem.getDropItem(event.getRightClicked());
						final PlayerGottenEvent e = new PlayerGottenEvent(dropItem.getItemStack(), event.getPlayer());
						Bukkit.getServer().getPluginManager().callEvent(e);
						if (e.isCancelled())
							return;
						event.getPlayer().setItemInHand(dropItem.getItemStack());
						CraftDropItem.remove(dropItem, DeathCause.PLAYER_GOTTEN);
					}
				} else if (event.getPlayer().getItemInHand().getType().isBlock())
					if (this.buildBlock(event.getPlayer(), event.getPlayer().getItemInHand()))
						event.setCancelled(true);
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in calling Event PlayerInteractAtEntityEvent.");
		}
	}

	@SuppressWarnings("deprecation")
	private void placeBlock(final Block block, final Player player, final ItemStack itemStack) {
		block.setType(itemStack.getType());
		block.setData((byte) itemStack.getDurability());
		if (!player.getGameMode().equals(GameMode.CREATIVE))
			if (itemStack.getAmount() == 1)
				itemStack.setType(Material.AIR);
			else
				itemStack.setAmount(itemStack.getAmount() - 1);
		player.setItemInHand(itemStack);
		player.updateInventory();
		this.playSound(player, block);
	}

	private void playSound(final Player player, final Block block) {
		try {
			final Object nmsblock = NMSManager.getMethod(NMSManager.getCraftClass("util.CraftMagicNumbers"), "getBlock",
					new Class[] { Material.class }).invoke(null, block.getType());
			final Field stepSound = NMSManager.getField(NMSManager.getNMSClass("Block"), "stepSound");
			final Object sound = stepSound.get(nmsblock);
			final int version = NMSManager.getVersionInt();
			final Object nmsWorld = NMSManager.getMethod(NMSManager.CraftWorld, "getHandle", new Class[] {})
					.invoke(block.getWorld(), new Object[] {});
			if (version == 8) {
				final String sound_str = (String) NMSManager
						.getMethod(sound.getClass(), "getPlaceSound", new Class[] {}).invoke(sound, new Object[] {});
				NMSManager.getMethod(NMSManager.World, "makeSound",
						new Class[] { double.class, double.class, double.class, String.class, float.class,
								float.class })
						.invoke(nmsWorld, block.getLocation().getX(), block.getLocation().getY(),
								block.getLocation().getZ(), sound_str, 1f, 0.8f);
			} else {
				final Object block_position = NMSManager
						.getConstructor(NMSManager.getNMSClass("BlockPosition"),
								new Class[] { double.class, double.class, double.class })
						.newInstance(block.getLocation().getX(), block.getLocation().getY(),
								block.getLocation().getZ());
				final Object sound_effect = NMSManager
						.getMethod(NMSManager.getNMSClass("SoundEffectType"), "e", new Class[] {})
						.invoke(sound, new Object[] {});
				Object category = null;
				for (final Object e : NMSManager.getNMSClass("SoundCategory").getEnumConstants())
					if (e.toString().equalsIgnoreCase("BLOCKS"))
						category = e;
				NMSManager
						.getMethod(NMSManager.World, "a",
								new Class[] { NMSManager.getNMSClass("EntityHuman"),
										NMSManager.getNMSClass("BlockPosition"), NMSManager.getNMSClass("SoundEffect"),
										NMSManager.getNMSClass("SoundCategory"), float.class, float.class })
						.invoke(nmsWorld, null, block_position, sound_effect, category, 1.0f, 0.8f);
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in playing sound with Player(" + player.getName() + ")" + "and Block("
					+ block + ")");
		}
	}
}
