/*     */ package net.mittnett.reke.Rekeverden.commands;
/*     */ 
/*     */ import net.mittnett.reke.Rekeverden.Rekeverden;
/*     */ import net.mittnett.reke.Rekeverden.handlers.Group;
/*     */ import net.mittnett.reke.Rekeverden.handlers.GroupHandler;
/*     */ import net.mittnett.reke.Rekeverden.handlers.GroupInvite;
/*     */ import net.mittnett.reke.Rekeverden.handlers.User;
/*     */ import net.mittnett.reke.Rekeverden.handlers.UserHandler;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class GroupCommand implements org.bukkit.command.CommandExecutor
/*     */ {
/*     */   private Rekeverden plugin;
/*     */   private GroupHandler groupHandler;
/*     */   
/*     */   public GroupCommand(Rekeverden plugin)
/*     */   {
/*  21 */     this.plugin = plugin;
/*  22 */     this.groupHandler = plugin.getGroupHandler();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
/*     */   {
/*  29 */     if (!(sender instanceof Player)) {
/*  30 */       sender.sendMessage("This command can only be used by Players");
/*  31 */       return true;
/*     */     }
/*     */     
/*     */ 
/*  35 */     Player player = (Player)sender;
/*     */     
/*     */ 
/*  38 */     User user = this.plugin.getUserHandler().getUser(player.getUniqueId());
/*     */     
/*     */ 
/*  41 */     if (user.getAccessLevel() < 1) {
/*  42 */       player.sendMessage(ChatColor.RED + "You do not have access to use this command.");
/*  43 */       return true;
/*     */     }
/*     */     
/*  46 */     if (args.length == 0)
/*     */     {
/*  48 */       handleIndex(user, player);
/*     */     }
/*  50 */     else if ((args[0].equalsIgnoreCase("new")) && (args.length > 1)) {
/*  51 */       handleNew(user, player, args);
/*  52 */     } else if ((args[0].equalsIgnoreCase("invite")) && (args.length > 2)) {
/*  53 */       handleInvite(user, player, args);
/*  54 */     } else if ((args[0].equalsIgnoreCase("accept")) && (args.length > 1)) {
/*  55 */       handleAcceptInvite(user, player, args);
/*  56 */     } else if ((args[0].equalsIgnoreCase("deny")) && (args.length > 1)) {
/*  57 */       handleDenyInvite(user, player, args);
/*  58 */     } else if (args[0].equalsIgnoreCase("info")) {
/*  59 */       handleInfo(user, player, args);
/*  60 */     } else if (args[0].equalsIgnoreCase("invites")) {
/*  61 */       handleInvites(user, player, args);
/*  62 */     } else if ((args[0].equalsIgnoreCase("leave")) && (args.length > 1)) {
/*  63 */       handleLeave(user, player, args);
/*  64 */     } else if ((args[0].equalsIgnoreCase("kick")) && (args.length > 2)) {
/*  65 */       handleKick(user, player, args);
/*  66 */     } else if ((args[0].equalsIgnoreCase("group")) && (args.length > 1)) {
/*  67 */       handleGroupInformation(user, player, args);
/*     */     }
/*     */     
/*     */ 
/*  71 */     return true;
/*     */   }
/*     */   
/*     */   private void handleIndex(User user, Player player) {
/*  75 */     player.sendMessage(ChatColor.GOLD + "--------------- " + ChatColor.BLUE + "Group System" + ChatColor.GOLD + " ---------------");
/*  76 */     player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr new" + ChatColor.GRAY + " [name]" + ChatColor.WHITE + " -- Creates a new group");
/*  77 */     player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr info" + ChatColor.WHITE + " -- Get information about your current group memberships");
/*  78 */     player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr invite" + ChatColor.GRAY + " [groupID] [player]" + ChatColor.WHITE + " -- Invite a user to a group");
/*  79 */     player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr invites" + ChatColor.WHITE + " -- List all invites");
/*  80 */     player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr accept" + ChatColor.GRAY + " [inviteID]" + ChatColor.WHITE + " -- Accept a invite");
/*  81 */     player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr deny" + ChatColor.GRAY + " [inviteID]" + ChatColor.WHITE + " -- Deny a invite");
/*     */     
/*     */ 
/*  84 */     if (user.getGroups().size() > 0) {
/*  85 */       player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr group" + ChatColor.GRAY + " [groupID]" + ChatColor.WHITE + " -- Get information about one group.");
/*  86 */       player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr leave" + ChatColor.GRAY + " [groupID]" + ChatColor.WHITE + " -- Leave a group");
/*     */       
/*     */ 
/*  89 */       if (user.isOwnerOfAGroup() == true) {
/*  90 */         player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr kick" + ChatColor.GRAY + " [groupID] [player]" + ChatColor.WHITE + " -- Kick a member of a group.");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleNew(User user, Player player, String[] args)
/*     */   {
/*  97 */     if (args[1].length() > 20) {
/*  98 */       player.sendMessage(ChatColor.RED + "The name is too long. maximum is 20 characters.");
/*  99 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 103 */       Group newGroup = this.groupHandler.createGroup(args[1], user);
/* 104 */       this.groupHandler.addMembership(user, newGroup);
/* 105 */       player.sendMessage(ChatColor.GREEN + "Created the group " + ChatColor.WHITE + args[1] + ChatColor.GREEN + "!");
/*     */     } catch (Exception e) {
/* 107 */       player.sendMessage(e.getMessage());
/* 108 */       return;
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleInvite(User user, Player player, String[] args) {
/* 113 */     int toGroupID = 0;
/*     */     try {
/* 115 */       toGroupID = Integer.parseInt(args[1]);
/*     */     } catch (NumberFormatException e) {
/* 117 */       player.sendMessage(ChatColor.RED + "GroupID must be integer.");
/* 118 */       return;
/*     */     }
/*     */     
/* 121 */     Group toGroup = this.groupHandler.getGroup(toGroupID);
/* 122 */     if (toGroup == null) {
/* 123 */       player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED + " was not found in the database.");
/* 124 */       return;
/*     */     }
/* 126 */     if (!toGroup.getOwner().equals(user)) {
/* 127 */       player.sendMessage(ChatColor.RED + "Only owner of a group can invite new users.");
/* 128 */       return;
/*     */     }
/*     */     
/* 131 */     User inviteUser = this.plugin.getUserHandler().getUser(args[2]);
/* 132 */     if (inviteUser == null) {
/* 133 */       player.sendMessage(ChatColor.RED + "Requested user " + ChatColor.WHITE + args[2] + ChatColor.RED + " was not found in the database.");
/* 134 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 138 */       GroupInvite gi = this.groupHandler.createInvite(toGroup, inviteUser, user);
/* 139 */       inviteUser.addGroupInvite(gi);
/*     */       
/*     */ 
/* 142 */       Player playerInvited = this.plugin.getServer().getPlayer(inviteUser.getUuid());
/* 143 */       boolean wasOnline = false;
/*     */       
/* 145 */       if (((playerInvited instanceof Player)) && (playerInvited.isOnline())) {
/* 146 */         wasOnline = true;
/* 147 */         playerInvited.sendMessage(ChatColor.GREEN + "You have been invited to the group " + ChatColor.WHITE + toGroup
/* 148 */           .getName() + ChatColor.GREEN + " by " + ChatColor.WHITE + user
/* 149 */           .getName() + ChatColor.GREEN + "!");
/* 150 */         playerInvited.sendMessage(ChatColor.GREEN + "Write " + ChatColor.WHITE + "/gr accept " + gi.getInviteID() + ChatColor.GREEN + " to accept this invite.");
/*     */       }
/*     */       
/*     */ 
/* 154 */       player.sendMessage(ChatColor.GREEN + "The user " + ChatColor.WHITE + inviteUser.getName() + ChatColor.GREEN + " has been invited to the group.");
/* 155 */       if (wasOnline)
/* 156 */         player.sendMessage(ChatColor.GREEN + "The user was online and has been notified.");
/*     */     } catch (Exception e) {
/* 158 */       player.sendMessage(e.getMessage());
/* 159 */       return;
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleAcceptInvite(User user, Player player, String[] args) {
/* 164 */     int inviteID = 0;
/*     */     try {
/* 166 */       inviteID = Integer.parseInt(args[1]);
/*     */     } catch (NumberFormatException e) {
/* 168 */       player.sendMessage(ChatColor.RED + "Invite ID must be integer.");
/* 169 */       return;
/*     */     }
/*     */     
/* 172 */     GroupInvite gi = this.groupHandler.getGroupInvite(inviteID);
/* 173 */     if ((gi == null) || (!gi.getInvited().equals(user))) {
/* 174 */       player.sendMessage(ChatColor.RED + "The invite was not found.");
/* 175 */       return;
/*     */     }
/*     */     
/* 178 */     Group gr = gi.getToGroup();
/*     */     
/*     */     try
/*     */     {
/* 182 */       this.groupHandler.applyInvite(gi);
/* 183 */       this.groupHandler.deleteInvite(gi);
/* 184 */       gi = null;
/*     */       
/* 186 */       player.sendMessage(ChatColor.GREEN + "Invite has been accepted!");
/*     */       
/*     */ 
/* 189 */       gr.broadcast(player.getName() + " just became a member of this group.");
/*     */     }
/*     */     catch (Exception e) {
/* 192 */       player.sendMessage(e.getMessage());
/* 193 */       return;
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleDenyInvite(User user, Player player, String[] args) {
/* 198 */     int inviteID = 0;
/*     */     try {
/* 200 */       inviteID = Integer.parseInt(args[1]);
/*     */     } catch (NumberFormatException e) {
/* 202 */       player.sendMessage(ChatColor.RED + "Invite ID must be integer.");
/* 203 */       return;
/*     */     }
/*     */     
/* 206 */     GroupInvite gi = this.groupHandler.getGroupInvite(inviteID);
/* 207 */     if ((gi == null) || (!gi.getInvited().equals(user))) {
/* 208 */       player.sendMessage(ChatColor.RED + "The invite was not found.");
/* 209 */       return;
/*     */     }
/*     */     
/*     */ 
/* 213 */     this.groupHandler.deleteInvite(gi);
/* 214 */     gi = null;
/*     */     
/* 216 */     player.sendMessage(ChatColor.GREEN + "Invite has been deleted!");
/*     */   }
/*     */   
/*     */ 
/*     */   private void handleInfo(User user, Player player, String[] args)
/*     */   {
/* 222 */     player.sendMessage(ChatColor.BLUE + "Group memberships:");
/* 223 */     for (Group group : user.getGroups()) {
/* 224 */       player.sendMessage(ChatColor.DARK_GREEN + " Group: " + ChatColor.WHITE + group.getName());
/* 225 */       player.sendMessage(ChatColor.DARK_GREEN + " ID: " + ChatColor.WHITE + group.getGroupID());
/* 226 */       player.sendMessage(ChatColor.DARK_GREEN + " Owner: " + ChatColor.WHITE + group.getOwner().getName());
/* 227 */       player.sendMessage("");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void handleInvites(User user, Player player, String[] args)
/*     */   {
/* 234 */     player.sendMessage(ChatColor.BLUE + "Group invites:");
/* 235 */     for (GroupInvite gi : user.getGroupInvites()) {
/* 236 */       player.sendMessage(ChatColor.DARK_GREEN + " To group: " + ChatColor.WHITE + gi.getToGroup().getName());
/* 237 */       player.sendMessage(ChatColor.DARK_GREEN + " InviteID: " + ChatColor.WHITE + gi.getInviteID());
/* 238 */       player.sendMessage(ChatColor.DARK_GREEN + " Invitee: " + ChatColor.WHITE + gi.getInvitee().getName());
/* 239 */       player.sendMessage("");
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleLeave(User user, Player player, String[] args) {
/* 244 */     int fromGroupID = 0;
/*     */     try {
/* 246 */       fromGroupID = Integer.parseInt(args[1]);
/*     */     } catch (NumberFormatException e) {
/* 248 */       player.sendMessage(ChatColor.RED + "GroupID must be integer.");
/* 249 */       return;
/*     */     }
/*     */     
/* 252 */     Group fromGroup = this.groupHandler.getGroup(fromGroupID);
/* 253 */     if (fromGroup == null) {
/* 254 */       player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED + " was not found in the database.");
/* 255 */       return;
/*     */     }
/*     */     
/*     */ 
/* 259 */     if (!user.isMemberOf(fromGroup)) {
/* 260 */       player.sendMessage(ChatColor.RED + "You are not a member of this group.");
/* 261 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 266 */     if ((fromGroup.getOwner().getId() == user.getId()) && (fromGroup.getMembers().size() > 1)) {
/* 267 */       player.sendMessage(ChatColor.RED + "");
/* 268 */       return;
/*     */     }
/*     */     
/*     */ 
/* 272 */     this.groupHandler.removeMembership(user, fromGroup);
/* 273 */     fromGroup.broadcast(user.getName() + " has left the group.");
/* 274 */     player.sendMessage(ChatColor.GREEN + "You have left the group " + ChatColor.WHITE + fromGroup.getName() + ChatColor.GREEN + ".");
/*     */   }
/*     */   
/*     */   private void handleKick(User user, Player player, String[] args)
/*     */   {
/* 279 */     int fromGroupID = 0;
/*     */     try {
/* 281 */       fromGroupID = Integer.parseInt(args[1]);
/*     */     } catch (NumberFormatException e) {
/* 283 */       player.sendMessage(ChatColor.RED + "GroupID must be integer.");
/* 284 */       return;
/*     */     }
/*     */     
/* 287 */     Group fromGroup = this.groupHandler.getGroup(fromGroupID);
/* 288 */     if (fromGroup == null) {
/* 289 */       player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED + " was not found in the database.");
/* 290 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 295 */     if (fromGroup.getOwner().getId() != user.getId()) {
/* 296 */       player.sendMessage(ChatColor.RED + "You must be the owner to kick a member of the group.");
/* 297 */       return;
/*     */     }
/*     */     
/*     */ 
/* 301 */     if (args[2].trim().length() < 1) {
/* 302 */       player.sendMessage(ChatColor.RED + "Please name the player you would like to kick out of the group.");
/* 303 */       player.sendMessage("/gr kick [groupID] [player]");
/* 304 */       return;
/*     */     }
/*     */     
/* 307 */     User toKick = this.plugin.getUserHandler().getUser(args[2].trim());
/* 308 */     if ((toKick == null) || (!toKick.isMemberOf(fromGroup))) {
/* 309 */       player.sendMessage(ChatColor.RED + "User " + ChatColor.WHITE + args[2] + ChatColor.RED + " was not found or is not a member of this group.");
/* 310 */       return;
/*     */     }
/*     */     
/*     */ 
/* 314 */     this.groupHandler.removeMembership(toKick, fromGroup);
/* 315 */     fromGroup.broadcast(toKick.getName() + " was kicked out of the group.");
/* 316 */     player.sendMessage(ChatColor.GREEN + "You have kicked " + ChatColor.WHITE + toKick.getName() + ChatColor.GREEN + " from " + ChatColor.WHITE + fromGroup.getName() + ChatColor.GREEN + ".");
/*     */     
/*     */ 
/* 319 */     Player targetPlayer = this.plugin.getServer().getPlayer(toKick.getUuid());
/* 320 */     if (((targetPlayer instanceof Player)) && (targetPlayer.isOnline())) {
/* 321 */       targetPlayer.sendMessage(ChatColor.RED + "You have been kicked from the group " + ChatColor.WHITE + fromGroup.getName() + ChatColor.RED + " by " + ChatColor.WHITE + user
/* 322 */         .getName() + ChatColor.RED + ".");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void handleGroupInformation(User user, Player player, String[] args)
/*     */   {
/* 329 */     int groupID = 0;
/*     */     try {
/* 331 */       groupID = Integer.parseInt(args[1]);
/*     */     } catch (NumberFormatException e) {
/* 333 */       player.sendMessage(ChatColor.RED + "GroupID must be integer.");
/* 334 */       return;
/*     */     }
/*     */     
/* 337 */     Group group = this.groupHandler.getGroup(groupID);
/* 338 */     if (group == null) {
/* 339 */       player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED + " was not found in the database.");
/* 340 */       return;
/*     */     }
/*     */     
/*     */ 
/* 344 */     if (!user.isMemberOf(group)) {
/* 345 */       player.sendMessage(ChatColor.RED + "You are not a member of this group.");
/* 346 */       return;
/*     */     }
/*     */     
/* 349 */     String members = "";
/* 350 */     for (User member : group.getMembers()) {
/* 351 */       members = members + member.getName() + ", ";
/*     */     }
/* 353 */     members = members.trim().substring(0, members.length() - 2) + ".";
/*     */     
/*     */ 
/* 356 */     player.sendMessage(ChatColor.GOLD + "--------------- " + ChatColor.BLUE + "Group Info" + ChatColor.GOLD + " ---------------");
/* 357 */     player.sendMessage(ChatColor.DARK_GREEN + "Name: " + ChatColor.WHITE + group.getName());
/* 358 */     player.sendMessage(ChatColor.DARK_GREEN + "ID: " + ChatColor.WHITE + group.getGroupID());
/* 359 */     player.sendMessage(ChatColor.DARK_GREEN + "Owner: " + ChatColor.WHITE + group.getOwner().getName());
/* 360 */     player.sendMessage(ChatColor.DARK_GREEN + "Members: " + ChatColor.WHITE + members);
/*     */   }
/*     */ }


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/commands/GroupCommand.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */