package net.estra.EstraPearls.command;

import net.estra.EstraPearls.PearlPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceFree implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Buzz off console man :<");
            return true;
        }

        Player player = (Player) commandSender;

        if(!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Only admins can use this command.");
        }

        if(args.length < 1) {
            player.sendMessage(ChatColor.RED + "Please use proper arguments. /forcefree <player>");
            return true;
        }
        if(args.length == 1) {
            OfflinePlayer pearl = Bukkit.getOfflinePlayer(args[0]);
            if(pearl == null) {
                player.sendMessage(ChatColor.RED + "You used an invalid player name");
                return true;
            }

            PearlPlugin.pearlManager.freePlayer(pearl.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Successfully freed " + pearl.getName());

            return true;
        }
        return false;
    }
}
