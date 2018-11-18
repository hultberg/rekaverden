package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.mysql.JDBCConnectionPool;
import net.mittnett.reke.Rekeverden.mysql.MySQLConnectionPool;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockProtectionHandler implements Handler {
    private Logger logger;
    private MySQLConnectionPool connectionPool;
    private UserHandler userHandler;
    private String sqlProtect;
    private String sqlUnprotect;
    private String sqlGetOwnerInt;

    public BlockProtectionHandler(Logger logger, MySQLConnectionPool connectionPool, UserHandler userHandler) {
        this.logger = logger;
        this.connectionPool = connectionPool;
        this.userHandler = userHandler;

        this.sqlProtect = "REPLACE INTO `r_blocks`(`uid`, `x`, `y`, `z`, `world`)VALUES(?, ?, ?, ?, ?)";
        this.sqlUnprotect = "DELETE FROM `r_blocks` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?";
        this.sqlGetOwnerInt = "SELECT `uid` FROM `r_blocks` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?";
    }


    public void onEnable() {
    }


    public void onDisable() {
    }


    public boolean protect(int uid, int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = this.connectionPool.getConnection();
            ps = conn.prepareStatement(this.sqlProtect);
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
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }
    }


    public boolean unProtect(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = this.connectionPool.getConnection();
            ps = conn.prepareStatement(this.sqlUnprotect);
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setString(4, world);

            if (ps.executeUpdate() < 1) {
               // this.logger.log(Level.INFO, "Unexpected number of rows changed when unprotecting block.");
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
                if (conn != null) {
                    conn.close();
                  this.connectionPool.removeConnection(conn);
                }
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }
    }


    public User getOwnerUser(Location loc) {
        return this.getOwnerUser(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }


    public User getOwnerUser(int x, int y, int z, String world) {
        int userID = this.getOwner(x, y, z, world);
        if (userID < 1) {
            return null;
        }
        return this.userHandler.getUser(userID);
    }


    private int getOwner(int x, int y, int z, String world) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
          conn = this.connectionPool.getConnection();
          ps = conn.prepareStatement(this.sqlGetOwnerInt);
          ps.setInt(1, x);
          ps.setInt(2, y);
          ps.setInt(3, z);
          ps.setString(4, world);
          rs = ps.executeQuery();

          if (rs.first()) {
            return rs.getInt(1);
          }
        } catch (SQLException e) {
          this.logger.log(Level.SEVERE, "[Rekeverden] Failed to get owner ID because of exception", e);
        } finally {
          try {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
            if (conn != null) {
              conn.close();
              this.connectionPool.removeConnection(conn);
            }
          } catch (SQLException e) {
            this.logger.log(Level.SEVERE, "[Rekeverden] Failed to get owner ID because of exception during finally", e);
          }
        }

        return -1;
    }


    public boolean isProtected(int x, int y, int z, String world) {
        return getOwner(x, y, z, world) > 0;
    }
}
