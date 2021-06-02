package net.estra.EstraPearls.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EpHelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Console cannot use this cmd.");
            return true;
        }
        //I don't care if there are any arguments this should send no matter what.
        Player player = (Player) commandSender;
        player.sendMessage(ChatColor.DARK_RED + "EstraPearls\n" +
                ChatColor.GOLD + "/epfree " + ChatColor.DARK_GRAY + "- frees a pearl in your hand\n" +
                ChatColor.GOLD + "/eplocate " + ChatColor.DARK_GRAY + "- locates a pearl of your choice\n" +
                ChatColor.GOLD + "/ep " + ChatColor.DARK_GRAY + "- Displays this help menu");
        return true;
    }
}
