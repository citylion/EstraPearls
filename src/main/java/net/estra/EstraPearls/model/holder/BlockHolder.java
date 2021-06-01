package net.estra.EstraPearls.model.holder;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockHolder extends PearlHolder{

    private Block block;

    @Override
    public Location getLocation() {
        return block.getLocation();
    }

    @Override
    public String getType() {
        return "Block";
    }
}
