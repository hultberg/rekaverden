/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */
/*    */ import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import org.bukkit.World;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */
/*    */
/*    */


public class SpawnCommand extends BaseCommand {
  public SpawnCommand(UserHandler userHandler) {
    super(userHandler, User.GUEST);
  }

  @Override
  public boolean execute(Player player, User user, String[] args, String label) {
    if (user.isRestricted()) {
      return true;
    }

    player.teleport(player.getWorld().getSpawnLocation());
    return true;
  }
}


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/SpawnCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
