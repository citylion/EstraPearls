package net.estra.EstraPearls.listener;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.Pearl;
import net.estra.EstraPearls.model.holder.PlayerHolder;
import net.minelink.ctplus.compat.base.NpcIdentity;
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

        Player player = (Player)event.getEntity();
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        if (PearlPlugin.instance.getCombatTagManager().isCombatTagPlusNPC(player)){
            NpcIdentity iden = PearlPlugin.instance.getCombatTagManager().getCombatTagPlusNPCIdentity(player);
            uuid = iden.getId();
            playerName = iden.getName();
            PearlPlugin.logger.info("NPC Player: " + playerName + ", ID: " + uuid);
        } else if (PearlPlugin.instance.getCombatTagManager().isEnabled() && !PearlPlugin.instance.getCombatTagManager().isCombatTagged(player)) {
            PearlPlugin.logger.info("Player: " + playerName + " is out of combatTag, immune from pearling.");
            return;
        }

        if(PearlPlugin.pearlManager.hasPearl(uuid)) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByID(uuid);
            //No idea how'd they would login.
            Bukkit.getPlayer(uuid).kickPlayer(ChatColor.AQUA + "You are pearled! \n"
                    + ChatColor.GOLD + "Your pearl is located at " + pearl.getLocationAsString() + "\n"
                    + ChatColor.GREEN + "You will be freed on " + pearl.getDateFreedAsString());

            for(Player damager : PearlPlugin.damageLogManager.getDamagers(player)) {
                damager.sendMessage(ChatColor.RED + "[EP] Player was apparently already pearled. Contact an administrator, this is a bug :/");
            }
            return;
        }
        for(Player damager : PearlPlugin.damageLogManager.getDamagers(player)) {
            if (PearlPlugin.pearlManager.hasPearl(uuid)) {
                //Player is already pearled, meaning the for statement was likely completed. Will ALWAYS be a PlayerContainer after.
                Pearl pearl = PearlPlugin.pearlManager.getPearlByID(uuid);
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

            if(PearlPlugin.pearlManager.pearlPlayer(uuid, damager)) {
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
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.DARK_RED + "You are pearled! \n"
                    + ChatColor.DARK_GRAY + "Your pearl is located at " + ChatColor.AQUA + pearl.getLocationAsString() + "\n"
            + ChatColor.DARK_GRAY + "You will be freed on " + ChatColor.GOLD + pearl.getDateFreedAsString());
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        if(PearlPlugin.pearlManager.hasPearl(event.getPlayer().getUniqueId())) {
            Pearl pearl = PearlPlugin.pearlManager.getPearlByID(event.getPlayer().getUniqueId());
            if(pearl.isFreed()) {
                return;
            }
            event.getPlayer().kickPlayer(ChatColor.DARK_RED + "You are pearled! \n"
                    + ChatColor.DARK_GRAY + "Your pearl is located at " + ChatColor.AQUA + pearl.getLocationAsString() + "\n"
                    + ChatColor.DARK_GRAY + "You will be freed on " + ChatColor.GOLD + pearl.getDateFreedAsString());
        }
    }
}
