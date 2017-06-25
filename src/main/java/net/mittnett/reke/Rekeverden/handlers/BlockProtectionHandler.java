package net.mittnett.reke.Rekeverden.handlers;

import org.bukkit.Location;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockProtectionHandler implements Handler {
    private Logger logger;
    private DatabaseHandler dbHandler;
    private UserHandler userHandler;

    public BlockProtectionHandler(DatabaseHandler databaseHandler, UserHandler userHandler, Logger logger) {
        this.dbHandler = databaseHandler;
        this.userHandler = userHandler;
        this.logger = logger;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public boolean protect(int uid, int x, int y, int z, String world) {
        Connection conn = this.dbHandler.getConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("REPLACE INTO `r_blocks`(`uid`, `x`, `y`, `z`, `world`)VALUES(?, ?, ?, ?, ?)");
            ps.setInt(1, uid);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, world);

            if (ps.executeUpdate() < 1) {
                this.logger.log(Level.INFO, "Unexpected number of rows changed when protecting block.");
            }


            return true;
        } catch (SQLException e) {
            this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while protecting a block.", e);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }
    }


    public boolean unProtect(int x, int y, int z, String world) {
        Connection conn = this.dbHandler.getConnection();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("DELETE FROM `r_blocks` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?");
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setString(4, world);

            if (ps.executeUpdate() < 1) {
                this.logger.log(Level.INFO, "Unexpected number of rows changed when unprotecting block.");
            }

            return true;
        } catch (SQLException e) {
            this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while unprotecting a block.", e);
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
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
        return this.userHandler.getUser(userID);
    }


    public int getOwner(Location loc) {
        return getOwner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }


    public int getOwner(int x, int y, int z, String world) {
      Connection conn = this.dbHandler.getConnection();
      Statement ps = null;
      ResultSet rs = null;
      int column = 0;

      try {
        ps = conn.createStatement();
        rs = ps.executeQuery("SELECT `uid` FROM `r_blocks` WHERE `x`=" + x + " AND `y`=" + y + " AND `z`=" + z + " AND `world`='" + world + "'");

        if (rs.next()) {
          column = rs.getInt("uid");
        }

        return column;
      } catch (SQLException ex) {
        this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
        return 0;
      } finally {
        try {
          if (ps != null) {
            ps.close();
          }
          if (rs != null) {
            rs.close();
          }
        } catch (SQLException e) {
          this.logger.log(Level.SEVERE, "SQL-error:", e);
        }
      }
    }

    public boolean isProtected(int x, int y, int z, String world) {
        return getOwner(x, y, z, world) > 0;
    }
}
