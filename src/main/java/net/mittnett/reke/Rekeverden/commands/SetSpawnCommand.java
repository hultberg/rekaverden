/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */
/*    */ import net.mittnett.reke.Rekeverden.handlers.User;
import net.mittnett.reke.Rekeverden.handlers.UserHandler;
import org.bukkit.ChatColor;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */
/*    */
/*    */
/*    */
/*    */
public class SetSpawnCommand extends BaseCommand
{
  public SetSpawnCommand(UserHandler userHandler) {
    super(userHandler, User.ADMIN);
  }

/*    */   public boolean execute(Player player, User user, String[] args, String label)
/*    */   {
/* 35 */     World world = player.getWorld();
/*    */
/*    */
/* 38 */     Location location = player.getLocation();
/*    */
/*    */
/* 41 */     world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
/*    */
/*    */
/* 44 */     player.sendMessage(ChatColor.GREEN + "New spawn of world " + ChatColor.WHITE + world.getName() + ChatColor.GREEN + " has been set.");
/*    */
/* 46 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/SetSpawnCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
