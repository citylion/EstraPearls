package net.estra.EstraPearls.model.holder;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerHolder extends PearlHolder{

    private final Player player;

    public PlayerHolder(Player player) {
        this.player = player;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public String getType() {
        return "Player";
    }

    public Player getPlayer() { return player; }
}
