/*    */ package net.mittnett.reke.Rekeverden.handlers;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import net.md_5.bungee.api.ChatColor;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ public class Group
/*    */ {
/*    */   private final int id;
/*    */   private String name;
/*    */   private User owner;
/*    */   private Set<User> members;
/*    */   
/*    */   public Group(int id, String name, User owner)
/*    */   {
/* 20 */     this.id = id;
/* 21 */     this.name = name;
/* 22 */     this.owner = owner;
/* 23 */     this.members = new HashSet();
/*    */   }
/*    */   
/*    */   public int getGroupID() {
/* 27 */     return this.id;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 31 */     return this.name;
/*    */   }
/*    */   
/*    */   public User getOwner() {
/* 35 */     return this.owner;
/*    */   }
/*    */   
/*    */   public void setOwner(User owner) {
/* 39 */     this.owner = owner;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Set<User> getMembers()
/*    */   {
/* 48 */     return this.members;
/*    */   }
/*    */   
/*    */   public void addMember(User member) {
/* 52 */     this.members.add(member);
/*    */   }
/*    */   
/*    */   public void removeMember(User member) {
/* 56 */     this.members.remove(member);
/*    */   }
/*    */   
/*    */   public void setMembers(Set<User> members) {
/* 60 */     this.members = members;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void broadcast(String message)
/*    */   {
/* 69 */     for (User user : this.members) {
/* 70 */       Player pl = Bukkit.getServer().getPlayer(user.getUuid());
/* 71 */       if (((pl instanceof Player)) && (pl.isOnline())) {
/* 72 */         pl.sendMessage(ChatColor.DARK_GRAY + "(" + getName() + ") " + ChatColor.RESET + message);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public boolean equals(Group group) {
/* 78 */     return group.getGroupID() == getGroupID();
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/handlers/Group.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */