package net.mittnett.reke.Rekeverden.commands;

import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeleportCommand extends BaseCommand {
  public TeleportCommand(UserHandler userHandler) {
    super(userHandler, User.MODERATOR);
  }

  @Override
  public boolean execute(Player player, User user, String[] args, String label) {
    if (args.length < 1) {
      return false;
    }

    if (user.isRestricted()) {
      return true;
    }

    Player targetPlayer = Bukkit.getPlayer(args[0]);
    if (targetPlayer == null) {
      player.sendMessage(ChatColor.RED + "Unable to find player " + ChatColor.RESET + args[0] + ChatColor.RED + " on the server.");
      return true; // Command usage is correct.
    }

    if (label.equalsIgnoreCase("tphere")) {
      targetPlayer.teleport(player);
    } else {
      player.teleport(targetPlayer);
    }

    return true;
  }
}
