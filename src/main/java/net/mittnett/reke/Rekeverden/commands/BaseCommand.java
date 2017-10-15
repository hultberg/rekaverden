package net.mittnett.reke.Rekeverden.commands;

import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements CommandExecutor {
  protected UserHandler userHandler;
  private int requiredAccess;

  protected BaseCommand(UserHandler userHandler, int requiredAccess) {
    this.userHandler = userHandler;
    this.requiredAccess = requiredAccess;
  }

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("This command can only be used by Players");
      return true;
    }

    Player player = (Player) commandSender;
    User user = this.userHandler.getUser(player.getUniqueId());

    if (!player.isOp() && !user.hasAccessLevel(this.requiredAccess)) {
      player.sendMessage(ChatColor.RED + "Permission denied.");
      return true;
    }

    return this.execute(player, user, args, label);
  }

  public abstract boolean execute(Player player, User user, String[] args, String label);

  public Player findPlayer(Player asker, String name) {
    Player target = Bukkit.getPlayer(name);

    if (target == null) {
      asker.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + name + ChatColor.RED + " was not found.");
      return null;
    }

    return target;
  }
}
