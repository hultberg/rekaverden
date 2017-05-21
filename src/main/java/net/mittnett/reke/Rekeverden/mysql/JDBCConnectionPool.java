/*     */ package net.mittnett.reke.Rekeverden.mysql;
/*     */ 
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Struct;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ public class JDBCConnectionPool implements Connection
/*     */ {
/*     */   private MySQLConnectionPool pool;
/*     */   private final Connection conn;
/*     */   private boolean inuse;
/*     */   private long timestamp;
/*     */   private int networkTimeout;
/*     */   private String schema;
/*     */   
/*     */   public JDBCConnectionPool(Connection conn, MySQLConnectionPool pool)
/*     */   {
/*  32 */     this.conn = conn;
/*  33 */     this.pool = pool;
/*  34 */     this.inuse = false;
/*  35 */     this.timestamp = 0L;
/*  36 */     this.networkTimeout = 30;
/*  37 */     this.schema = "default";
/*     */   }
/*     */   
/*     */   public void clearWarnings() throws SQLException
/*     */   {
/*  42 */     this.conn.clearWarnings();
/*     */   }
/*     */   
/*     */   public void close() throws SQLException
/*     */   {
/*  47 */     this.inuse = false;
/*     */     try {
/*  49 */       if (!this.conn.getAutoCommit()) {
/*  50 */         this.conn.setAutoCommit(true);
/*     */       }
/*     */     } catch (SQLException ex) {
/*  53 */       this.pool.removeConnection(this);
/*  54 */       terminate();
/*     */     }
/*     */   }
/*     */   
/*     */   public void commit() throws SQLException
/*     */   {
/*  60 */     this.conn.commit();
/*     */   }
/*     */   
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException
/*     */   {
/*  65 */     return this.conn.createArrayOf(typeName, elements);
/*     */   }
/*     */   
/*     */   public Blob createBlob() throws SQLException
/*     */   {
/*  70 */     return this.conn.createBlob();
/*     */   }
/*     */   
/*     */   public Clob createClob() throws SQLException
/*     */   {
/*  75 */     return this.conn.createClob();
/*     */   }
/*     */   
/*     */   public java.sql.NClob createNClob() throws SQLException
/*     */   {
/*  80 */     return this.conn.createNClob();
/*     */   }
/*     */   
/*     */   public SQLXML createSQLXML() throws SQLException
/*     */   {
/*  85 */     return this.conn.createSQLXML();
/*     */   }
/*     */   
/*     */   public Statement createStatement() throws SQLException
/*     */   {
/*  90 */     return this.conn.createStatement();
/*     */   }
/*     */   
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
/*     */   {
/*  95 */     return this.conn.createStatement(resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*     */   {
/* 100 */     return this.conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */   
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException
/*     */   {
/* 105 */     return this.conn.createStruct(typeName, attributes);
/*     */   }
/*     */   
/*     */   public boolean getAutoCommit() throws SQLException
/*     */   {
/* 110 */     return this.conn.getAutoCommit();
/*     */   }
/*     */   
/*     */   public String getCatalog() throws SQLException
/*     */   {
/* 115 */     return this.conn.getCatalog();
/*     */   }
/*     */   
/*     */   public Properties getClientInfo() throws SQLException
/*     */   {
/* 120 */     return this.conn.getClientInfo();
/*     */   }
/*     */   
/*     */   public String getClientInfo(String name) throws SQLException
/*     */   {
/* 125 */     return this.conn.getClientInfo(name);
/*     */   }
/*     */   
/*     */   public int getHoldability() throws SQLException
/*     */   {
/* 130 */     return this.conn.getHoldability();
/*     */   }
/*     */   
/*     */   public DatabaseMetaData getMetaData() throws SQLException
/*     */   {
/* 135 */     return this.conn.getMetaData();
/*     */   }
/*     */   
/*     */   public int getTransactionIsolation() throws SQLException
/*     */   {
/* 140 */     return this.conn.getTransactionIsolation();
/*     */   }
/*     */   
/*     */   public Map<String, Class<?>> getTypeMap() throws SQLException
/*     */   {
/* 145 */     return this.conn.getTypeMap();
/*     */   }
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException
/*     */   {
/* 150 */     return this.conn.getWarnings();
/*     */   }
/*     */   
/*     */   public boolean isClosed() throws SQLException
/*     */   {
/* 155 */     return this.conn.isClosed();
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() throws SQLException
/*     */   {
/* 160 */     return this.conn.isReadOnly();
/*     */   }
/*     */   
/*     */   public boolean isValid(int timeout) throws SQLException
/*     */   {
/* 165 */     return this.conn.isValid(timeout);
/*     */   }
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException
/*     */   {
/* 170 */     return this.conn.isWrapperFor(iface);
/*     */   }
/*     */   
/*     */   public String nativeSQL(String sql) throws SQLException
/*     */   {
/* 175 */     return this.conn.nativeSQL(sql);
/*     */   }
/*     */   
/*     */   public CallableStatement prepareCall(String sql) throws SQLException
/*     */   {
/* 180 */     return this.conn.prepareCall(sql);
/*     */   }
/*     */   
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*     */   {
/* 185 */     return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*     */   {
/* 190 */     return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql) throws SQLException
/*     */   {
/* 195 */     return this.conn.prepareStatement(sql);
/*     */   }
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
/*     */   {
/* 200 */     return this.conn.prepareStatement(sql, autoGeneratedKeys);
/*     */   }
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
/*     */   {
/* 205 */     return this.conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*     */   {
/* 210 */     return this.conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
/*     */   {
/* 215 */     return this.conn.prepareStatement(sql, columnIndexes);
/*     */   }
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
/*     */   {
/* 220 */     return this.conn.prepareStatement(sql, columnNames);
/*     */   }
/*     */   
/*     */   public void releaseSavepoint(Savepoint savepoint) throws SQLException
/*     */   {
/* 225 */     this.conn.releaseSavepoint(savepoint);
/*     */   }
/*     */   
/*     */   public void rollback() throws SQLException
/*     */   {
/* 230 */     this.conn.rollback();
/*     */   }
/*     */   
/*     */   public void rollback(Savepoint savepoint) throws SQLException
/*     */   {
/* 235 */     this.conn.rollback(savepoint);
/*     */   }
/*     */   
/*     */   public void setAutoCommit(boolean autoCommit) throws SQLException
/*     */   {
/* 240 */     this.conn.setAutoCommit(autoCommit);
/*     */   }
/*     */   
/*     */   public void setCatalog(String catalog) throws SQLException
/*     */   {
/* 245 */     this.conn.setCatalog(catalog);
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException
/*     */   {
/* 250 */     this.conn.setClientInfo(properties);
/*     */   }
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException
/*     */   {
/* 255 */     this.conn.setClientInfo(name, value);
/*     */   }
/*     */   
/*     */   public void setHoldability(int holdability) throws SQLException
/*     */   {
/* 260 */     this.conn.setHoldability(holdability);
/*     */   }
/*     */   
/*     */   public void setReadOnly(boolean readOnly) throws SQLException
/*     */   {
/* 265 */     this.conn.setReadOnly(readOnly);
/*     */   }
/*     */   
/*     */   public Savepoint setSavepoint() throws SQLException
/*     */   {
/* 270 */     return this.conn.setSavepoint();
/*     */   }
/*     */   
/*     */   public Savepoint setSavepoint(String name) throws SQLException
/*     */   {
/* 275 */     return this.conn.setSavepoint(name);
/*     */   }
/*     */   
/*     */   public void setTransactionIsolation(int level) throws SQLException
/*     */   {
/* 280 */     this.conn.setTransactionIsolation(level);
/*     */   }
/*     */   
/*     */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException
/*     */   {
/* 285 */     this.conn.setTypeMap(map);
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException
/*     */   {
/* 290 */     return (T)this.conn.unwrap(iface);
/*     */   }
/*     */   
/*     */   public int getNetworkTimeout() throws SQLException
/*     */   {
/* 295 */     return this.networkTimeout;
/*     */   }
/*     */   
/*     */   public void setNetworkTimeout(Executor exec, int timeout) throws SQLException
/*     */   {
/* 300 */     this.networkTimeout = timeout;
/*     */   }
/*     */   
/*     */   public void abort(Executor exec)
/*     */     throws SQLException
/*     */   {}
/*     */   
/*     */   public String getSchema()
/*     */     throws SQLException
/*     */   {
/* 310 */     return this.schema;
/*     */   }
/*     */   
/*     */   public void setSchema(String str) throws SQLException
/*     */   {
/* 315 */     this.schema = str;
/*     */   }
/*     */   
/*     */   void terminate() {
/*     */     try {
/* 320 */       this.conn.close();
/*     */     }
/*     */     catch (SQLException localSQLException) {}
/*     */   }
/*     */   
/*     */   long getLastUse() {
/* 326 */     return this.timestamp;
/*     */   }
/*     */   
/*     */   boolean inUse() {
/* 330 */     return this.inuse;
/*     */   }
/*     */   
/*     */   synchronized boolean lease() {
/* 334 */     if (this.inuse) {
/* 335 */       return false;
/*     */     }
/* 337 */     this.inuse = true;
/* 338 */     this.timestamp = System.currentTimeMillis();
/* 339 */     return true;
/*     */   }
/*     */   
/*     */   boolean isValid() {
/*     */     try {
/* 344 */       return this.conn.isValid(1);
/*     */     } catch (SQLException ex) {}
/* 346 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/mysql/JDBCConnectionPool.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */