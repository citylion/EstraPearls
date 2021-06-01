package net.estra.EstraPearls.listener;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.Pearl;
import net.estra.EstraPearls.model.holder.BlockHolder;
import net.estra.EstraPearls.model.holder.ItemHolder;
import net.estra.EstraPearls.model.holder.PlayerHolder;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;

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
     * Announce the person in a pearl when a player holds it
     * Ensure pearl is validated at all times.
     * @Import ExilePearl
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {

        Inventory inv = event.getPlayer().getInventory();
        ItemStack item = inv.getItem(event.getNewSlot());
        ItemStack newitem = validatePearl(item);
        if (newitem != null) {
            inv.setItem(event.getNewSlot(), newitem);
        }
    }

    /**
     * Logs all item movement between hoppers mostly.
     * @param event
     */
    @EventHandler
    public void itemMove(InventoryMoveItemEvent event) {
        if(!(event.getItem().getType() == Material.ENDER_PEARL)) {
            return;
        }

        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getItem());
        if(pearl == null) { return; }
        //Pearls cannot be moved :/
        event.setCancelled(true);
    }

    /**
     * Validates an ender pearl item
     * @param item The item to check
     * @Import ExilePearl
     * @return the updated item
     */
    private ItemStack validatePearl(ItemStack item) {
        if (item == null) {
            return null;
        }

        if (item.getType() == Material.ENDER_PEARL
                && item.getEnchantmentLevel(Enchantment.DURABILITY) != 0) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(item);
            if (pearl == null) {
                return new ItemStack(Material.ENDER_PEARL, 1);
            }
            return pearl.getPearlAsItem();
        }

        return null;
    }

    /**
     * Handle inventory dragging properly
     * @Import ExilePearl
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {

        Map<Integer, ItemStack> items = event.getNewItems();

        for(Integer slot : items.keySet()) {
            ItemStack item = items.get(slot);

            Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(item);
            if(pearl != null) {
                boolean clickedTop = event.getView().convertSlot(slot) == slot;

                InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();

                updatePearlHolder(pearl, holder, event);

                if(event.isCancelled()) {
                    return;
                }
            }
        }
    }

    /**
     * Track the location of a pearl
     * Forbid pearls from being put in storage minecarts
     * @Import ExilePearl
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        InventoryAction a = event.getAction();
        //pearlApi.log("Inv Action: " + a.toString());
        if(a == InventoryAction.COLLECT_TO_CURSOR
                || a == InventoryAction.PICKUP_ALL
                || a == InventoryAction.PICKUP_HALF
                || a == InventoryAction.PICKUP_ONE) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getCurrentItem());

            if(pearl != null) {
                pearl.updateHolder(new PlayerHolder(((Player) event.getWhoClicked())));
            }
        }
        else if(event.getAction() == InventoryAction.PLACE_ALL
                || event.getAction() == InventoryAction.PLACE_SOME
                || event.getAction() == InventoryAction.PLACE_ONE) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getCursor());

            if(pearl != null) {
                boolean clickedTop = event.getRawSlot() < event.getView().getTopInventory().getSize();
                InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
                if (holder != null) {
                    updatePearlHolder(pearl, holder, event);
                }
            }
        }
        else if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getCurrentItem());

            if(pearl != null) {
                boolean clickedTop = event.getRawSlot() < event.getView().getTopInventory().getSize();

                InventoryHolder holder = !clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();

                // ShiftClicking into a furnace will not move the pearl into the furnace so the pearlHolder should not be updated
                if (event.getClick().isShiftClick() && holder != null && holder.getInventory() instanceof FurnaceInventory) {
                    event.setCancelled(true);
                    return;
                }

                if(holder != null && holder.getInventory().firstEmpty() >= 0) {
                    updatePearlHolder(pearl, holder, event);
                }
            }
        }
        else if(event.getAction() == InventoryAction.HOTBAR_SWAP) {
            PlayerInventory playerInventory = event.getWhoClicked().getInventory();
            Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(playerInventory.getItem(event.getHotbarButton()));

            if(pearl != null) {
                boolean clickedTop = event.getRawSlot() < event.getView().getTopInventory().getSize();

                InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();

                updatePearlHolder(pearl, holder, event);
            }

            if(event.isCancelled())
                return;

            pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getCurrentItem());

            if(pearl != null) {
                pearl.updateHolder(new PlayerHolder((Player) event.getWhoClicked()));
            }
        }
        else if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getCursor());

            if(pearl != null) {
                boolean clickedTop = event.getRawSlot() < event.getView().getTopInventory().getSize();

                InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();

                updatePearlHolder(pearl, holder, event);
            }

            if(event.isCancelled())
                return;

            pearl = PearlPlugin.pearlManager.getPearlByItemStack(event.getCurrentItem());

            if(pearl != null) {
                pearl.updateHolder(new PlayerHolder((Player) event.getWhoClicked()));
            }
        }
        else if(event.getAction() == InventoryAction.DROP_ALL_CURSOR
                || event.getAction() == InventoryAction.DROP_ALL_SLOT
                || event.getAction() == InventoryAction.DROP_ONE_CURSOR
                || event.getAction() == InventoryAction.DROP_ONE_SLOT) {
            // Handled by onItemSpawn
        }
        else if (a != InventoryAction.NOTHING) {
            if(PearlPlugin.pearlManager.getPearlByItemStack(event.getCurrentItem()) != null || PearlPlugin.pearlManager.getPearlByItemStack(event.getCursor()) != null) {
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You can't do that with an exile pearl.");

                event.setCancelled(true);
            }
        }
    }

    /**
     * Updates the pearl holder
     * @param pearl The pearl to update
     * @param holder The pearl holder
     * @param event The event
     */
    private void updatePearlHolder(Pearl pearl, InventoryHolder holder, Cancellable event) {

        if (holder instanceof Chest) {
            pearl.updateHolder(new BlockHolder(((Chest) holder).getBlock()));
        } else if (holder instanceof DoubleChest) {
            pearl.updateHolder(new BlockHolder(((DoubleChest) holder).getLeftSide().getInventory().getLocation().getBlock()));
        } else if (holder instanceof Furnace) {
            pearl.updateHolder(new BlockHolder(((Furnace) holder).getBlock()));
        } else if (holder instanceof Dispenser) {
            event.setCancelled(true);
        } else if (holder instanceof Dropper) {
            event.setCancelled(true);
        } else if (holder instanceof Hopper) {
            event.setCancelled(true);
        } else if (holder instanceof BrewingStand) {
            pearl.updateHolder(new BlockHolder(((BrewingStand) holder).getBlock()));
        } else if (holder instanceof Player) {
            pearl.updateHolder(new PlayerHolder(((Player) holder).getPlayer()));
        } else {
            event.setCancelled(true);
        }
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
