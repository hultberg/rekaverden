/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */ 
/*    */ import net.mittnett.reke.Rekeverden.Rekeverden;
/*    */ import net.mittnett.reke.Rekeverden.handlers.User;
/*    */ import net.mittnett.reke.Rekeverden.handlers.UserHandler;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class ProtectAreaCommand implements CommandExecutor
/*    */ {
/*    */   private Rekeverden plugin;
/*    */   
/*    */   public ProtectAreaCommand(Rekeverden plugin)
/*    */   {
/* 19 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*    */   {
/* 26 */     if (!(sender instanceof Player)) {
/* 27 */       sender.sendMessage("This command can only be used by Players");
/* 28 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 32 */     Player player = (Player)sender;
/*    */     
/*    */ 
/* 35 */     User user = this.plugin.getUserHandler().getUser(player.getUniqueId());
/*    */     
/*    */ 
/* 38 */     if (user.getAccessLevel() < 3) {
/* 39 */       player.sendMessage(ChatColor.RED + "You do not have access to use this command.");
/* 40 */       return true;
/*    */     }
/*    */     
/* 43 */     if (!user.hasEnabledSelectTool()) {
/* 44 */       player.sendMessage(ChatColor.RED + "Select tool must be enabled to use this command.");
/* 45 */       return true;
/*    */     }
/*    */     
/* 48 */     if ((user.getSelectToolPoint1() == null) || (user.getSelectToolPoint2() == null)) {
/* 49 */       player.sendMessage(ChatColor.RED + "A selection of two points must be made before using command.");
/* 50 */       return true;
/*    */     }
/*    */     
/* 53 */     if (user.countSelection() > 10000) {
/* 54 */       player.sendMessage(ChatColor.RED + "A selection of more than 10,000 blocks is not allowed, it will crash the server.");
/* 55 */       return true;
/*    */     }
/*    */     
/* 58 */     if (Rekeverden.isAreaProtectorRunning == true) {
/* 59 */       player.sendMessage(ChatColor.RED + "An area protector instance is already running, please wait until it finishes.");
/* 60 */       return true;
/*    */     }
/*    */     
/* 63 */     int newOwnerID = user.getId();
/*    */     
/*    */ 
/* 66 */     if ((args.length > 0) && (args[0].trim().length() > 0)) {
/* 67 */       User newOwner = this.plugin.getUserHandler().getUser(args[0].trim());
/* 68 */       if (newOwner == null) {
/* 69 */         player.sendMessage(ChatColor.RED + "Requested user " + ChatColor.WHITE + args[0] + ChatColor.RED + " was not found in the database.");
/* 70 */         return true;
/*    */       }
/*    */       
/* 73 */       newOwnerID = newOwner.getId();
/*    */     }
/*    */     
/*    */ 
/* 77 */     org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new net.mittnett.reke.Rekeverden.handlers.AreaProtectTask(user
/* 78 */       .getMinimumSelectedPoint().getBlockX(), user
/* 79 */       .getMinimumSelectedPoint().getBlockY(), user
/* 80 */       .getMinimumSelectedPoint().getBlockZ(), user
/* 81 */       .getMaximumSelectedPoint().getBlockX(), user
/* 82 */       .getMaximumSelectedPoint().getBlockY(), user
/* 83 */       .getMaximumSelectedPoint().getBlockZ(), newOwnerID, player
/*    */       
/* 85 */       .getWorld(), player
/* 86 */       .getUniqueId(), this.plugin
/* 87 */       .getConnection(), user
/* 88 */       .getId()));
/*    */     
/*    */ 
/* 91 */     player.sendMessage(ChatColor.GREEN + "Area proector has been scheduled.");
/*    */     
/* 93 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/ProtectAreaCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */