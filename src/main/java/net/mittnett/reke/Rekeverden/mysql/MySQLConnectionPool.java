package net.mittnett.reke.Rekeverden.mysql;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class MySQLConnectionPool implements Closeable {
    private static final long alive = 30000L;
    private final List<JDBCConnectionPool> connections;
    private final Lock lock = new java.util.concurrent.locks.ReentrantLock();
    private static final String jdbc = "jdbc:mysql://";
    private final String database;

    public MySQLConnectionPool(String host, String dbName, String username, String password, int port) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        this.dbuser = username;
        this.dbpasswd = password;
        this.database = ("jdbc:mysql://" + host + ":" + port + "/" + dbName);

        this.connections = new ArrayList();
        ConnectionReaper reaper = new ConnectionReaper();
        new Thread(reaper).start();
    }

    private final String dbuser;
    private final String dbpasswd;

    public void close() {
        this.lock.lock();
        Iterator<JDBCConnectionPool> itr = this.connections.iterator();
        while (itr.hasNext()) {
            JDBCConnectionPool conn = (JDBCConnectionPool) itr.next();
            itr.remove();
            conn.terminate();
        }
        this.lock.unlock();
    }

    public Connection getConnection() throws SQLException {
        this.lock.lock();
        try {
            Iterator<JDBCConnectionPool> itr = this.connections.iterator();
            JDBCConnectionPool localJDBCConnectionPool1;
            while (itr.hasNext()) {
                JDBCConnectionPool conn = (JDBCConnectionPool) itr.next();
                if ((conn.lease()) &&
                        (conn.isValid())) {
                    return conn;
                }
            }

            JDBCConnectionPool conn = new JDBCConnectionPool(DriverManager.getConnection(this.database, this.dbuser, this.dbpasswd), this);
            conn.lease();
            if (!conn.isValid()) {
                conn.terminate();
                throw new SQLException("Failed to confirm connection");
            }
            this.connections.add(conn);
            return conn;
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized void removeConnection(JDBCConnectionPool JDBCCconn) {
      this.connections.remove(JDBCCconn);
    }

    public synchronized void removeConnection(Connection connection) {
      if (connection instanceof JDBCConnectionPool) {
        this.connections.remove(connection);
      }
    }

    public synchronized void checkTrueFalse(JDBCConnectionPool JDBCCconn) {
        System.out.println(JDBCCconn.inUse());
    }

    private void reapConnections() {
        this.lock.lock();
        long stale = System.currentTimeMillis() - 30000L;
        Iterator<JDBCConnectionPool> itr = this.connections.iterator();
        while (itr.hasNext()) {
            JDBCConnectionPool conn = (JDBCConnectionPool) itr.next();
            if ((conn.inUse()) && (stale > conn.getLastUse())) {
                conn.terminate();
                itr.remove();
            }
        }
        this.lock.unlock();
    }

    private class ConnectionReaper implements Runnable {
        private ConnectionReaper() {
        }

        public void run() {
            for (; ; ) {
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException localInterruptedException) {
                }
                MySQLConnectionPool.this.reapConnections();
            }
        }
    }
}
