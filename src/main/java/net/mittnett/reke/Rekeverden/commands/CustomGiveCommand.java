/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */ 
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ 
/*    */ public class CustomGiveCommand implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
/*    */   {
/* 15 */     if (!(commandSender instanceof Player)) {
/* 16 */       commandSender.sendMessage("This command can only be used by Players");
/* 17 */       return true;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 27 */     Player player = (Player)commandSender;
/*    */     
/* 29 */     if (args.length == 0) {
/* 30 */       return false;
/*    */     }
/*    */     
/*    */ 
/* 34 */     String matName = args[0];
/* 35 */     Material foundMat = Material.getMaterial(matName);
/*    */     
/* 37 */     if (foundMat == null) {
/* 38 */       player.sendMessage(ChatColor.RED + "Unable to find material " + matName);
/* 39 */       return false;
/*    */     }
/*    */     
/*    */ 
/* 43 */     int amountMaterial = 64;
/*    */     
/* 45 */     if (args.length >= 1) {
/*    */       try {
/* 47 */         amountMaterial = Integer.parseInt(args[1]);
/*    */       }
/*    */       catch (NumberFormatException localNumberFormatException) {}
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 54 */     short damage = 0;
/*    */     
/* 56 */     if (args.length >= 2) {
/*    */       try {
/* 58 */         damage = (short)Integer.parseInt(args[2]);
/*    */       }
/*    */       catch (NumberFormatException localNumberFormatException1) {}
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 65 */     ItemStack stack = null;
/*    */     try {
/* 67 */       stack = new ItemStack(foundMat, amountMaterial, damage);
/*    */     }
/*    */     catch (Exception localException) {}
/*    */     
/*    */ 
/* 72 */     if (stack == null) {
/* 73 */       player.sendMessage(ChatColor.RED + "Unable to find the material and damage.");
/* 74 */       return false;
/*    */     }
/*    */     
/*    */ 
/* 78 */     player.getInventory().addItem(new ItemStack[] { stack });
/* 79 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/CustomGiveCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */