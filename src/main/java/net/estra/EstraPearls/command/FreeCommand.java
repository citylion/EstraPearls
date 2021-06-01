package net.estra.EstraPearls.command;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.Pearl;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FreeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            //fuck off console
            return true;
        }
        Player player = (Player) commandSender;
        if(!(player.getItemInHand().getType() == Material.ENDER_PEARL)) {
            player.sendMessage(ChatColor.RED + "You are not holding a pearl in your hand");
            return true;
        }
        if(!PearlPlugin.pearlManager.isPearlByItemStack(player.getItemInHand())) {
            player.sendMessage(ChatColor.RED + "The pearl you are holding is not a valid pearl.");
            return true;
        }
        Pearl pearl = PearlPlugin.pearlManager.getPearlByItemStack(player.getItemInHand());
        PearlPlugin.pearlManager.freePlayer(pearl.getPlayer());
        player.sendMessage(ChatColor.GREEN + "You have freed " + pearl.getPearlName());
        player.setItemInHand(new ItemStack(Material.AIR));
        return true;
    }
}
