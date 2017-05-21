/*    */ package net.mittnett.reke.Rekeverden.commands;
/*    */ 
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class TimeCommand implements CommandExecutor
/*    */ {
/*    */   public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
/*    */   {
/* 13 */     if (!(commandSender instanceof Player)) {
/* 14 */       commandSender.sendMessage("This command can only be used by Players");
/* 15 */       return true;
/*    */     }
/*    */     
/*    */ 
/* 19 */     Player player = (Player)commandSender;
/*    */     
/* 21 */     if (args.length == 0) {
/* 22 */       player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "tid day" + ChatColor.WHITE + " -- Set time to day");
/* 23 */       player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "tid night" + ChatColor.WHITE + " -- Set time to night");
/* 24 */       player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "tid reset" + ChatColor.WHITE + " -- Set your time to server-time");
/* 25 */       player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "tid " + ChatColor.GRAY + " [int]" + ChatColor.WHITE + " -- Set time to any second.");
/* 26 */     } else if (args.length >= 1) {
/* 27 */       String firstArg = args[0];
/*    */       
/*    */ 
/* 30 */       if (firstArg.equalsIgnoreCase("reset")) {
/* 31 */         player.resetPlayerTime();
/* 32 */         player.sendMessage(ChatColor.YELLOW + "The time has been reset.");
/* 33 */         return true;
/*    */       }
/*    */       
/* 36 */       long curtime = player.getPlayerTime();
/* 37 */       long newtime = curtime - curtime % 24000L;
/*    */       
/* 39 */       if (firstArg.equalsIgnoreCase("day")) {
/* 40 */         newtime += 6000L;
/* 41 */         player.sendMessage(ChatColor.YELLOW + "The time has been set to day");
/* 42 */       } else if (firstArg.equalsIgnoreCase("night")) {
/* 43 */         newtime += 14000L;
/* 44 */         player.sendMessage(ChatColor.YELLOW + "The time has been set to night");
/*    */       } else {
/*    */         try {
/* 47 */           newtime += Integer.parseInt(firstArg);
/* 48 */           player.sendMessage(ChatColor.YELLOW + "The time has been set to " + newtime);
/*    */         } catch (NumberFormatException e) {
/* 50 */           return false;
/*    */         }
/*    */       }
/*    */       
/* 54 */       player.setPlayerTime(newtime, false);
/*    */     }
/*    */     
/* 57 */     return true;
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/TimeCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */