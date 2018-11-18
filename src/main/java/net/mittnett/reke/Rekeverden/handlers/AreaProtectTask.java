/*     */ package net.mittnett.reke.Rekeverden.handlers;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AreaProtectTask
/*     */   implements Runnable
/*     */ {
/*     */   private int x1;
/*     */   private int y1;
/*     */   private int z1;
/*     */   private int x2;
/*     */   private int y2;
/*     */   private int z2;
/*     */   private int newOwnerID;
/*     */   private World world;
/*     */   private UUID playerUUID;
/*     */   private Connection connection;
/*     */   private int currentUserID;
/*  35 */   private PreparedStatement ps1 = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  40 */   private PreparedStatement ps2 = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AreaProtectTask(int x1, int y1, int z1, int x2, int y2, int z2, int newOwnerID, World world, UUID playerUUID, Connection conn, int currentUserID)
/*     */   {
/*  54 */     this.x1 = x1;
/*  55 */     this.y1 = y1;
/*  56 */     this.z1 = z1;
/*  57 */     this.x2 = x2;
/*  58 */     this.y2 = y2;
/*  59 */     this.z2 = z2;
/*  60 */     this.newOwnerID = newOwnerID;
/*  61 */     this.currentUserID = currentUserID;
/*  62 */     this.world = world;
/*  63 */     this.playerUUID = playerUUID;
/*  64 */     this.connection = conn;
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/*  69 */     Player player = Bukkit.getServer().getPlayer(this.playerUUID);
/*     */     
/*  71 */     net.mittnett.reke.Rekeverden.Rekeverden.isAreaProtectorRunning = true;
/*  72 */     player.sendMessage(ChatColor.GREEN + "[AP] Starting to handle selection.");
/*     */     
/*     */     try
/*     */     {
/*  76 */       this.ps1 = this.connection.prepareStatement("INSERT INTO `r_blocklog`(`uid`, `x`, `y`, `z`, `world`, `block_id`, `block_data`, `timestamp`, `action`)VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
/*  77 */       this.ps1.setInt(1, this.currentUserID);
/*  78 */       this.ps1.setString(5, this.world.getName());
/*  79 */       this.ps1.setInt(8, (int)(System.currentTimeMillis() / 1000L));
/*     */       
/*  81 */       if (this.newOwnerID < 1)
/*     */       {
/*  83 */         this.ps1.setInt(9, 3);
/*     */         
/*  85 */         this.ps2 = this.connection.prepareStatement("DELETE FROM `r_blocks` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?");
/*  86 */       } else if (this.newOwnerID > 0)
/*     */       {
/*  88 */         this.ps1.setInt(9, 2);
/*     */         
/*  90 */         this.ps2 = this.connection.prepareStatement("REPLACE INTO `r_blocks`(`x`, `y`, `z`, `world`, `uid`)VALUES(?, ?, ?, ?, ?)");
/*  91 */         this.ps2.setInt(5, this.newOwnerID);
/*     */       }
/*     */       
/*     */ 
/*  95 */       this.ps2.setString(4, this.world.getName());
/*     */       
/*     */ 
/*  98 */       runOnSelection();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 105 */         if (this.ps1 != null) {
/* 106 */           this.ps1.close();
/*     */         }
/*     */         
/* 109 */         if (this.ps2 != null) {
/* 110 */           this.ps2.close();
/*     */         }
/*     */         
/* 113 */         if (this.connection != null) {
/* 114 */           this.connection.close();
/*     */         }
/*     */       } catch (SQLException e) {
/* 117 */         player.sendMessage(ChatColor.RED + "[AP]--> An SQLException occured while protecting blocks, please contact developer!");
/* 118 */         e.printStackTrace();
/*     */       }
/*     */       
/*     */ 
/* 122 */       net.mittnett.reke.Rekeverden.Rekeverden.isAreaProtectorRunning = false;
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 101 */       player.sendMessage(ChatColor.RED + "[AP]--> An SQLException occured while protecting blocks, please contact developer!");
/* 102 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 105 */         if (this.ps1 != null) {
/* 106 */           this.ps1.close();
/*     */         }
/*     */         
/* 109 */         if (this.ps2 != null) {
/* 110 */           this.ps2.close();
/*     */         }
/*     */         
/* 113 */         if (this.connection != null) {
/* 114 */           this.connection.close();
/*     */         }
/*     */       } catch (SQLException e) {
/* 117 */         player.sendMessage(ChatColor.RED + "[AP]--> An SQLException occured while protecting blocks, please contact developer!");
/* 118 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 123 */     player.sendMessage(ChatColor.GREEN + "[AP] Done");
/*     */   }
/*     */   
/*     */   private void runOnSelection() throws SQLException
/*     */   {
/* 128 */     for (int x = this.x1; x <= this.x2; x++) {
/* 129 */       for (int y = this.y1; y <= this.y2; y++) {
/* 130 */         for (int z = this.z1; z <= this.z2; z++) {
/* 131 */           Location loc = new Location(this.world, x, y, z);
/* 132 */           Block block = this.world.getBlockAt(loc);
/*     */           
/* 134 */           if ((block != null) && 
/* 135 */             (block.getType() != Material.AIR) && 
/* 136 */             (block.getType() != Material.LAVA) && 
/* 137 */             (block.getType() != Material.WATER))
/*     */           {
/* 139 */             this.ps1.setInt(2, x);
/* 140 */             this.ps1.setInt(3, y);
/* 141 */             this.ps1.setInt(4, z);
/* 142 */             this.ps1.setInt(6, block.getType().getId());
/* 143 */             this.ps1.setInt(7, block.getData());
/* 144 */             this.ps1.executeUpdate();
/*     */             
/*     */ 
/* 147 */             this.ps2.setInt(1, x);
/* 148 */             this.ps2.setInt(2, y);
/* 149 */             this.ps2.setInt(3, z);
/* 150 */             this.ps2.executeUpdate();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/handlers/AreaProtectTask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
