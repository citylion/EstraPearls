package net.estra.EstraPearls.listener;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.Pearl;
import net.estra.EstraPearls.model.holder.PlayerHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        PearlPlugin.logger.info("player damage and shit");

        final UUID playerUid;
        playerUid = ((Player) event.getEntity()).getUniqueId();
        Player player = (Player) event.getEntity();
        if(PearlPlugin.pearlManager.hasPearl(playerUid)) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByID(playerUid);
            //No idea how'd they would login.
            Bukkit.getPlayer(playerUid).kickPlayer(ChatColor.AQUA + "You are pearled! \n"
                    + ChatColor.GOLD + "Your pearl is located at " + pearl.getLocationAsString() + "\n"
                    + ChatColor.GREEN + "You will be freed on " + pearl.getDateFreedAsString());

            for(Player damager : PearlPlugin.damageLogManager.getDamagers(player)) {
                damager.sendMessage(ChatColor.RED + "[EP] Player was apparently already pearled. Contact an administrator, this is a bug :/");
            }
            return;
        }
        for(Player damager : PearlPlugin.damageLogManager.getDamagers(player)) {
            if (PearlPlugin.pearlManager.hasPearl(playerUid)) {
                //Player is already pearled, meaning the for statement was likely completed. Will ALWAYS be a PlayerContainer after.
                Pearl pearl = PearlPlugin.pearlManager.getPearlByID(playerUid);
                if (pearl.getHolder() instanceof PlayerHolder) {
                    Player kill = ((PlayerHolder) pearl.getHolder()).getPlayer();
                    damager.sendMessage(ChatColor.DARK_GRAY + "[EP] " + ChatColor.AQUA + player.getName() + ChatColor.DARK_GRAY + " was pearled by " + ChatColor.AQUA + kill.getName());
                    break; //stop FUCKING with it AAAAAAAAAAAAAAAAAAAAA
                }
            }
            int firstPearl = Integer.MAX_VALUE; //find the first pearl in their inv
            for (Map.Entry<Integer, ? extends ItemStack> entry : damager.getInventory().all(Material.ENDER_PEARL).entrySet()) {
                ItemStack stack = entry.getValue();
                if (!stack.hasItemMeta())
                    firstPearl = Math.min(entry.getKey(), firstPearl);
            }

            if (firstPearl == Integer.MAX_VALUE)
                continue; //No pearl no imprison!

            if (firstPearl > 9)
                continue; //Pearl isn't in the hotbar, so skip.

            if(PearlPlugin.pearlManager.pearlPlayer(player.getUniqueId(), damager)) {
                PearlPlugin.logger.info(player.getName() + " was pearled by " + damager.getName());
                player.sendMessage(ChatColor.DARK_GRAY + "[EP] You have been imprisoned by " + ChatColor.YELLOW + damager.getName());
                damager.sendMessage(ChatColor.DARK_GRAY + "[EP] You imprisoned " + ChatColor.YELLOW + player.getName());
                break; //Woohoo
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if(PearlPlugin.pearlManager.hasPearl(event.getPlayer().getUniqueId())) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByID(event.getPlayer().getUniqueId());
            PearlPlugin.pearlManager.verifyPearl(pearl);
            //Check if pearl has been freed after verifying pearl.
            if(pearl.isFreed()) {
                //delay so sends when player is actually logged
                Bukkit.getScheduler().runTaskLater(PearlPlugin.instance, () -> event.getPlayer().sendMessage(ChatColor.GREEN + "You have been freed!"), 40);
                return;
            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.AQUA + "You are pearled! \n"
                    + ChatColor.GOLD + "Your pearl is located at " + pearl.getLocationAsString() + "\n"
            + ChatColor.GREEN + "You will be freed on " + pearl.getDateFreedAsString());
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        if(PearlPlugin.pearlManager.hasPearl(event.getPlayer().getUniqueId())) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByID(event.getPlayer().getUniqueId());
            if(pearl.isFreed()) {
                return;
            }
            event.getPlayer().kickPlayer(ChatColor.AQUA + "You are pearled! \n"
                    + ChatColor.GOLD + "Your pearl is located at " + pearl.getLocationAsString() + "\n"
                    + ChatColor.GREEN + "You will be freed on " + pearl.getDateFreedAsString());
        }
    }
}
