package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.Rekeverden;
import net.mittnett.reke.Rekeverden.handlers.AccessLevel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class UserHandler implements Handler {
  private final Rekeverden plugin;
  private Set<User> onlineUsers;
  private Set<User> localCachedUsers;

  public UserHandler(Rekeverden plugin) {
    this.plugin = plugin;
    this.onlineUsers = new java.util.HashSet();
    this.localCachedUsers = new java.util.HashSet();
  }


  public void onEnable() {
    Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getMainScoreboard();


    for (Iterator localIterator1 = scoreboard.getTeams().iterator(); localIterator1.hasNext(); ) {
      Team team = (Team) localIterator1.next();

      for (org.bukkit.OfflinePlayer player : team.getPlayers()) {
        team.removePlayer(player);
      }
    }
  }

  public void onDisable() {
    this.onlineUsers.clear();
    this.localCachedUsers.clear();
  }


  public void clearUserCached(User user) {
    this.onlineUsers.remove(user);
    this.localCachedUsers.remove(user);
  }


  public void loginPlayer(Player p) {
    User user = null;

    if (userExists(p.getUniqueId())) {
      user = getUser(p.getUniqueId(), true);
    } else {
      user = createUser(p.getUniqueId(), p.getName(), 1);
    }

    this.onlineUsers.add(user);
    updateUser(user, p.getUniqueId(), p.getName(), user.getAccessLevel());
  }


  public void logoutPlayer(Player p) {
    User user = getUser(p.getUniqueId());
    if (user != null) {
      this.onlineUsers.remove(user);
    }
  }


  public boolean userExists(UUID uuid) {
    return this.plugin.getMySQLHandler().getColumn("SELECT `uid` FROM `r_users` WHERE `uuid`='" + uuid.toString() + "'") != null;
  }


  public User createUser(UUID uuid, String nick, int accessLevel) {
    int newUserId = this.plugin.getMySQLHandler().insert("INSERT INTO `r_users`(`nick`,`uuid`,`access`,`groups`)VALUES('" + nick + "', '" + uuid.toString() + "', " + accessLevel + ", '')");


    if (newUserId < 1) {
      return null;
    }

    return new User(newUserId, uuid, nick, accessLevel);
  }


  public boolean updateUser(User user, UUID uuid, String nick, int accessLevel) {
    String query = null;


    if ((!nick.equalsIgnoreCase(user.getName())) || (!uuid.equals(user.getUuid())) || (accessLevel != user.getAccessLevel())) {
      query = "UPDATE `r_users` SET";
    }


    if (!nick.equalsIgnoreCase(user.getName())) {
      query = query + " `nick` = '" + nick + "'";
    }


    if (!uuid.equals(user.getUuid())) {
      query = query + " `uuid` = '" + uuid.toString() + "'";
    }


    if (accessLevel != user.getAccessLevel()) {
      query = query + " `access` = " + accessLevel;
    }


    if (query != null) {
      query = query + " WHERE `uid` = " + user.getId();
      if (!this.plugin.getMySQLHandler().update(query)) {
        this.plugin.getLogger().log(Level.SEVERE, "Failed to update a user!", new SQLException("Failed to update a user!"));
        return false;
      }


      this.onlineUsers.remove(user);
      this.onlineUsers.add(getUser(uuid, true));
    }

    return true;
  }


  public User getUser(int id) {
    return getUser(id, false);
  }


  public User getUser(int id, boolean refreshCache) {
    User user = null;


    if ((!refreshCache) && (this.onlineUsers.size() > 0)) {
      for (User thisUser : this.onlineUsers) {
        if (thisUser.getId() == id) {
          return thisUser;
        }
      }
    }

    if ((!refreshCache) && (this.localCachedUsers.size() > 0)) {
      for (User thisUser : this.localCachedUsers) {
        if (thisUser.getId() == id) {
          return thisUser;
        }
      }
    }
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      conn = this.plugin.getConnection();
      ps = conn.prepareStatement("SELECT `uid`, `uuid`, `nick`, `access`, `groups` FROM `r_users` WHERE `uid` = ?");
      ps.setInt(1, id);

      rs = ps.executeQuery();
      while (rs.next()) {
        user = new User(rs.getInt(1), UUID.fromString(rs.getString(2)), rs.getString(3), rs.getInt(4));
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


      if (user == null) {
        return user;
      }
    } catch (SQLException e) {
      this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while getting a user.", e);
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


    this.localCachedUsers.add(user);
    user.setGroupInvites(this.plugin.getGroupHandler().getGroupInvitesOfUser(user));
    fillGroupMemberships(user);
    this.localCachedUsers.add(user);


    return user;
  }


  public User getUser(String nick) {
    return getUser(nick, false);
  }


  public User getUser(String nick, boolean refreshCache) {
    User user = null;


    if ((!refreshCache) && (this.onlineUsers.size() > 0)) {
      for (User thisUser : this.onlineUsers) {
        if (thisUser.getName().equalsIgnoreCase(nick)) {
          return thisUser;
        }
      }
    }

    if ((!refreshCache) && (this.localCachedUsers.size() > 0)) {
      for (User thisUser : this.localCachedUsers) {
        if (thisUser.getName().equalsIgnoreCase(nick)) {
          return thisUser;
        }
      }
    }
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      conn = this.plugin.getConnection();
      ps = conn.prepareStatement("SELECT `uid`, `uuid`, `nick`, `access`, `groups` FROM `r_users` WHERE `nick` = ?");
      ps.setString(1, nick);

      rs = ps.executeQuery();
      while (rs.next()) {
        user = new User(rs.getInt(1), UUID.fromString(rs.getString(2)), rs.getString(3), rs.getInt(4));
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
      this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while getting a user.", e);
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

    if (user != null) {
      this.localCachedUsers.add(user);
      user.setGroupInvites(this.plugin.getGroupHandler().getGroupInvitesOfUser(user));
      fillGroupMemberships(user);
      this.localCachedUsers.add(user);
    }

    return user;
  }


  public User getUser(UUID uuid) {
    return getUser(uuid, false);
  }


  public User getUser(UUID uuid, boolean refreshCache) {
    User user = null;


    if ((!refreshCache) && (this.onlineUsers.size() > 0)) {
      for (User thisUser : this.onlineUsers) {
        if (thisUser.getUuid().equals(uuid)) {
          return thisUser;
        }
      }
    }

    if ((!refreshCache) && (this.localCachedUsers.size() > 0)) {
      for (User thisUser : this.localCachedUsers) {
        if (thisUser.getUuid().equals(uuid)) {
          return thisUser;
        }
      }
    }
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      conn = this.plugin.getConnection();
      ps = conn.prepareStatement("SELECT `uid`, `uuid`, `nick`, `access`, `groups` FROM `r_users` WHERE `uuid` = ?");
      ps.setString(1, uuid.toString());

      rs = ps.executeQuery();
      while (rs.next()) {
        user = new User(rs.getInt(1), UUID.fromString(rs.getString(2)), rs.getString(3), rs.getInt(4));
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


      if (user == null) {
        return user;
      }
    } catch (SQLException e) {
      this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while getting a user.", e);
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


    this.localCachedUsers.add(user);
    user.setGroupInvites(this.plugin.getGroupHandler().getGroupInvitesOfUser(user));
    fillGroupMemberships(user);
    this.localCachedUsers.add(user);


    return user;
  }

  private void fillGroupMemberships(User user) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = this.plugin.getConnection();
      ps = conn.prepareStatement("SELECT `group_ID` FROM `r_group_membership` WHERE `user_ID` = ?");
      ps.setInt(1, user.getId());

      rs = ps.executeQuery();
      while (rs.next())
        user.addGroup(this.plugin.getGroupHandler().getGroup(rs.getInt(1)));
      return;
    } catch (SQLException e) {
      this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while getting group memberships.", e);
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
  }

  public void setDisplayName(Player player) {
    User user = getUser(player.getUniqueId());

    if (user == null) {
      return;
    }
    ChatColor color = null;
    String prefix = "";

    switch (user.getAccessLevel()) {
      case 0:
        color = ChatColor.GRAY;
        prefix = "";
        break;
      case 1:
        color = ChatColor.WHITE;
        break;
      case 3:
        color = ChatColor.GREEN;
        prefix = "";
        break;
      case 4:
        color = ChatColor.RED;
        prefix = "";
    }

    player.setDisplayName((color != null ? color : "") + (prefix.length() > 0 ? "[" + prefix + "] " : "") + player.getName() + ChatColor.RESET);

    player.setPlayerListName(color + (player.getName().length() > 14 ? player.getName().substring(0, 14) : player.getName()));

    Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getMainScoreboard();

    Team team = scoreboard.getPlayerTeam(player);

    if (team != null) {
      team.removePlayer(player);
    }

    if (prefix.length() > 0) {
      team = scoreboard.getTeam(prefix);

      if (team == null) {
        team = scoreboard.registerNewTeam(prefix);
        team.setPrefix(color + "[" + prefix + "] ");
      }

      team.addPlayer(player);
    }
  }
}


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/handlers/UserHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
