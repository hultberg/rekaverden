package net.mittnett.reke.Rekeverden.commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.reke.Rekeverden.Rekeverden;
import net.mittnett.reke.Rekeverden.handlers.Group;
import net.mittnett.reke.Rekeverden.handlers.GroupHandler;
import net.mittnett.reke.Rekeverden.handlers.GroupInvite;
import net.mittnett.reke.Rekeverden.handlers.User;

public class GroupCommand implements org.bukkit.command.CommandExecutor {
  private Rekeverden plugin;
  private GroupHandler groupHandler;

  public GroupCommand(Rekeverden plugin) {
    this.plugin = plugin;
    this.groupHandler = plugin.getGroupHandler();
  }

  public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("This command can only be used by Players");
      return true;
    }

    Player player = (Player) sender;

    User user = this.plugin.getUserHandler().getUser(player.getUniqueId());

    if (user.isRestricted()) return true;

    if (user.getAccessLevel() < 1) {
      player.sendMessage(ChatColor.RED + "You do not have access to use this command.");
      return true;
    }

    if (args.length == 0) {
      handleIndex(user, player);
    } else if ((args[0].equalsIgnoreCase("new")) && (args.length > 1)) {
      handleNew(user, player, args);
    } else if ((args[0].equalsIgnoreCase("invite")) && (args.length > 2)) {
      handleInvite(user, player, args);
    } else if ((args[0].equalsIgnoreCase("accept")) && (args.length > 1)) {
      handleAcceptInvite(user, player, args);
    } else if ((args[0].equalsIgnoreCase("deny")) && (args.length > 1)) {
      handleDenyInvite(user, player, args);
    } else if (args[0].equalsIgnoreCase("info")) {
      handleInfo(user, player, args);
    } else if (args[0].equalsIgnoreCase("invites")) {
      handleInvites(user, player, args);
    } else if ((args[0].equalsIgnoreCase("leave")) && (args.length > 1)) {
      handleLeave(user, player, args);
    } else if ((args[0].equalsIgnoreCase("kick")) && (args.length > 2)) {
      handleKick(user, player, args);
    } else if ((args[0].equalsIgnoreCase("group")) && (args.length > 1)) {
      handleGroupInformation(user, player, args);
    }

    return true;
  }

  private void handleIndex(User user, Player player) {
    player.sendMessage(
        ChatColor.GOLD + "--------------- " + ChatColor.BLUE + "Group System" + ChatColor.GOLD + " ---------------");
    player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr new" + ChatColor.GRAY + " [name]"
        + ChatColor.WHITE + " -- Creates a new group");
    player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr info" + ChatColor.WHITE
        + " -- Get information about your current group memberships");
    player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr invite" + ChatColor.GRAY
        + " [groupID] [player]" + ChatColor.WHITE + " -- Invite a user to a group");
    player.sendMessage(
        ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr invites" + ChatColor.WHITE + " -- List all invites");
    player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr accept" + ChatColor.GRAY + " [inviteID]"
        + ChatColor.WHITE + " -- Accept a invite");
    player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr deny" + ChatColor.GRAY + " [inviteID]"
        + ChatColor.WHITE + " -- Deny a invite");

    if (user.getGroups().size() > 0) {
      player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr group" + ChatColor.GRAY + " [groupID]"
          + ChatColor.WHITE + " -- Get information about one group.");
      player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr leave" + ChatColor.GRAY + " [groupID]"
          + ChatColor.WHITE + " -- Leave a group");

      if (user.isOwnerOfAGroup() == true) {
        player.sendMessage(ChatColor.GOLD + "/" + ChatColor.DARK_GREEN + "gr kick" + ChatColor.GRAY
            + " [groupID] [player]" + ChatColor.WHITE + " -- Kick a member of a group.");
      }
    }
  }

  private void handleNew(User user, Player player, String[] args) {
    if (args[1].length() > 20) {
      player.sendMessage(ChatColor.RED + "The name is too long. maximum is 20 characters.");
      return;
    }
    try {
      Group newGroup = this.groupHandler.createGroup(args[1], user);
      this.groupHandler.addMembership(user, newGroup);
      player.sendMessage(ChatColor.GREEN + "Created the group " + ChatColor.WHITE + args[1] + ChatColor.GREEN + "!");
    } catch (Exception e) {
      player.sendMessage(e.getMessage());
      return;
    }
  }

  private void handleInvite(User user, Player player, String[] args) {
    int toGroupID = 0;
    try {
      toGroupID = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "GroupID must be integer.");
      return;
    }

    Group toGroup = this.groupHandler.getGroup(toGroupID);
    if (toGroup == null) {
      player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED
          + " was not found in the database.");
      return;
    }
    if (!toGroup.getOwner().equals(user)) {
      player.sendMessage(ChatColor.RED + "Only owner of a group can invite new users.");
      return;
    }

    User inviteUser = this.plugin.getUserHandler().getUser(args[2]);
    if (inviteUser == null) {
      player.sendMessage(ChatColor.RED + "Requested user " + ChatColor.WHITE + args[2] + ChatColor.RED
          + " was not found in the database.");
      return;
    }
    try {
      GroupInvite gi = this.groupHandler.createInvite(toGroup, inviteUser, user);
      inviteUser.addGroupInvite(gi);

      Player playerInvited = this.plugin.getServer().getPlayer(inviteUser.getUuid());
      boolean wasOnline = false;

      if (((playerInvited instanceof Player)) && (playerInvited.isOnline())) {
        wasOnline = true;
        playerInvited.sendMessage(ChatColor.GREEN + "You have been invited to the group " + ChatColor.WHITE
            + toGroup.getName() + ChatColor.GREEN + " by " + ChatColor.WHITE + user.getName() + ChatColor.GREEN + "!");
        playerInvited.sendMessage(ChatColor.GREEN + "Write " + ChatColor.WHITE + "/gr accept " + gi.getInviteID()
            + ChatColor.GREEN + " to accept this invite.");
      }

      player.sendMessage(ChatColor.GREEN + "The user " + ChatColor.WHITE + inviteUser.getName() + ChatColor.GREEN
          + " has been invited to the group.");
      if (wasOnline)
        player.sendMessage(ChatColor.GREEN + "The user was online and has been notified.");
    } catch (Exception e) {
      player.sendMessage(e.getMessage());
      return;
    }
  }

  private void handleAcceptInvite(User user, Player player, String[] args) {
    int inviteID = 0;
    try {
      inviteID = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "Invite ID must be integer.");
      return;
    }

    GroupInvite gi = this.groupHandler.getGroupInvite(inviteID);
    if ((gi == null) || (!gi.getInvited().equals(user))) {
      player.sendMessage(ChatColor.RED + "The invite was not found.");
      return;
    }

    Group gr = gi.getToGroup();

    try {
      this.groupHandler.applyInvite(gi);
      this.groupHandler.deleteInvite(gi);
      gi = null;

      player.sendMessage(ChatColor.GREEN + "Invite has been accepted!");

      gr.broadcast(player.getName() + " just became a member of this group.");
    } catch (Exception e) {
      player.sendMessage(e.getMessage());
      return;
    }
  }

  private void handleDenyInvite(User user, Player player, String[] args) {
    int inviteID = 0;
    try {
      inviteID = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "Invite ID must be integer.");
      return;
    }

    GroupInvite gi = this.groupHandler.getGroupInvite(inviteID);
    if ((gi == null) || (!gi.getInvited().equals(user))) {
      player.sendMessage(ChatColor.RED + "The invite was not found.");
      return;
    }

    this.groupHandler.deleteInvite(gi);
    gi = null;

    player.sendMessage(ChatColor.GREEN + "Invite has been deleted!");
  }

  private void handleInfo(User user, Player player, String[] args) {
    player.sendMessage(ChatColor.BLUE + "Group memberships:");

    Set<Group> groups = user.getGroups();

    if (groups.size() > 0) {
      for (Group group : user.getGroups()) {
        player.sendMessage(ChatColor.DARK_GREEN + " Group: " + ChatColor.WHITE + group.getName());
        player.sendMessage(ChatColor.DARK_GREEN + " ID: " + ChatColor.WHITE + group.getGroupID());
        player.sendMessage(ChatColor.DARK_GREEN + " Owner: " + ChatColor.WHITE + group.getOwner().getName());
        player.sendMessage("");
      }
    } else {
      player.sendMessage(ChatColor.RED + " You are not a member of any groups.");
    }
  }

  private void handleInvites(User user, Player player, String[] args) {
    player.sendMessage(ChatColor.BLUE + "Group invites:");
    for (GroupInvite gi : user.getGroupInvites()) {
      player.sendMessage(ChatColor.DARK_GREEN + " To group: " + ChatColor.WHITE + gi.getToGroup().getName());
      player.sendMessage(ChatColor.DARK_GREEN + " InviteID: " + ChatColor.WHITE + gi.getInviteID());
      player.sendMessage(ChatColor.DARK_GREEN + " Invitee: " + ChatColor.WHITE + gi.getInvitee().getName());
      player.sendMessage("");
    }
  }

  private void handleLeave(User user, Player player, String[] args) {
    int fromGroupID = 0;
    try {
      fromGroupID = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "GroupID must be integer.");
      return;
    }

    Group fromGroup = this.groupHandler.getGroup(fromGroupID);
    if (fromGroup == null) {
      player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED
          + " was not found in the database.");
      return;
    }

    if (!user.isMemberOf(fromGroup)) {
      player.sendMessage(ChatColor.RED + "You are not a member of this group.");
      return;
    }

    if ((fromGroup.getOwner().getId() == user.getId()) && (fromGroup.getMembers().size() > 1)) {
      player.sendMessage(ChatColor.RED + "");
      return;
    }

    this.groupHandler.removeMembership(user, fromGroup);
    fromGroup.broadcast(user.getName() + " has left the group.");
    player.sendMessage(
        ChatColor.GREEN + "You have left the group " + ChatColor.WHITE + fromGroup.getName() + ChatColor.GREEN + ".");
  }

  private void handleKick(User user, Player player, String[] args) {
    int fromGroupID = 0;
    try {
      fromGroupID = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "GroupID must be integer.");
      return;
    }

    Group fromGroup = this.groupHandler.getGroup(fromGroupID);
    if (fromGroup == null) {
      player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED
          + " was not found in the database.");
      return;
    }

    if (fromGroup.getOwner().getId() != user.getId()) {
      player.sendMessage(ChatColor.RED + "You must be the owner to kick a member of the group.");
      return;
    }

    if (args[2].trim().length() < 1) {
      player.sendMessage(ChatColor.RED + "Please name the player you would like to kick out of the group.");
      player.sendMessage("/gr kick [groupID] [player]");
      return;
    }

    User toKick = this.plugin.getUserHandler().getUser(args[2].trim());
    if ((toKick == null) || (!toKick.isMemberOf(fromGroup))) {
      player.sendMessage(ChatColor.RED + "User " + ChatColor.WHITE + args[2] + ChatColor.RED
          + " was not found or is not a member of this group.");
      return;
    }

    this.groupHandler.removeMembership(toKick, fromGroup);
    fromGroup.broadcast(toKick.getName() + " was kicked out of the group.");
    player.sendMessage(ChatColor.GREEN + "You have kicked " + ChatColor.WHITE + toKick.getName() + ChatColor.GREEN
        + " from " + ChatColor.WHITE + fromGroup.getName() + ChatColor.GREEN + ".");

    Player targetPlayer = this.plugin.getServer().getPlayer(toKick.getUuid());
    if (((targetPlayer instanceof Player)) && (targetPlayer.isOnline())) {
      targetPlayer.sendMessage(ChatColor.RED + "You have been kicked from the group " + ChatColor.WHITE
          + fromGroup.getName() + ChatColor.RED + " by " + ChatColor.WHITE + user.getName() + ChatColor.RED + ".");
    }
  }

  private void handleGroupInformation(User user, Player player, String[] args) {
    int groupID = 0;
    try {
      groupID = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      player.sendMessage(ChatColor.RED + "GroupID must be integer.");
      return;
    }

    Group group = this.groupHandler.getGroup(groupID);
    if (group == null) {
      player.sendMessage(ChatColor.RED + "Requested group " + ChatColor.WHITE + args[1] + ChatColor.RED
          + " was not found in the database.");
      return;
    }

    if (!user.isMemberOf(group)) {
      player.sendMessage(ChatColor.RED + "You are not a member of this group.");
      return;
    }

    String members = "";
    for (User member : group.getMembers()) {
      members = members + member.getName() + ", ";
    }
    members = members.trim().substring(0, members.length() - 2) + ".";

    player.sendMessage(
        ChatColor.GOLD + "--------------- " + ChatColor.BLUE + "Group Info" + ChatColor.GOLD + " ---------------");
    player.sendMessage(ChatColor.DARK_GREEN + "Name: " + ChatColor.WHITE + group.getName());
    player.sendMessage(ChatColor.DARK_GREEN + "ID: " + ChatColor.WHITE + group.getGroupID());
    player.sendMessage(ChatColor.DARK_GREEN + "Owner: " + ChatColor.WHITE + group.getOwner().getName());
    player.sendMessage(ChatColor.DARK_GREEN + "Members: " + ChatColor.WHITE + members);
  }
}
