package net.mittnett.reke.Rekeverden.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLSelectResult {
  private Logger logger;
  private ResultSet resultSet;
  private Statement statement;

  public SQLSelectResult(Statement ps, ResultSet rs, Logger logger) {
    this.statement = ps;
    this.resultSet = rs;
    this.logger = logger;
  }

  public ResultSet getResultSet() { return this.resultSet; }

  public void close() {
    try {
      if (this.resultSet != null) {
        this.resultSet.close();
      }
    } catch (SQLException e) {
      this.logger.log(Level.SEVERE, "Failed to close result.", e);
    }

    try {
      if (this.statement != null) {
        this.statement.close();
      }
    } catch (SQLException e) {
      this.logger.log(Level.SEVERE, "Failed to close statement.", e);
    }
  }
}
