package net.mittnett.reke.Rekeverden.commands;

import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RestrictPlayerCommand extends BaseCommand {
  public RestrictPlayerCommand(UserHandler userHandler) {
    super(userHandler, User.MODERATOR);
  }

  @Override
  public boolean execute(Player player, User user, String[] args, String label) {
    if (args.length < 2) return false;

    User targetUser = this.userHandler.getUser(args[0]);

    if (targetUser == null) {
      player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED + " was not found.");
      return false;
    }

    int restrictCode;

    try {
      restrictCode = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "Number " + ChatColor.WHITE + args[1] + ChatColor.RED + " is invalid.");
      return false;
    }

    User newTargetUser = new User(targetUser);
    newTargetUser.setRestricted(restrictCode == 1);

    this.userHandler.updateUser(newTargetUser);
    this.userHandler.updatePlayerCanPickUp(newTargetUser);

    Player targetPlayer = Bukkit.getPlayer(targetUser.getName());

    if (targetPlayer != null) {
      this.userHandler.alertRestricting(targetPlayer, newTargetUser.isRestricted());
    }

    if (newTargetUser.isRestricted()) {
      this.userHandler.alertMods(ChatColor.RED + "Player " + ChatColor.WHITE + targetUser.getName()
        + ChatColor.RED + " has been set as restricted by " + user.getDisplayName() + ChatColor.RED + ".");
    } else {
      this.userHandler.alertMods(ChatColor.RED + "Player " + ChatColor.WHITE + targetUser.getName()
        + ChatColor.RED + " is no longer restricted by " + user.getDisplayName() + ChatColor.RED + ".");
    }

    return true;
  }
}
