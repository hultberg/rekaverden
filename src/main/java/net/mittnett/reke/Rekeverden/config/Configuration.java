package net.mittnett.reke.Rekeverden.config;

import java.io.File;
import net.mittnett.reke.Rekeverden.Rekeverden;
import org.bukkit.configuration.file.FileConfiguration;

public class Configuration
{
    protected Rekeverden plugin;
    protected FileConfiguration config;
    
    public Configuration(Rekeverden plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public void addDefaults()
    {
        this.config.addDefault("db.host", "");
        this.config.addDefault("db.user", "");
        this.config.addDefault("db.pass", "");
        this.config.addDefault("db.name", "");
        this.config.addDefault("db.port", 3306);
        this.config.options().copyDefaults(true);
    }
    
    public void save()
    {
        this.plugin.saveConfig();
    }
    
    public void reload()
    {
        this.plugin.reloadConfig();
    }
    
    public String getDatabaseHost()
    {
        return this.config.getString("db.host");
    }
    
    public String getDatabaseUser()
    {
        return this.config.getString("db.user");
    }
    
    public String getDatabasePass()
    {
        return this.config.getString("db.pass");
    }
    
    public String getDatabaseName()
    {
        return this.config.getString("db.name");
    }
    
    public int getDatabasePort()
    {
        return this.config.getInt("db.port");
    }
}