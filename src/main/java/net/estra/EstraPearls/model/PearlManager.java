package net.estra.EstraPearls.model;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.holder.BlockHolder;
import net.estra.EstraPearls.model.holder.ItemHolder;
import net.estra.EstraPearls.model.holder.PearlHolder;
import net.estra.EstraPearls.model.holder.PlayerHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PearlManager {
    List<Pearl> pearls = new ArrayList<>();



    public void freePlayer(UUID uuid) {
        pearls.removeIf(pearl -> pearl.getPlayer().equals(uuid));
    }

    public boolean pearlPlayer(UUID player, Player imprisoner) {
        Pearl pearl = new Pearl(new PlayerHolder(imprisoner), player, imprisoner.getName());

        pearls.add(pearl);

        // set up the imprisoner's inventory
        Inventory inv = imprisoner.getInventory();
        ItemStack stack = null;
        int stacknum = -1;

        // scan for the smallest stack of normal ender pearls
        for (Map.Entry<Integer, ? extends ItemStack> entry :
                inv.all(Material.ENDER_PEARL).entrySet()) {
            ItemStack newstack = entry.getValue();
            int newstacknum = entry.getKey();
            if (!newstack.hasItemMeta()) {
                if (stack != null) {
                    // don't keep a stack bigger than the previous one
                    if (newstack.getAmount() > stack.getAmount()) {
                        continue;
                    }
                    // don't keep an identical sized stack in a higher slot
                    if (newstack.getAmount() == stack.getAmount() &&
                            newstacknum > stacknum) {
                        continue;
                    }
                }

                stack = newstack;
                stacknum = entry.getKey();
            }
        }

        int pearlnum;
        ItemStack dropStack = null;
        if (stacknum == -1) { // no pearl (admin command)
            // give him a new one at the first empty slot
            pearlnum = inv.firstEmpty();
        } else if (stack.getAmount() == 1) { // if he's just got one pearl
            pearlnum = stacknum; // put the prison pearl there
        } else {
            // otherwise, put the prison pearl in the first empty slot
            pearlnum = inv.firstEmpty();
            if (pearlnum > 0) {
                // and reduce his stack of pearls by one
                stack.setAmount(stack.getAmount() - 1);
                inv.setItem(stacknum, stack);
            } else { // no empty slot?
                inv.clear(stacknum); // clear before drop
                dropStack = new ItemStack(Material.ENDER_PEARL, stack.getAmount() - 1);
                pearlnum = stacknum; // then overwrite his stack of pearls
            }
        }

        // drop pearls that otherwise would be deleted
        if (dropStack != null) {
            imprisoner.getWorld().dropItem(imprisoner.getLocation(), dropStack);
            Bukkit.getLogger().info(
                    imprisoner.getLocation() + ", " + dropStack.getAmount());
        }

        ItemStack is = pearl.getPearlAsItem();

        inv.setItem(pearlnum, is);
        return true;
    }

    public Pearl getPearlByID(UUID uid) {
        for(Pearl pearl : pearls) {
            if(pearl.getPlayer().equals(uid)) {
                return pearl;
            }
        }
        return null;
    }

    public boolean isPearlByItemStack(ItemStack is) {
        for(Pearl pearl : pearls) {
            if(pearl.getPearlAsItem().equals(is)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This is horrible code but fuck exilepearl i have no interest in using that shit
     * @param is
     * @return
     */
    public Pearl getPearlByItemStack(ItemStack is) {
        //Item has no item meta, nor lore so its not a pearl. :/
        if(is.getItemMeta() == null) { return null; }
        if(is.getItemMeta().getLore() == null) { return null; }
        for(Pearl pearl : pearls) {
            List<String> lore = is.getItemMeta().getLore();
            String uid = lore.get(2);
            String compare = ChatColor.DARK_GRAY + pearl.getPlayer().toString();
            if(compare.equals(uid)) {
                return pearl;
            }
        }
        return null;
    }

    public boolean hasPearl(UUID uid) {
        for(Pearl pearl : pearls) {
            if(pearl.getPlayer().equals(uid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies that all pearls exist and haven't been yeeted.
     */
    public void verifyPearls() {
        PearlPlugin.logger.info("Running pearl verification task.");
        for(Pearl pearl : pearls) {
            PearlPlugin.logger.info("Starting verification of pearl UUID: " + pearl.getPlayer().toString());
            PearlHolder holder = pearl.getHolder();
            //stupid bug mess bullshit fuck you
            if(holder == null) {
                freePlayer(pearl.getPlayer());
                return;
            }
            if(holder instanceof ItemHolder) {
                ItemHolder itemHolder = (ItemHolder) holder;
                //Entity is dead, removed, or deleted.
                if (itemHolder.getItem().isDead() || !itemHolder.getItem().isValid()) {
                    freePlayer(pearl.getPlayer());
                    return;
                }
                PearlPlugin.logger.info("Pearl Verified.");
                return;
                //pearl still exists sooooooo, whatever!
            } else if(holder instanceof PlayerHolder) {
                PlayerHolder playerHolder = (PlayerHolder) holder;
                Player player = playerHolder.getPlayer();
                //Assuming the player is any of the above, clearly the pearl is in Debug Limbo
                if(!player.isOnline() || player.isBanned() || !player.isValid() || player.isDead()) {
                    freePlayer(pearl.getPlayer());
                    return;
                }
                Inventory inventory = player.getInventory();
                boolean found = false; //false by default, if we find we set to true.
                for(Map.Entry<Integer, ? extends ItemStack> entry : inventory.all(Material.ENDER_PEARL).entrySet()) {
                    ItemStack item = entry.getValue();
                    Pearl entryPearl = getPearlByItemStack(item);
                    if(entryPearl == null) { continue; }
                    //only mark true if we find the pearl in the container.
                    if(entryPearl.equals(pearl)) { found = true; }
                }
                if(!found) {
                    //Couldn't find the pearl so we free.
                    freePlayer(pearl.getPlayer());
                    return;
                }
                PearlPlugin.logger.info("Pearl Verified.");
                return;
                //Player is good to go so, WHATEVER.
            } else if(holder instanceof BlockHolder) {
                BlockHolder blockHolder = (BlockHolder) holder;
                Block block = blockHolder.getBlock();
                if(!(block instanceof Container)) {
                    //how the fuck did you accomplish this
                    freePlayer(pearl.getPlayer());
                    return;
                }
                Inventory inventory = ((Container) block).getInventory();
                boolean found = false; //false by default, if we find we set to true.
                for(Map.Entry<Integer, ? extends ItemStack> entry : inventory.all(Material.ENDER_PEARL).entrySet()) {
                    ItemStack item = entry.getValue();
                    Pearl entryPearl = getPearlByItemStack(item);
                    if(entryPearl == null) { continue; }
                    //only mark true if we find the pearl in the container.
                    if(entryPearl.equals(pearl)) { found = true; }
                }
                if(!found) {
                    //Couldn't find the pearl so we free.
                    freePlayer(pearl.getPlayer());
                    return;
                }
                PearlPlugin.logger.info("Pearl Verified.");
                return;
            }
            PearlPlugin.logger.warning("Container bugged for pearl? " + pearl.getPlayer() + " freeing.");
            freePlayer(pearl.getPlayer());
        }
    }

    /**
     * Verify a specific pearl
     */
    public void verifyPearl(Pearl pearl) {
        PearlPlugin.logger.info("Starting individual verification of pearl UUID: " + pearl.getPlayer().toString());
        PearlHolder holder = pearl.getHolder();
        //stupid bug mess bullshit fuck you
        if(holder == null) {
            freePlayer(pearl.getPlayer());
            pearl.setFreed(true);
            return;
        }
        if(holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            //Entity is dead, removed, or deleted.
            if (itemHolder.getItem().isDead() || !itemHolder.getItem().isValid()) {
                freePlayer(pearl.getPlayer());
                pearl.setFreed(true);
                return;
            }
            PearlPlugin.logger.info("Pearl Verified.");
            return;
            //pearl still exists sooooooo, whatever!
        } else if(holder instanceof PlayerHolder) {
            PlayerHolder playerHolder = (PlayerHolder) holder;
            Player player = playerHolder.getPlayer();
            //Assuming the player is any of the above, clearly the pearl is in Debug Limbo
            if(!player.isOnline() || player.isBanned() || !player.isValid() || player.isDead()) {
                freePlayer(pearl.getPlayer());
                pearl.setFreed(true);
                return;
            }
            Inventory inventory = player.getInventory();
            boolean found = false; //false by default, if we find we set to true.
            for(Map.Entry<Integer, ? extends ItemStack> entry : inventory.all(Material.ENDER_PEARL).entrySet()) {
                ItemStack item = entry.getValue();
                Pearl entryPearl = getPearlByItemStack(item);
                if(entryPearl == null) { continue; }
                //only mark true if we find the pearl in the container.
                if(entryPearl.equals(pearl)) { found = true; }
            }
            if(!found) {
                //Couldn't find the pearl so we free.
                freePlayer(pearl.getPlayer());
                pearl.setFreed(true);
                return;
            }
            PearlPlugin.logger.info("Pearl Verified.");
            return;
            //Player is good to go so, WHATEVER.
        } else if(holder instanceof BlockHolder) {
            BlockHolder blockHolder = (BlockHolder) holder;
            Block block = blockHolder.getBlock();
            if(!(block instanceof Container)) {
                //how the fuck did you accomplish this
                freePlayer(pearl.getPlayer());
                pearl.setFreed(true);
                return;
            }
            Inventory inventory = ((Container) block).getInventory();
            boolean found = false; //false by default, if we find we set to true.
            for(Map.Entry<Integer, ? extends ItemStack> entry : inventory.all(Material.ENDER_PEARL).entrySet()) {
                ItemStack item = entry.getValue();
                Pearl entryPearl = getPearlByItemStack(item);
                if(entryPearl == null) { continue; }
                //only mark true if we find the pearl in the container.
                if(entryPearl.equals(pearl)) { found = true; }
            }
            if(!found) {
                //Couldn't find the pearl so we free.
                freePlayer(pearl.getPlayer());
                pearl.setFreed(true);
                return;
            }
            PearlPlugin.logger.info("Pearl Verified.");
            return;
        }
        PearlPlugin.logger.warning("Container bugged for pearl? " + pearl.getPlayer() + " freeing.");
        freePlayer(pearl.getPlayer());
        pearl.setFreed(true);
    }
}
