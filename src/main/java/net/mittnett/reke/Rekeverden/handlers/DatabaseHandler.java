package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.config.Configuration;
import net.mittnett.reke.Rekeverden.mysql.MySQLConnectionPool;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHandler implements Handler {
  private Logger logger;
  private Configuration config;
  private MySQLConnectionPool dbPool;
  private boolean sqlConnected;

  public DatabaseHandler(Logger logger, Configuration config) {
    this.logger = logger;
    this.config = config;
    this.dbPool = null;
    this.sqlConnected = false;
  }

  @Override
  public void onEnable() {
    // No-op
  }

  @Override
  public void onDisable() {
    if (this.dbPool != null) {
      this.dbPool.close();
      this.logger.info(" - Closing SQL connection...");
    }
  }

  /**
   * Provides the connect.
   * @return Connection
   */
  public Connection getConnection() {
    try {
      Connection conn = this.dbPool.getConnection();

      if ((!this.sqlConnected) && (conn != null)) {
        this.logger.info("SQL connection re-established.");
        this.sqlConnected = true;
      }

      return conn;
    } catch (Exception e) {
      this.sqlConnected = false;
      this.logger.severe("Could not fetch SQL connection! " + e.getMessage());
    }

    return null;
  }

  public boolean createConnection() {
    try {
      this.dbPool = new MySQLConnectionPool(
        this.config.getDatabaseHost(),
        this.config.getDatabaseName(),
        this.config.getDatabaseUser(),
        this.config.getDatabasePass(),
        this.config.getDatabasePort()
      );

      Connection conn = this.getConnection();
      if (conn == null) {
        return false;
      }

      conn.close();
      this.sqlConnected = true;
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return this.sqlConnected;
  }

  public int insert(PreparedStatement ps) {
    ResultSet rs = null;
    int id = -1;

    try {
      ps.executeUpdate();
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
      return id;
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
        this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception:", e);
      }
    }
  }

  public boolean update(PreparedStatement ps) {
    boolean result = false;

    try {
      ps.executeUpdate();
      result = true;
    } catch (SQLException ex) {
      this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
    } finally {
      try {
        if (ps != null) {
          ps.close();
        }
      } catch (SQLException e) {
        this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception:", e);
      }
    }

    return result;
  }
}
