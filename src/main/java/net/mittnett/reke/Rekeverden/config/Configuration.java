package net.mittnett.reke.Rekeverden.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        this.config.addDefault("access.default", 0);

        List<String> motd = new ArrayList<>();
        motd.add("Welcome to this server");
        motd.add(" - Ask an admin or mod to be given access.");
        this.config.addDefault("motd", motd);

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

    public int getDefaultAccess() {
      return this.config.getInt("access.default");
    }

    public List<String> getMotdLines()
    {
        return this.config.getStringList("motd");
    }

    public int getDatabasePort()
    {
        return this.config.getInt("db.port");
    }
}
