package net.estra.EstraPearls.model.holder;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockHolder extends PearlHolder{

    private final Block block;

    public BlockHolder(Block block) {
        this.block = block;
    }

    public Block getBlock() { return block; }

    @Override
    public Location getLocation() {
        return block.getLocation();
    }

    @Override
    public String getType() {
        return "Block";
    }
}
