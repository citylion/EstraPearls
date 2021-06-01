package net.estra.EstraPearls.model.holder;

import org.bukkit.Location;

public abstract class PearlHolder {

    private Location location;
    private String type;

    public Location getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }
}
