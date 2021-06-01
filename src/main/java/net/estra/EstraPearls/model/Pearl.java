package net.estra.EstraPearls.model;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.holder.PearlHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Pearl "Object", pretty much has all the methods you should need to interact with and modify a pearl.
 */
public class Pearl {

    //Pearls current location
    private PearlHolder holder;

    //This shouldn't ever need to be changed since the pearl only exists for a small amount of time.
    private String killerName;

    //Day when pearled
    private Date pearlDate;

    //UUID of the player pearled.
    private UUID player;

    //Date when the pearl will automatically be freed.
    private Date freeDate;

    private boolean freed;

    public Pearl(PearlHolder holder, UUID player, String killer) {
        this.holder = holder;
        this.player = player;
        this.killerName = killer;
        this.pearlDate = new Date();
        //Set the time that should be freed.
        long freeTime = pearlDate.getTime() + PearlPlugin.instance.getPearlTimeInMilis();
        this.freeDate = new Date();
        freeDate.setTime(freeTime);
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isFreed() {
        return freed;
    }

    public String getLocationAsString() {
        return holder.getLocation().getBlockX() + " " + holder.getLocation().getBlockY() + " " + holder.getLocation().getBlockZ();
    }

    public String getDateFreedAsString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(freeDate);
        return calendar.get(2) + "/" + calendar.get(5) + "/" + calendar.get(1);
    }

    public PearlHolder getHolder() { return holder; }

    public ItemStack getPearlAsItem() {
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = pearl.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + Bukkit.getOfflinePlayer(player).getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Killer: " + ChatColor.AQUA + killerName);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(freeDate);
        lore.add(ChatColor.GOLD + "Freed at: " + ChatColor.AQUA + calendar.get(2) + "/" + calendar.get(5) + "/" + calendar.get(1));
        lore.add(ChatColor.DARK_GRAY + player.toString());
        meta.setLore(lore);
        pearl.setItemMeta(meta);
        pearl.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return pearl;
    }
}
