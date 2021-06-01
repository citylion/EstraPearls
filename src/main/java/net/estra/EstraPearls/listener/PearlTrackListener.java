package net.estra.EstraPearls.listener;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.Pearl;
import net.estra.EstraPearls.model.holder.BlockHolder;
import net.estra.EstraPearls.model.holder.ItemHolder;
import net.estra.EstraPearls.model.holder.PlayerHolder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class PearlTrackListener implements Listener {

    /**
     * Handles if a pearl has been exploded/destroyed.
     * @param event
     */
    @EventHandler
    public void entityCombustEvent(EntityCombustEvent event) {
        if(!(event.getEntity() instanceof Item)) { return; }

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack( ((Item) event.getEntity()).getItemStack());
        if(pearl == null) { return; }

        PearlPlugin.logger.info("Freeing " + Bukkit.getOfflinePlayer(pearl.getPlayer()).getName() + " due to being combusted");
        PearlPlugin.pearlManager.freePlayer(pearl.getPlayer());
    }

    /**
     * Items Spawning!!! AAAAAAa
     */
    @EventHandler
    public void itemSpawnEvent(ItemSpawnEvent event) {
        ItemStack is = event.getEntity().getItemStack();
        if(!(is.getType() == Material.ENDER_PEARL)) { return; }

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(is);
        if(pearl == null) { return; }

        pearl.updateHolder(new ItemHolder(event.getEntity()));
    }

    /**
     * Drops pearl if player logs out
     */
    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Location loc = player.getLocation();
        World world = player.getWorld();
        Inventory inv = player.getInventory();
        for (Map.Entry<Integer, ? extends ItemStack> entry :
                inv.all(Material.ENDER_PEARL).entrySet()) {
            ItemStack item = entry.getValue();
            Pearl pp = PearlPlugin.pearlManager.getPearlByItemStack(item);
            if (pp == null) {
                continue;
            }
            int slot = entry.getKey();
            inv.clear(slot);
            world.dropItemNaturally(loc, item);
            //Should be tracked by ItemSpawnEvent
        }
    }

    /**
     * Logs all item movement between inventories. Notably containers.
     * @param event
     */
    @EventHandler
    public void itemMove(InventoryMoveItemEvent event) {
        if(!(event.getItem().getType() == Material.ENDER_PEARL)) {
            return;
        }

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getItem());
        if(pearl == null) { return; }

        //Deny always if the destInv isn't one of a player. Means that a hopper or something else is trying to transfer
        //the pearl.
        if(event.getInitiator().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
            return;
        }

        PlayerInventory playerInv = (PlayerInventory) event.getInitiator();
        if(!(playerInv.getHolder() instanceof Player)) {
            //Somehow not a player?
            event.setCancelled(true);
            return;
        }
        Player player = (Player) playerInv.getHolder();

        Inventory destInv = event.getDestination();
        //Disable any stupid attempts at putting pearls where they should not be.
        switch(destInv.getType()) {
            case BEACON:
                player.sendMessage(ChatColor.RED + "Beacons cannot hold pearls");
                event.setCancelled(true);
                return;
            case ANVIL:
                player.sendMessage(ChatColor.RED + "Anvil cannot hold pearls");
                event.setCancelled(true);
                return;
            case MERCHANT:
                player.sendMessage(ChatColor.RED + "Merchants cannot hold pearls");
                event.setCancelled(true);
                return;
            case HOPPER:
                player.sendMessage(ChatColor.RED + "Hoppers cannot hold pearls");
                event.setCancelled(true);
                return;
            case BREWING:
                player.sendMessage(ChatColor.RED + "Brewing Stands cannot hold pearls");
                event.setCancelled(true);
                return;
            case SHULKER_BOX:
                player.sendMessage(ChatColor.RED + "Shulkers cannot hold pearls");
                event.setCancelled(true);
                return;
            case DROPPER:
            case DISPENSER:
                player.sendMessage(ChatColor.RED + "Dispensaries cannot hold pearls");
                event.setCancelled(true);
                return;
            default:
        }

        Block block = event.getDestination().getLocation().getBlock();

        //Update the block holder.
        pearl.updateHolder(new BlockHolder(block));
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent event) {
        if(!(event.getItemDrop().getItemStack().getType() == Material.ENDER_PEARL)) { return; }

        ItemStack is = event.getItemDrop().getItemStack();

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(is);
        if(pearl == null) { return; }

        pearl.updateHolder(new ItemHolder(event.getItemDrop()));
    }

    @EventHandler
    public void pickupItem(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) { return; }

        if(!(event.getItem().getItemStack().getType() == Material.ENDER_PEARL)) { return; }

        Player player = (Player) event.getEntity();

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getItem().getItemStack());
        if(pearl == null) { return; }

        pearl.updateHolder(new PlayerHolder(player));
    }

    /**
     * fuck hoppers
     * @param event
     */
    @EventHandler
    public void hopperShit(InventoryPickupItemEvent event) {
        if(!(event.getItem().getItemStack().getType() == Material.ENDER_PEARL)) { return; }

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getItem().getItemStack());
        if(pearl == null) { return; }

        event.setCancelled(true);
    }

    @EventHandler
    public void itemDespawn(ItemDespawnEvent event) {
        if(!(event.getEntity().getItemStack().getType() == Material.ENDER_PEARL)) { return; }

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getEntity().getItemStack());
        if(pearl == null) { return; }

        //Free since despawned.
        PearlPlugin.pearlManager.freePlayer(pearl.getPlayer());
    }
}
