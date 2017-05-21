package net.mittnett.reke.Rekeverden.commands;

import net.md_5.bungee.api.ChatColor;
import net.mittnett.reke.Rekeverden.Rekeverden;
import net.mittnett.reke.Rekeverden.handlers.HomeWaypoint;
import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import net.mittnett.reke.Rekeverden.handlers.UserHomeHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class HomeCommand implements CommandExecutor {

    private UserHandler userHandler;
    private UserHomeHandler userHomeHandler;

    public HomeCommand(Rekeverden plugin)
    {
        this.userHandler = plugin.getUserHandler();
        this.userHomeHandler = plugin.getUserHomeHandler();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be used by Players");
            return true;
        }

        Player player = ((Player) commandSender);
        User user = this.userHandler.getUser(player.getUniqueId());

        switch (command.getName()) {
            case "delhome": {

                if (args.length > 0) {
                    String theName = args[0];
                    HashMap<String, HomeWaypoint> homes = this.userHomeHandler.getUserHomes(user);

                    if (homes.containsKey(theName) == true) {
                        this.userHomeHandler.deleteHome(homes.get(theName).getName(), user);
                        player.sendMessage(ChatColor.GREEN + "Home " + ChatColor.WHITE + theName + ChatColor.GREEN + " was deleted.");
                    } else {
                        player.sendMessage(ChatColor.RED + "No home found with that name.");
                    }
                }

                break;
            }

            case "sethome": {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "You must provide a name to this home.");
                } else if (args.length > 0) {
                    // Find the one with the name provided
                    String theNewName = args[0];
                    HashMap<String, HomeWaypoint> homes = this.userHomeHandler.getUserHomes(user);

                    if (homes.containsKey(theNewName) == false) {
                        this.userHomeHandler.createHome(theNewName, user, player.getLocation(), player.getWorld());
                        player.sendMessage(ChatColor.GREEN + "Home " + ChatColor.WHITE + theNewName + ChatColor.GREEN + " was created.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You already have a home with that name.");
                    }
                }
                break;
            }

            case "listhome": {
                player.sendMessage(ChatColor.BLUE + "All your homes:");

                HashMap<String, HomeWaypoint> homes = this.userHomeHandler.getUserHomes(user);

                if (homes.isEmpty() == false) {
                    for (HomeWaypoint home : homes.values()) {
                        player.sendMessage(ChatColor.GRAY + " - " + ChatColor.RESET + home.getName() + ", " + home.getLocation().getBlockX() + ", " + home.getLocation().getBlockY() + ", " + home.getLocation().getBlockZ() + ", world: " + home.getLocation().getWorld().getName());
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You have no homes set.");
                }
                break;
            }

            case "home": {

                // If no args are provided, teleport to primary home if any.
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Please provide the name of a home too teleport to.");
                } else if (args.length > 0) {
                    // Find the one with the name provided
                    String theName = args[0];
                    HashMap<String, HomeWaypoint> homes1 = this.userHomeHandler.getUserHomes(user);

                    if ((homes1.isEmpty() == false) && (homes1.containsKey(theName) == true)) {
                        player.teleport(homes1.get(theName).getLocation());
                    } else {
                        player.sendMessage(ChatColor.RED + "Home by name " + ChatColor.WHITE + theName + ChatColor.RED + " do not exists.");
                    }
                }

                break;
            }
        }

        return true;

    }
}
