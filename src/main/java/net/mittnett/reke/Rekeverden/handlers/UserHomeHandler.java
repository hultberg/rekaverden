package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.Rekeverden;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class UserHomeHandler implements Handler {
    protected Rekeverden plugin;

    public UserHomeHandler(Rekeverden plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        // no-op
    }

    @Override
    public void onDisable() {
        // no-op
    }

    public boolean createHome(String name, User owner, Location location, World world)
    {
        return this.createHome(name, owner, location, world, false);
    }

    public boolean createHome(String name, User owner, Location location, World world, boolean primary)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        boolean result = false;

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO r_waypoints(name,owner,enabled,type,data,x,y,z,f,world)VALUES(?, ?, 1, 1, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setInt(2, owner.getId());
            ps.setInt(3, (primary ? 1 : 0));
            ps.setInt(4, location.getBlockX());
            ps.setInt(5, location.getBlockY());
            ps.setInt(6, location.getBlockZ());
            ps.setInt(7, (int) location.getYaw());
            ps.setString(8, world.getName());
            ps.executeUpdate();
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public boolean deleteHome(String name, User owner)
    {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean result = false;

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("DELETE FROM r_waypoints WHERE (name LIKE ? AND owner = ? AND type = 1)");
            ps.setString(1, name);
            ps.setInt(2, owner.getId());
            ps.executeUpdate();
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public HashMap<String, HomeWaypoint> getUserHomes(User owner)
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<String, HomeWaypoint> homes = new HashMap<String, HomeWaypoint>();

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT id,name,owner,enabled,type,data,x,y,z,f,world FROM r_waypoints WHERE (owner = ? AND type = 1 AND data != 1)");
            ps.setInt(1, owner.getId());
            rs = ps.executeQuery();

            while (rs.next()) {
                Location loc = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                loc.setYaw(rs.getInt("f"));
                homes.put(rs.getString("name"), new HomeWaypoint(rs.getString("name"), loc, owner));
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return homes;
    }
}
