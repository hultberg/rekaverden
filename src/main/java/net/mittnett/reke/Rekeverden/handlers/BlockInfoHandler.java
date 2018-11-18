package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.mysql.MySQLConnectionPool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockInfoHandler implements Handler {
  private Logger logger;
  private MySQLConnectionPool connectionPool;
  private BlockTypeManager blockTypeManager;
  private UserHandler userHandler;
  private String sqlInsert;
  private String sqlGetLog;
  public SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy (HH:mm:ss)");

  public BlockInfoHandler(Logger logger, MySQLConnectionPool connectionPool, UserHandler userHandler, BlockTypeManager blockTypeManager) {
    this.logger = logger;
    this.connectionPool = connectionPool;
    this.userHandler = userHandler;
    this.blockTypeManager = blockTypeManager;

    this.sqlInsert = "INSERT INTO `r_blocklog`(`uid`, `x`, `y`, `z`, `world`, `new_block_id`, `block_data`, `timestamp`, `action`)VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    this.sqlGetLog = "SELECT l.uid AS uid, l.action AS action, l.timestamp AS timestamp, b.material_name AS materialName, l.block_id AS legacyBlockId, l.block_data AS blockData FROM r_blocklog AS l" +
      " LEFT JOIN r_blocklog_block_types AS b ON b.id = l.new_block_id WHERE l.x=? AND l.y=? AND l.z=? AND l.world=?";
  }


  public void onEnable() {
  }


  public void onDisable() {
  }


  public void log(User user, Location loc, Block block, BlockAction action) {
    log(user.getId(), loc
      .getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), block
      .getType(), block.getData(), action);
  }


  public void log(int uid, int x, int y, int z, String world, Material material, int blockData, BlockAction action) {
    Connection conn = null;
    PreparedStatement ps = null;

    int actionInt = action.getActionID();
    int blockID = this.blockTypeManager.getOrCreate(material);

    try {
      conn = this.connectionPool.getConnection();
      ps = conn.prepareStatement(this.sqlInsert);
      ps.setInt(1, uid);
      ps.setInt(2, x);
      ps.setInt(3, y);
      ps.setInt(4, z);
      ps.setString(5, world);
      ps.setInt(6, blockID);
      ps.setInt(7, blockData);
      ps.setInt(8, (int) (System.currentTimeMillis() / 1000L));
      ps.setInt(9, actionInt);

      if (ps.executeUpdate() < 1)
        throw new SQLException("Unexpected amount of rows changed from last query");
      return;
    } catch (SQLException e) {
      this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while logging a block.", e);
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


  public ArrayList<String> getBlockLog(Location loc) {
    return getBlockLog(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
  }


  public ArrayList<String> getBlockLog(int x, int y, int z, String world) {
    ArrayList<String> logLines = new ArrayList<>();

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      conn = this.connectionPool.getConnection();
      ps = conn.prepareStatement(this.sqlGetLog);
      ps.setInt(1, x);
      ps.setInt(2, y);
      ps.setInt(3, z);
      ps.setString(4, world);

      rs = ps.executeQuery();
      while (rs.next()) {
        Date date = new Date(rs.getLong("timestamp") * 1000L);
        String materialName = "unknown";

        int legacyBlockId = rs.getInt("legacyBlockId");
        if (legacyBlockId > 0) {
          for (Material mat : Material.values()) {
            if (mat.isLegacy() && mat.getId() == legacyBlockId) {
              materialName = mat.name();

              // ensure this block type is stored for a later time.
              this.blockTypeManager.getOrCreate(mat);
              break;
            }
          }
        } else {
          materialName = rs.getString("materialName");
        }

        logLines.add(this.dateFormat.format(date) + " -- " + this.userHandler.getUser(rs.getInt("uid")).getName() + " " + actionToString(rs.getInt("action")) + " " + materialName);
      }
    } catch (SQLException e) {
      this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while fetching blocklog.", e);
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
      } catch (SQLException ex) {
        this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
      }
    }

    return logLines;
  }


  public String actionToString(int action) {
    BlockAction ba = BlockAction.fromActionID(action);
    return (ba != null) ? ba.getActionString() : "unknown";
  }
}
