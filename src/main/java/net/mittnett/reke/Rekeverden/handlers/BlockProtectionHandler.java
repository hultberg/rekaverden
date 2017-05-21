package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.Rekeverden;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class BlockProtectionHandler implements Handler {
    private Rekeverden plugin;

    public BlockProtectionHandler(Rekeverden plugin) {
        this.plugin = plugin;
    }


    public void onEnable() {
    }


    public void onDisable() {
    }


    public boolean protect(int uid, int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("REPLACE INTO `r_blocks`(`uid`, `x`, `y`, `z`, `world`)VALUES(?, ?, ?, ?, ?)");
            ps.setInt(1, uid);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, world);

            if (ps.executeUpdate() < 1) {
                this.plugin.getLogger().log(Level.INFO, "Unexpected number of rows changed when protecting block.");
            }


            return true;
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while protecting a block.", e);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }
    }


    public boolean unProtect(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("DELETE FROM `r_blocks` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?");
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setString(4, world);

            if (ps.executeUpdate() < 1) {
                this.plugin.getLogger().log(Level.INFO, "Unexpected number of rows changed when unprotecting block.");
            }


            return true;
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while unprotecting a block.", e);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }
    }


    public User getOwnerUser(Location loc) {
        return getOwnerUser(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }


    public User getOwnerUser(int x, int y, int z, String world) {
        int userID = getOwner(x, y, z, world);
        if (userID < 1) {
            return null;
        }
        return this.plugin.getUserHandler().getUser(userID);
    }


    public int getOwner(Location loc) {
        return getOwner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }


    public int getOwner(int x, int y, int z, String world) {
        return this.plugin.getMySQLHandler().getColumnInt("SELECT `uid` FROM `r_blocks` WHERE `x`=" + x + " AND `y`=" + y + " AND `z`=" + z + " AND `world`='" + world + "'", "uid");
    }


    public boolean isProtected(int x, int y, int z, String world) {
        return getOwner(x, y, z, world) > 0;
    }
}
