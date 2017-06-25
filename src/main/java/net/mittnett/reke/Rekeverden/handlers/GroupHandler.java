package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.Rekeverden;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupHandler implements Handler {
    private DatabaseHandler databaseHandler;
    private UserHandler userHandler;
    private Logger logger;
    private Set<Group> groupCache;
    private Set<GroupInvite> groupInviteCache;

    public GroupHandler(DatabaseHandler databaseHandler, UserHandler userHandler, Logger logger) {
        this.databaseHandler = databaseHandler;
        this.userHandler = userHandler;
        this.logger = logger;
        this.groupCache = new java.util.HashSet();
        this.groupInviteCache = new java.util.HashSet();
    }

    public void onEnable() {
    }

    public void onDisable() {
        this.groupCache.clear();
        this.groupInviteCache.clear();
    }


    public void removeGroupInviteCache(GroupInvite gi) {
        this.groupInviteCache.remove(gi);
    }

    public void removeGroupCache(Group g) {
        this.groupCache.remove(g);
    }

    public Group getGroup(int gID) {
        return getGroup(gID, false);
    }

    public Group getGroup(int gID, boolean refreshCache) {
        if ((!refreshCache) && (this.groupCache.size() > 0)) {
            for (Group group : this.groupCache) {
                if (group.getGroupID() == gID) {
                    return group;
                }
            }
        }

        Connection conn = this.databaseHandler.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Group group = null;
        try {
            ps = conn.prepareStatement("SELECT `group_ID`, `name`, `owner` FROM `r_groups` WHERE `group_ID` = ?");
            ps.setInt(1, gID);

            rs = ps.executeQuery();
            while (rs.next()) {
                group = new Group(rs.getInt(1), rs.getString(2), this.plugin.getUserHandler().getUser(rs.getInt(3)));
            }

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        } catch (SQLException e) {
            this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while getting a group.", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }

        // Only add to cache when it is not null.
        if (group != null) {
            this.groupCache.add(group);
            fillMembersGroup(group);
            this.groupCache.add(group);
        }

        return group;
    }

    private void fillMembersGroup(Group group) {
        Connection conn = this.databaseHandler.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT `user_ID` FROM `r_group_membership` WHERE `group_ID` = ?");
            ps.setInt(1, group.getGroupID());

            rs = ps.executeQuery();
            while (rs.next())
                group.addMember(this.userHandler.getUser(rs.getInt(1)));
        } catch (SQLException e) {
            this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception while getting group memberships.", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                this.logger.log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }
    }


    public Group createGroup(String name, User creator) throws Exception {
        if (name.trim().length() < 1) {
            throw new Exception("Name is required to create a group.");
        }

        PreparedStatement ps = this.databaseHandler.getConnection().prepareStatement("INSERT INTO `r_groups` (`name`, `owner`) VALUES ('?', ?)");
        ps.setString(1, name);
        ps.setInt(2, creator.getId());

        int newGroupID = this.databaseHandler.insert(ps);
        if (newGroupID < 1) {
            throw new Exception("Failed to create the group, please contact developer!");
        }

        return getGroup(newGroupID);
    }


    public void cleanupGroup(Group group) {
        if (group.getMembers().size() > 0) {
            for (User member : group.getMembers()) {
                group.removeMember(member);
                member.removeGroup(group);
            }
        }

        PreparedStatement ps1;
        PreparedStatement ps2;

        try {
          ps1 = this.databaseHandler.getConnection().prepareStatement("DELETE FROM `r_group_membership` WHERE `group_ID` = ?");
          ps1.setInt(1, group.getGroupID());
          this.databaseHandler.update(ps1);

          ps2 = this.databaseHandler.getConnection().prepareStatement("DELETE FROM `r_group_invitations` WHERE `to_group` = ?");
          ps2.setInt(1, group.getGroupID());
          this.databaseHandler.update(ps2);
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
          try {
            if (ps1 != null) ps1.close();
            if (ps2 != null) ps2.close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }


    }


    public void deleteGroup(Group group) {

      this.databaseHandler.update(
        this.databaseHandler.getConnection().prepareStatement("DELETE FROM `r_group_invitations` WHERE `to_group` = ?")
      );
        this.plugin.getMySQLHandler().update("DELETE FROM `r_groups` WHERE `group_ID` = " + group.getGroupID());


        this.groupCache.remove(group);


        group = null;
    }


    public Set<GroupInvite> getGroupInvitesOfUser(User user) {
        HashSet<GroupInvite> invites = new HashSet<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `invite_ID` FROM `r_group_invitations` WHERE `invited` = ?");
            ps.setInt(1, user.getId());

            rs = ps.executeQuery();
            while (rs.next()) {
                GroupInvite gi = getGroupInvite(rs.getInt(1));

                invites.add(gi);
                this.groupInviteCache.add(gi);
            }
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while getting group invites of a user.", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }


        return invites;
    }


    public void addMembership(User member, Group group) {
        this.plugin.getMySQLHandler().insert("INSERT INTO `r_group_membership`(`user_ID`, `group_ID`)VALUES(" + member.getId() + ", " + group.getGroupID() + ")");


        getGroup(group.getGroupID()).addMember(member);
        this.plugin.getUserHandler().getUser(member.getId()).addGroup(group);


        removeGroupCache(group);
        this.plugin.getUserHandler().clearUserCached(member);
    }


    public void removeMembership(User member, Group group) {
        this.plugin.getMySQLHandler().update("DELETE FROM `r_group_membership` WHERE `user_ID`=" + member.getId() + " AND `group_ID`=" + group.getGroupID() + "");


        getGroup(group.getGroupID()).removeMember(member);
        this.plugin.getUserHandler().getUser(member.getId()).removeGroup(group);


        removeGroupCache(group);
        this.plugin.getUserHandler().clearUserCached(member);
    }


    public GroupInvite getGroupInvite(int inviteID) {
        return getGroupInvite(inviteID, false);
    }


    public GroupInvite getGroupInvite(int inviteID, boolean refreshCache) {
        if ((!refreshCache) && (this.groupInviteCache.size() > 0)) {
            for (GroupInvite groupInvite : this.groupInviteCache) {
                if (groupInvite.getInviteID() == inviteID) {
                    return groupInvite;
                }
            }
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GroupInvite groupInvite = null;
        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `invite_ID`, `invited`, `invitee`, `to_group` FROM `r_group_invitations` WHERE `invite_ID` = ?");
            ps.setInt(1, inviteID);

            rs = ps.executeQuery();
            while (rs.next()) {


                groupInvite = new GroupInvite(rs.getInt(1), this.plugin.getUserHandler().getUser(rs.getInt(2)), this.plugin.getUserHandler().getUser(rs.getInt(3)), getGroup(rs.getInt(4)));
            }


            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while getting a group.", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }


        if (groupInvite != null) {
            this.groupInviteCache.add(groupInvite);
        }


        return groupInvite;
    }


    public GroupInvite createInvite(Group toGroup, User inviteUser, User invitee)
            throws Exception {
        if (inviteUser.hasInviteToGroup(toGroup) == true) {
            throw new Exception(ChatColor.RED + "The user " + ChatColor.WHITE + inviteUser.getName() + ChatColor.RED + " has already been invited to this group.");
        }

        int inviteID = this.plugin.getMySQLHandler().insert("INSERT INTO `r_group_invitations`(`invited`,`invitee`,`to_group`)VALUES(" + inviteUser.getId() + ", " + invitee.getId() + ", " + toGroup.getGroupID() + ")");
        if (inviteID < 1) {
            throw new Exception(ChatColor.RED + "Failed to create the group, please contact developer!");
        }

        return getGroupInvite(inviteID);
    }


    public void applyInvite(GroupInvite groupInvite)
            throws Exception {
        if (groupInvite.getInvited().isMemberOf(groupInvite.getToGroup()) == true) {
            throw new Exception(ChatColor.RED + "The user " + ChatColor.WHITE + groupInvite.getInvited().getName() + ChatColor.RED + " is already a member of this group.");
        }

        addMembership(groupInvite.getInvited(), groupInvite.getToGroup());


        this.groupCache.remove(groupInvite.getToGroup());
        this.plugin.getUserHandler().clearUserCached(groupInvite.getInvited());
    }


    public void deleteInvite(GroupInvite groupInvite) {
        this.groupInviteCache.remove(groupInvite);
        groupInvite.getInvited().removeGroupInvite(groupInvite);
        this.plugin.getUserHandler().clearUserCached(groupInvite.getInvited());
        this.plugin.getMySQLHandler().update("DELETE FROM `r_group_invitations` WHERE `invite_ID` = " + groupInvite.getInviteID());
        groupInvite = null;
    }
}
