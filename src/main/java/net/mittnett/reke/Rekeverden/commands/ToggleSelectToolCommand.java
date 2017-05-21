/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */ 
/*    */ import net.mittnett.reke.Rekeverden.Rekeverden;
/*    */ import net.mittnett.reke.Rekeverden.handlers.User;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class ToggleSelectToolCommand implements CommandExecutor
/*    */ {
/*    */   private Rekeverden plugin;
/*    */   
/*    */   public ToggleSelectToolCommand(Rekeverden plugin)
/*    */   {
/* 17 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*    */   {
/* 24 */     if (!(sender instanceof Player)) {
/* 25 */       sender.sendMessage("This command can only be used by Players");
/* 26 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 30 */     Player player = (Player)sender;
/*    */     
/*    */ 
/* 33 */     User user = this.plugin.getUserHandler().getUser(player.getUniqueId());
/*    */     
/*    */ 
/* 36 */     if (user.getAccessLevel() < 3) {
/* 37 */       player.sendMessage(ChatColor.RED + "You do not have access to use this command.");
/* 38 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 42 */     if (user.hasEnabledSelectTool()) {
/* 43 */       user.setHasEnabledSelectTool(false);
/* 44 */       player.sendMessage(ChatColor.AQUA + "[SEL] Select tool has been disabled.");
/*    */     } else {
/* 46 */       user.setHasEnabledSelectTool(true);
/* 47 */       player.sendMessage(ChatColor.AQUA + "[SEL] Select tool has been enabled.");
/*    */     }
/*    */     
/*    */ 
/* 51 */     user.setSelectToolPoint1(null);
/* 52 */     user.setSelectToolPoint2(null);
/*    */     
/* 54 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/ToggleSelectToolCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */