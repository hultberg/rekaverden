package net.mittnett.reke.Rekeverden;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.lambdaworks.redis.api.StatefulRedisConnection;
import net.mittnett.reke.Rekeverden.config.Configuration;
import net.mittnett.reke.Rekeverden.commands.*;
import net.mittnett.reke.Rekeverden.handlers.*;
import net.mittnett.reke.Rekeverden.listeners.BlockListener;
import net.mittnett.reke.Rekeverden.listeners.VehicleListener;
import net.mittnett.reke.Rekeverden.mysql.MySQLHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Rekeverden extends JavaPlugin {
    private Logger log;
    private static Rekeverden instance;
    public static boolean isAreaProtectorRunning = false;

    private final MySQLHandler sqlHandler = new MySQLHandler(this);
    private DatabaseHandler dbHandler;
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

        this.log.info(" - Connecting to SQL server...");
        this.dbHandler = new DatabaseHandler(this.log, this.config);

        if (!this.dbHandler.createConnection()) {
          this.log.severe("Could not connect to SQL server!");
          this.getServer().shutdown();
          return;
        }

        this.userHandler = new UserHandler(this);
        this.groupHandler = new GroupHandler(this);
        this.bpHandler = new BlockProtectionHandler(this.dbHandler, this.userHandler, this.log);
        this.blockInfoHandler = new BlockInfoHandler(this.dbHandler, this.userHandler, this.log);
        this.userHomeHandler = new UserHomeHandler(this.dbHandler);

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
        getCommand("i").setExecutor(new CustomGiveCommand(this.userHandler));

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new net.mittnett.reke.Rekeverden.listeners.PlayerListener(this), this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new VehicleListener(), this);
        pm.registerEvents(new net.mittnett.reke.Rekeverden.listeners.EntityListener(), this);

        this.log.info("[Rekeverden] Ready");
    }

    public void onDisable() {
        this.disableHandlers();
        this.log.info("[Rekeverden] plugin is shutting down...");
    }

    public void disableHandlers()
    {
        if (this.userHomeHandler != null)   { this.userHomeHandler.onDisable(); }
        if (this.blockInfoHandler != null)  { this.blockInfoHandler.onDisable(); }
        if (this.bpHandler != null)         { this.bpHandler.onDisable(); }
        if (this.groupHandler != null)      { this.groupHandler.onDisable(); }
        if (this.userHandler != null)       { this.userHandler.onDisable(); }
        if (this.dbHandler != null)         { this.dbHandler.onDisable(); }
    }

    public static Rekeverden getInstance() {
        return instance;
    }

    /**
     * @deprecated
     */
    public Connection getConnection() {
      return this.dbHandler.getConnection();
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
