/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */ 
/*    */ import org.bukkit.ChatColor;
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
/*    */ public class SetSpawnCommand
/*    */   implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*    */   {
/* 20 */     if (!(sender instanceof Player)) {
/* 21 */       sender.sendMessage("Set spawn can only be used by Players");
/* 22 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 26 */     Player player = (Player)sender;
/*    */     
/*    */ 
/* 29 */     if (!player.isOp()) {
/* 30 */       player.sendMessage(ChatColor.RED + "I'm sorry, but you do not have access to execute this command.");
/* 31 */       return true;
/*    */     }
/*    */     
/*    */ 
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