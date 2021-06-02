package net.estra.EstraPearls.model;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.holder.BlockHolder;
import net.estra.EstraPearls.model.holder.PearlHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    //Held here since i dont trust bukkit to handle memory management
    private String pearlName;

    //This shouldn't ever need to be changed since the pearl only exists for a small amount of time.
    private String killerName;

    //Day when pearled
    private Date pearlDate;

    //UUID of the player pearled.
    private UUID player;

    //Date when the pearl will automatically be freed.
    private Date freeDate;

    private boolean freed;

    /**
     * Constructor for building a pearl while ingame.
     * @param holder
     * @param player
     * @param killer
     */
    public Pearl(PearlHolder holder, UUID player, String killer) {
        this.holder = holder;
        this.player = player;
        this.pearlName = Bukkit.getOfflinePlayer(player).getName();
        this.killerName = killer;
        this.pearlDate = new Date();
        //Set the time that should be freed.
        long freeTime = pearlDate.getTime() + PearlPlugin.instance.getPearlTimeInMilis();
        this.freeDate = new Date();
        freeDate.setTime(freeTime);
    }

    /**
     * Constructor for building/loading from DB.
     * @param player - player UUID
     * @param playerName - player's actual name
     * @param killerName - killers name
     * @param date - systemTimeInMilis for datePearled
     * @param dateFreed - systemTimeInMilis for dateFreed
     * @param x - x
     * @param y - y
     * @param z - z
     */
    public Pearl(String player, String playerName, String killerName, long date, long dateFreed, int x, int y, int z, String world) {
        this.player = UUID.fromString(player);
        this.pearlName = playerName;
        this.killerName = killerName;
        this.pearlDate = new Date();
        pearlDate.setTime(date);
        this.freeDate = new Date();
        freeDate.setTime(dateFreed);
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        this.holder = new BlockHolder(location.getBlock());
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
        return Math.addExact(calendar.get(Calendar.MONTH), 1) + "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.YEAR);
    }

    public String getKillerName() { return killerName; }

    public Date getPearlDate() { return pearlDate; }

    public Date getFreeDate() {
        return freeDate;
    }

    public PearlHolder getHolder() { return holder; }

    public void updateHolder(PearlHolder holder) {
        this.holder = holder;
    }

    public void setFreed(boolean freed) {
        this.freed = freed;
    }

    public String getPearlName() {
        return pearlName;
    }

    public ItemStack getPearlAsItem() {
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = pearl.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + pearlName);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Killer: " + ChatColor.AQUA + killerName);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(freeDate);
        lore.add(ChatColor.GOLD + "Freed at: " + ChatColor.AQUA + Math.addExact(calendar.get(Calendar.MONTH), 1) + "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.YEAR));
        lore.add(ChatColor.DARK_GRAY + player.toString());
        meta.setLore(lore);
        pearl.setItemMeta(meta);
        pearl.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return pearl;
    }
}
