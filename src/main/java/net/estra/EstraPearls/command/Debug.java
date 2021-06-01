package net.estra.EstraPearls.command;

import net.estra.EstraPearls.model.Pearl;
import net.estra.EstraPearls.model.holder.PlayerHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Debug implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true; //retard.
        }

        Player player = (Player) commandSender;

        Pearl pearl = new Pearl(new PlayerHolder(player), player.getUniqueId(), "Wonder");

        player.getInventory().addItem(pearl.getPearlAsItem());
        return true;
    }
}
