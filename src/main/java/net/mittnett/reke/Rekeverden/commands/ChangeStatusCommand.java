package net.mittnett.reke.Rekeverden.commands;

import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChangeStatusCommand extends BaseCommand {
  public ChangeStatusCommand(UserHandler userHandler) {
    super(userHandler, User.MODERATOR);
  }

  @Override
  public boolean execute(Player player, User user, String[] args, String label) {
    if (args.length < 2) {
      return false;
    }

    // Arg 0 is player.
    // Arg 1 is new status (int)

    // Validate parameters

    User targetUser = this.userHandler.getUser(args[0]);

    if (targetUser == null) {
      player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED + " was not found.");
      return false;
    }

    int newStatus;

    try {
      newStatus = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "Number " + ChatColor.WHITE + args[1] + ChatColor.RED + " is invalid.");
      return false;
    }

    // Deny mods elevating anyone to admins
    if (!player.isOp() && !user.hasAccessLevel(newStatus)) {
      player.sendMessage(ChatColor.RED + "Permission denied to elevate another player to a higher access level than your own.");
      return false;
    }

    // Deny Mod changing an admins level.
    if (!player.isOp() && targetUser.getAccessLevel() >= user.getAccessLevel()) {
      player.sendMessage(ChatColor.RED + "Permission denied to elevate another player with a higher access level than your own.");
      return false;
    }

    this.userHandler.changeStatus(targetUser, newStatus);

    // Re-fetch the user.
    User newTargetUser = this.userHandler.getUser(targetUser.getId());
    this.userHandler.updatePlayerCanPickUp(newTargetUser);

    Player target = Bukkit.getPlayer(args[0]);
    if (target != null) {
      this.userHandler.setDisplayName(target);
    }

    String hasBeen = "elevated";
    if (targetUser.getAccessLevel() > newStatus) {
      hasBeen = "demoted";
    }

    Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Player " + ChatColor.WHITE + targetUser.getName()
      + ChatColor.DARK_GREEN + " has been " + hasBeen + " to "
      + newTargetUser.getDisplayColor() + newTargetUser.getDisplayPrefixName());

    return true;
  }
}
