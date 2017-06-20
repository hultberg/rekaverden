package net.mittnett.reke.Rekeverden.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomGiveCommand implements CommandExecutor {
  public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("This command can only be used by Players");
      return true;
    }

    Player player = (Player) commandSender;

    if (args.length == 0) {
      return false;
    }

    String matName = args[0];
    Material foundMat = Material.getMaterial(matName);

    if (foundMat == null) {
      player.sendMessage(ChatColor.RED + "Unable to find material " + matName);
      return false;
    }

    int amountMaterial = 64;

    if (args.length >= 1) {
      try {
        amountMaterial = Integer.parseInt(args[1]);
      } catch (NumberFormatException localNumberFormatException) {
      }
    }

    short damage = 0;

    if (args.length >= 3) {
      try {
        damage = (short) Integer.parseInt(args[2]);
      } catch (NumberFormatException localNumberFormatException1) {
      }
    }

    ItemStack stack = null;
    try {
      stack = new ItemStack(foundMat, amountMaterial, damage);
    } catch (Exception localException) {
    }

    if (stack == null) {
      player.sendMessage(ChatColor.RED + "Unable to find the material and damage.");
      return false;
    }

    player.getInventory().addItem(new ItemStack[] { stack });
    return true;
  }
}

/*
 * Location:
 * /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/
 * commands/CustomGiveCommand.class Java compiler version: 7 (51.0) JD-Core
 * Version: 0.7.1
 */
