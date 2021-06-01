package net.estra.EstraPearls.model;

import net.estra.EstraPearls.model.holder.PlayerHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    public boolean pearlPlayer(Player player, Player imprisoner) {
        Pearl pearl = new Pearl(new PlayerHolder(imprisoner), player.getUniqueId(), imprisoner.getName());

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

    public Pearl getPearlByItemStack(ItemStack is) {
        for(Pearl pearl : pearls) {
            if(pearl.getPearlAsItem().equals(is)) {
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
}
