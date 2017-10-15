package net.mittnett.reke.Rekeverden.commands;

import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegisterUserCommand extends BaseCommand {
  public RegisterUserCommand(UserHandler userHandler) {
    super(userHandler, User.MODERATOR);
  }

  @Override
  public boolean execute(Player player, User user, String[] args, String label) {
    if (args.length == 0) {
      return false;
    }

    User targetUser = this.userHandler.getUser(args[0]);
    if (targetUser == null) {
      player.sendMessage(ChatColor.RED + "Unable to find player " + ChatColor.RESET + args[0] + ChatColor.RED + " in the database.");
      return false;
    }

    // Deny changing an target with a higher level than the current user.
    if (!player.isOp() && targetUser.getAccessLevel() >= user.getAccessLevel()) {
      player.sendMessage(ChatColor.RED + "Permission denied to elevate another player with a higher access level than your own.");
      return false;
    }

    this.userHandler.changeStatus(targetUser, User.BUILDER);

    User newTargetUser = this.userHandler.getUser(targetUser.getId());
    this.userHandler.updatePlayerCanPickUp(newTargetUser);

    Player target = Bukkit.getPlayer(args[0]);
    if (target != null) {
      this.userHandler.setDisplayName(target);
      target.sendMessage(ChatColor.GREEN + "You have been granted building rights on this server, welcome.");
    }

    Bukkit.broadcastMessage(newTargetUser.getDisplayName() + ChatColor.GREEN + " has been granted building rights on this server, welcome.");

    return true;
  }
}
