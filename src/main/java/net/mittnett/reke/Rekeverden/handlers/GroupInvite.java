/*    */ package net.mittnett.reke.Rekeverden.handlers;
/*    */ 
/*    */ public class GroupInvite
/*    */ {
/*    */   private int inviteID;
/*    */   private User invited;
/*    */   private User invitee;
/*    */   private Group toGroup;
/*    */   
/*    */   public GroupInvite(int inviteID, User invited, User invitee, Group toGroup) {
/* 11 */     this.inviteID = inviteID;
/* 12 */     this.invited = invited;
/* 13 */     this.invitee = invitee;
/* 14 */     this.toGroup = toGroup;
/*    */   }
/*    */   
/*    */   public int getInviteID() {
/* 18 */     return this.inviteID;
/*    */   }
/*    */   
/*    */   public void setInviteID(int inviteID) {
/* 22 */     this.inviteID = inviteID;
/*    */   }
/*    */   
/*    */   public User getInvited() {
/* 26 */     return this.invited;
/*    */   }
/*    */   
/*    */   public void setInvited(User invited) {
/* 30 */     this.invited = invited;
/*    */   }
/*    */   
/*    */   public User getInvitee() {
/* 34 */     return this.invitee;
/*    */   }
/*    */   
/*    */   public void setInvitee(User invitee) {
/* 38 */     this.invitee = invitee;
/*    */   }
/*    */   
/*    */   public Group getToGroup() {
/* 42 */     return this.toGroup;
/*    */   }
/*    */   
/*    */   public void setToGroup(Group toGroup) {
/* 46 */     this.toGroup = toGroup;
/*    */   }
/*    */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/handlers/GroupInvite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */