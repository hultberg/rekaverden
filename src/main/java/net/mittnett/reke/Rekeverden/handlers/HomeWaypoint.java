package net.mittnett.reke.Rekeverden.handlers;

import org.bukkit.Location;

/**
 * Created by edvin on 02.08.16.
 */
public class HomeWaypoint {
    protected String name;
    protected Location location;
    protected User owner;
    protected boolean isPrimary;

    public HomeWaypoint(String name, Location location, User owner) {
        this(name, location, owner, false);
    }

    public HomeWaypoint(String name, Location location, User owner, boolean isPrimary) {
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.isPrimary = isPrimary;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
