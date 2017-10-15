package net.mittnett.reke.Rekeverden.mysql;

import net.mittnett.reke.Rekeverden.Rekeverden;
import net.mittnett.reke.Rekeverden.handlers.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLHandler {
    private final Rekeverden plugin;

    public MySQLHandler(Rekeverden instance) {
        this.plugin = instance;
    }

    public void checkWarnings() {
        try {
            java.sql.SQLWarning warning = this.plugin.getConnection().getWarnings();
            while (warning != null) {
                this.plugin.getLogger().log(Level.WARNING, "[Rekeverden] SQL-Advarsel: ", warning);
                warning = warning.getNextWarning();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] Fikk ikke hentet warnings fra databasetilkobling! ", e);
        }
    }


    public boolean update(PreparedStatement statement) {
      boolean result = false;

      try {
        statement.executeUpdate();
        result = true;
      } catch (SQLException ex) {
        this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
      } finally {
        try {
          statement.close();
        } catch (SQLException e) {
          this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", e);
        }
      }

      return result;
    }


    public boolean update(String query) {
        Connection conn = null;
        Statement stmt = null;
        boolean result = false;

        try {
            conn = this.plugin.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            result = true;
        } catch (SQLException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "SQL-error:", e);
            }
        }

        return result;
    }


    public int insert(String query) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement(query, 1);
            ps.setEscapeProcessing(true);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;
        } catch (SQLException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
            return 0;
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
                this.plugin.getLogger().log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }


    public String getColumn(String query) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String column = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.createStatement();
            rs = ps.executeQuery(query);

            if (rs.next()) {
                column = rs.getString(1);
            }
            return column;
        } catch (SQLException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
            return null;
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
                this.plugin.getLogger().log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }


    public String getColumn(String query, String c, Object[] array) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String column = "";
        int counter = 1;

        if (array != null) {
            try {
                conn = this.plugin.getConnection();
                ps = conn.prepareStatement(query);


                for (Object o : array) {
                    if ((o instanceof Integer)) {
                        ps.setInt(counter, ((Integer) o).intValue());
                    } else if ((o instanceof String)) {
                        ps.setString(counter, (String) o);
                    } else {
                        this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] Nullobjekt i mysql-handler. (getColumn)");
                    }
                    counter++;
                }
                rs = ps.executeQuery();

                if (rs.next()) {
                    column = rs.getString(c);
                }


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
                    this.plugin.getLogger().log(Level.SEVERE, "SQL-error:", e);
                }


                this.plugin.getLogger().exiting(MySQLHandler.class.getName(), Thread.currentThread().getStackTrace()[0].getMethodName(), query);
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
                return null;
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
                    this.plugin.getLogger().log(Level.SEVERE, "SQL-error:", e);
                }
            }
        }


        return column;
    }


    public SQLSelectResult select(PreparedStatement statement) {
      SQLSelectResult result = null;

      try {
        result = new SQLSelectResult(statement, statement.executeQuery(), this.plugin.getLogger());
      } catch (SQLException e) {
        this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while getting a user.", e);
      }

      return result;
    }


    public int getColumnInt(String query, String c) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        int column = 0;
        try {
            conn = this.plugin.getConnection();
            ps = conn.createStatement();
            rs = ps.executeQuery(query);

            if (rs.next()) {
                column = rs.getInt(c);
            }

            return column;
        } catch (SQLException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
            return 0;
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
                this.plugin.getLogger().log(Level.SEVERE, "SQL-error:", e);
            }
        }
    }

    public static boolean checkString(String string) {
        for (int i = 0; i < string.length(); i++) {
            if ((!Character.isLetterOrDigit(string.codePointAt(i))) &&
                    (string.charAt(i) != '_')) {
                return false;
            }
        }


        return true;
    }
}
