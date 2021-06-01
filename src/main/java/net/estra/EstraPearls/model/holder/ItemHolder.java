package net.estra.EstraPearls.model.holder;

import org.bukkit.Location;
import org.bukkit.entity.Item;

public class ItemHolder extends PearlHolder{
    private final Item item;

    public ItemHolder(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public Location getLocation() {
        return item.getLocation();
    }

    @Override
    public String getType() {
        return "Item";
    }
}
