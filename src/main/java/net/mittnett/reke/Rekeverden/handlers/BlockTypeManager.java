package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.mysql.JDBCConnectionPool;
import net.mittnett.reke.Rekeverden.mysql.MySQLConnectionPool;
import org.bukkit.Material;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockTypeManager {
  private final Logger logger;
  private final MySQLConnectionPool connectionPool;
  private HashMap<String, Integer> cachedBlockIds;
  private final String sqlFindById;
  private final String sqlInsert;

  public BlockTypeManager(Logger logger, MySQLConnectionPool connectionPool) {
    this.logger = logger;
    this.connectionPool = connectionPool;
    this.cachedBlockIds = new HashMap<>();
    this.sqlFindById = "SELECT id AS blockID, material_name AS materialName FROM r_blocklog_block_types WHERE material_name = ?";
    this.sqlInsert = "INSERT INTO r_blocklog_block_types(material_name)VALUES(?)";
  }

  public int getOrCreate(Material material) {
    String materialKey = material.name();

    if (!this.cachedBlockIds.isEmpty() && this.cachedBlockIds.containsKey(materialKey)) {
      return this.cachedBlockIds.get(materialKey);
    }

    Connection connection = null;
    PreparedStatement find = null;
    ResultSet rsGet = null;

    try {
      connection = this.connectionPool.getConnection();
      find = connection.prepareStatement(this.sqlFindById);
      find.setString(1, materialKey);
      rsGet = find.executeQuery();

      if (rsGet.first()) {
        int blockID = rsGet.getInt("blockID");
        cachedBlockIds.put(materialKey, blockID);

        return blockID;
      }
    } catch (SQLException e) {
      this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while finding block ID", e);
    } finally {
      try {
        if (find != null) find.close();
        if (rsGet != null) rsGet.close();
        if (connection != null) {
          connection.close();
          this.connectionPool.removeConnection(connection);
        }
      } catch (SQLException ex) {
        this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
      }
    }

    // INSERT, we did not find any material in the database.
    Connection connection2 = null;
    PreparedStatement insert = null;
    ResultSet rsInsert = null;

    try {
      connection2 = this.connectionPool.getConnection();
      String[] generatedColumns = { "id" };
      insert = connection2.prepareStatement(this.sqlInsert, generatedColumns);
      insert.setString(1, materialKey);
      insert.execute();

      rsInsert = insert.getGeneratedKeys();
      if (rsInsert.first()) {
        int blockID = rsInsert.getInt(1);
        cachedBlockIds.put(materialKey, blockID);

        return blockID;
      }
    } catch (SQLException e) {
      this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while inserting material", e);
    } finally {
      try {
        if (insert != null) insert.close();
        if (connection2 != null) {
          connection2.close();
          this.connectionPool.removeConnection((JDBCConnectionPool) connection2);
        }
      } catch (SQLException ex) {
        this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
      }
    }

    this.logger.log(Level.WARNING, "[Rekeverden] Failed to get block id.");
    return -1;
  }
}
