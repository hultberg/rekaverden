/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */ 
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
/*    */ public class SpawnCommand
/*    */   implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*    */   {
/* 18 */     if (!(sender instanceof Player)) {
/* 19 */       sender.sendMessage("Spawn can only be used by Players");
/* 20 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 24 */     Player player = (Player)sender;
/*    */     
/*    */ 
/* 27 */     World world = player.getWorld();
/*    */     
/*    */ 
/* 30 */     player.teleport(world.getSpawnLocation());
/*    */     
/* 32 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/SpawnCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */