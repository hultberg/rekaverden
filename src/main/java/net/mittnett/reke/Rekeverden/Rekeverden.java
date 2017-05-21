package net.mittnett.reke.Rekeverden;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.mittnett.reke.Rekeverden.config.Configuration;
import net.mittnett.reke.Rekeverden.commands.*;
import net.mittnett.reke.Rekeverden.handlers.*;
import net.mittnett.reke.Rekeverden.listeners.BlockListener;
import net.mittnett.reke.Rekeverden.mysql.MySQLConnectionPool;
import net.mittnett.reke.Rekeverden.mysql.MySQLHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Rekeverden extends JavaPlugin {
    private Logger log;
    private static Rekeverden instance;
    public static boolean isAreaProtectorRunning = false;

    private boolean sqlConnected;

    private final MySQLHandler sqlHandler = new MySQLHandler(this);
    private MySQLConnectionPool sqlc;
    private Configuration config;
    private UserHandler userHandler;
    private GroupHandler groupHandler;
    private BlockProtectionHandler bpHandler;
    private BlockInfoHandler blockInfoHandler;
    private UserHomeHandler userHomeHandler;

    public void onEnable() {
        instance = this;
        
        this.config = new Configuration(this);
        this.config.addDefaults();
        this.config.save();
        
        this.log = getLogger();
        this.log.info("[Rekeverden] plugin is starting...");

        getLogger().info(" - Connecting to SQL server...");
        if (!sqlConnection()) {
            return;
        }

        this.userHandler = new UserHandler(this);
        this.groupHandler = new GroupHandler(this);
        this.bpHandler = new BlockProtectionHandler(this);
        this.blockInfoHandler = new BlockInfoHandler(this);
        this.userHomeHandler = new UserHomeHandler(this);

        this.userHandler.onEnable();
        this.groupHandler.onEnable();
        this.bpHandler.onEnable();
        this.blockInfoHandler.onEnable();
        this.userHomeHandler.onEnable();

        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new HomeCommand(this));
        getCommand("delhome").setExecutor(new HomeCommand(this));
        getCommand("listhome").setExecutor(new HomeCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand());
        getCommand("toggleselect").setExecutor(new net.mittnett.reke.Rekeverden.commands.ToggleSelectToolCommand(this));
        getCommand("protect").setExecutor(new net.mittnett.reke.Rekeverden.commands.ProtectAreaCommand(this));
        getCommand("gr").setExecutor(new net.mittnett.reke.Rekeverden.commands.GroupCommand(this));
        getCommand("tid").setExecutor(new TimeCommand());
        getCommand("eject").setExecutor(new EjectCommand());
        getCommand("i").setExecutor(new net.mittnett.reke.Rekeverden.commands.CustomGiveCommand());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new net.mittnett.reke.Rekeverden.listeners.PlayerListener(this), this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new net.mittnett.reke.Rekeverden.listeners.EntityListener(), this);

        this.log.info("[Rekeverden] Ready");
    }

    public void onDisable() {
        this.userHomeHandler.onDisable();
        this.blockInfoHandler.onDisable();
        this.bpHandler.onDisable();
        this.groupHandler.onDisable();
        this.userHandler.onDisable();

        if (this.sqlc != null) {
            getLogger().info(" - Closing SQL connection...");
            this.sqlc.close();
        }

        this.log.info("[Rekeverden] plugin is shutting down...");
    }

    public static Rekeverden getInstance() {
        return instance;
    }

    public boolean sqlConnection() {
        try {
            this.sqlc = new MySQLConnectionPool(
                this.config.getDatabaseHost(),
                this.config.getDatabaseName(),
                this.config.getDatabaseUser(),
                this.config.getDatabasePass(),
                this.config.getDatabasePort()
            );

            Connection conn = getConnection();
            if (conn == null) {
                getLogger().severe("Could not connect to SQL server!");
                getServer().shutdown();

                return false;
            }

            conn.close();
            this.sqlConnected = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return this.sqlConnected;
    }

    public Connection getConnection() {
        try {
            Connection conn = this.sqlc.getConnection();

            if ((!this.sqlConnected) && (conn != null)) {
                getLogger().info("SQL connection re-established.");
                this.sqlConnected = true;
            }

            return conn;
        } catch (Exception e) {
            this.sqlConnected = false;

            getLogger().severe("Could not fetch SQL connection! " + e.getMessage());
        }
        return null;
    }

    public BlockProtectionHandler getBlockProtectionHandler() {
        return this.bpHandler;
    }

    public BlockInfoHandler getBlockInfoHandler() {
        return this.blockInfoHandler;
    }

    public MySQLHandler getMySQLHandler() {
        return this.sqlHandler;
    }

    public GroupHandler getGroupHandler() {
        return this.groupHandler;
    }

    public UserHandler getUserHandler() {
        return this.userHandler;
    }

    public UserHomeHandler getUserHomeHandler() {
        return this.userHomeHandler;
    }
}
