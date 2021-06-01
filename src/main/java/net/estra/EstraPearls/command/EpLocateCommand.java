package net.estra.EstraPearls.command;

import net.estra.EstraPearls.PearlPlugin;
import net.estra.EstraPearls.model.Pearl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EpLocateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Go away console man");
            return true;
        }

        Player player = (Player) commandSender;

        if(args.length < 1) {
            player.sendMessage(ChatColor.RED + "You need to specify a player to locate.");
            return true;
        }
        if(args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if(target == null) {
                player.sendMessage(ChatColor.RED + "That player does not exist.");
                return true;
            }

            Pearl pearl = PearlPlugin.pearlManager.getPearlByID(target.getUniqueId());
            if(pearl == null) {
                player.sendMessage(ChatColor.RED + "That player is not pearled.");
                return true;
            }
            player.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.DARK_GRAY + " is held at " + pearl.getLocationAsString() + " by " + pearl.getHolder().getType());
            return true;
        }
        return false;
    }
}
