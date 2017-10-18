package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.Rekeverden;

import net.mittnett.reke.Rekeverden.mysql.SQLSelectResult;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;

public class UserHandler implements Handler {
  private final Rekeverden plugin;
  private Set<User> onlineUsers;
  private Set<User> localCachedUsers;

  public UserHandler(Rekeverden plugin) {
    this.plugin = plugin;
    this.onlineUsers = new HashSet<>();
    this.localCachedUsers = new HashSet<>();
  }

  public interface UserComparator {
    boolean equals(User user, Object value);
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

  public void explainDeniedAction(Player player) {
    player.sendMessage(ChatColor.RED + "You are not allowed to build or do anything as a guest.");
    player.sendMessage(ChatColor.RED + "Please contact and mod/admin to be registered.");
  }

  public User loginPlayer(Player p) {
    User user;

    if (userExists(p.getUniqueId())) {
      user = getUser(p.getUniqueId(), true);
    } else {
      user = createUser(p.getUniqueId(), p.getName(), User.GUEST);
    }

    this.updatePlayerCanPickUp(user);
    this.setPermissions(p);
    this.setDisplayName(p);
    this.onlineUsers.add(user);

    return user;
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

  /**
   * Update the provided user fields in database.
   *
   * This will update:
   * 1. nick if it has changed on
   *
   * If anything has changed, the runtime cached User object is refreshed.
   *
   * @param user User
   * @return boolean
   */
  public boolean updateUser(final User newState) {
    boolean result = false;

    // Get the current state of the user.
    User previousState = getUser(newState.getId());

    HashMap<String, Object> differences = new HashMap<>();

    if (!previousState.getName().equals(newState.getName())) {
      differences.put("nick", newState.getName());
    }

    if (!previousState.getUuid().toString().equals(newState.getUuid().toString())) {
      differences.put("uuid", newState.getUuid().toString());
    }

    if (previousState.getAccessLevel() != newState.getAccessLevel()) {
      differences.put("access", newState.getAccessLevel());
    }

    if (previousState.isRestricted() != newState.isRestricted()) {
      differences.put("restricted", newState.isRestricted() ? 1 : 0);
    }

    if (differences.isEmpty()) return false;

    StringBuilder query = new StringBuilder();
    query.append("UPDATE r_users SET ");

    Iterator keyIterator = differences.keySet().iterator();
    while (keyIterator.hasNext()) {
      query.append("`").append(keyIterator.next()).append("` = ?");

      if (keyIterator.hasNext()) query.append(", ");
      else query.append(" ");
    }

    query.append("WHERE `uid` = ?");

    try {
      PreparedStatement statement = this.plugin.getConnection().prepareStatement(query.toString());

      this.plugin.getLogger().log(Level.INFO, query.toString());

      int index = 1;
      for (Object value : differences.values()) {
        if (value instanceof String) {
          statement.setString(index, value.toString());
        } else if (value instanceof Integer) {
          statement.setInt(index, (int) value);
        } else if (value instanceof Boolean) {
          statement.setBoolean(index, (boolean) value);
        }

        index += 1;
      }

      statement.setInt(index, newState.getId());

      result = this.plugin.getMySQLHandler().update(statement);
    } catch (SQLException e) {
      this.plugin.getLogger().log(Level.SEVERE, "Failed to update a user!", e);
    }

    Predicate<? super User> predicate = new Predicate<User>() {
      @Override
      public boolean test(User user) {
        return user.getId() == newState.getId();
      }
    };

    this.onlineUsers.removeIf(predicate);
    this.localCachedUsers.removeIf(predicate);
    this.onlineUsers.add(newState);
    this.localCachedUsers.add(newState);

    return result;
  }

  public User getUser(int id) {
    return getUser(id, false);
  }

  public User getUser(int id, boolean refreshCache) {
    if (!refreshCache) {
      User foundCached = this.getCachedUser(id);

      if (foundCached != null) return foundCached;
    }

    User user = this.getUserFromDatabase(id, "uid");
    if (user == null) return null;

    this.localCachedUsers.add(user);
    user.setGroupInvites(this.plugin.getGroupHandler().getGroupInvitesOfUser(user));
    this.fillGroupMemberships(user);
    this.localCachedUsers.add(user);


    return user;
  }

  public User getUser(String nick) {
    return getUser(nick, false);
  }

  public User getUser(String nick, boolean refreshCache) {
    if (!refreshCache) {
      User foundCached = this.getCachedUser(nick);

      if (foundCached != null) return foundCached;
    }

    User user = this.getUserFromDatabase(nick, "nick");
    if (user == null) return null;

    this.localCachedUsers.add(user);
    user.setGroupInvites(this.plugin.getGroupHandler().getGroupInvitesOfUser(user));
    this.fillGroupMemberships(user);
    this.localCachedUsers.add(user);

    return user;
  }

  public User getUser(UUID uuid) {
    return getUser(uuid, false);
  }

  public User getUser(UUID uuid, boolean refreshCache) {
    if (!refreshCache) {
      User foundCached = this.getCachedUser(uuid);

      if (foundCached != null) return foundCached;
    }

    User user = this.getUserFromDatabase(uuid, "uuid");
    if (user == null) return null;

    this.localCachedUsers.add(user);
    user.setGroupInvites(this.plugin.getGroupHandler().getGroupInvitesOfUser(user));
    this.fillGroupMemberships(user);
    this.localCachedUsers.add(user);

    return user;
  }

  /**
   * Provides a user from the database.
   *
   * @param value
   * @param column
   * @return User
   */
  private User getUserFromDatabase(Object value, String column) {
    User user = null;

    try {
      PreparedStatement ps = this.plugin.getConnection().prepareStatement(
        "SELECT `uid`, `uuid`, `nick`, `access`, `restricted`, `groups` FROM `r_users` WHERE `" + column + "` = ?"
      );

      if (value instanceof String) {
        ps.setString(1, value.toString());
      } else if (value instanceof UUID) {
        ps.setString(1, value.toString());
      } else if (value instanceof Integer) {
        ps.setInt(1, (int) value);
      }

      SQLSelectResult result = this.plugin.getMySQLHandler().select(ps);

      while (result.getResultSet().next()) {
        user = new User(
          result.getResultSet().getInt(1),
          UUID.fromString(result.getResultSet().getString(2)),
          result.getResultSet().getString(3),
          result.getResultSet().getInt(4)
        );

        user.setRestricted(result.getResultSet().getInt(5) == 1);
      }

      result.close();
    } catch (SQLException ex) {
      this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception", ex);
    }

    return user;
  }

  private User getCachedUser(String nick) {
    return this.getCachedUser(nick, new UserComparator() {
      @Override
      public boolean equals(User user, Object value) {
        return user.getUuid().toString().equals(value.toString());
      }
    });
  }

  private User getCachedUser(UUID uuid) {
    return this.getCachedUser(uuid, new UserComparator() {
      @Override
      public boolean equals(User user, Object value) {
        return user.getUuid().toString().equals(value.toString());
      }
    });
  }

  private User getCachedUser(int id) {
    return this.getCachedUser(id, new UserComparator() {
      @Override
      public boolean equals(User user, Object value) {
        return user.getId() == (int) value;
      }
    });
  }

  private User getCachedUser(Object value, UserComparator comparator) {
    if (this.onlineUsers.size() > 0) {
      Iterator onlineUsers = this.localCachedUsers.iterator();

      while (onlineUsers.hasNext()) {
        User user = (User) onlineUsers.next();

        if (comparator.equals(user, value)) {
          return user;
        }
      }
    }

    if (this.localCachedUsers.size() > 0) {
      Iterator cachedUsers = this.localCachedUsers.iterator();

      while (cachedUsers.hasNext()) {
        User user = (User) cachedUsers.next();

        if (comparator.equals(user, value)) {
          return user;
        }
      }
    }

    return null;
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

    ChatColor color = user.getDisplayColor();
    String prefix = user.getDisplayPrefix();

    player.setDisplayName(user.getDisplayName());

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

  public boolean changeStatus(User user, int newStatus) {
    if (user.getAccessLevel() == newStatus) return false;

    User newUser = new User(user);
    newUser.setAccessLevel(newStatus);

    return this.updateUser(newUser);
  }

  public void updatePlayerCanPickUp(User user) {
    Player player = this.plugin.getServer().getPlayer(user.getUuid());
    if (player != null) {
      player.setCanPickupItems(user.isAllowedInteraction());
    }
  }

  public void setPermissions(Player player) {
    PermissionAttachment perms = player.addAttachment(this.plugin);

    User user = this.getUser(player.getUniqueId());

    this.setModPermissions(perms, user.hasAccessLevel(User.MODERATOR));
    this.setAdminPermissions(perms, user.hasAccessLevel(User.ADMIN));
  }

  private void setModPermissions(PermissionAttachment perms, boolean value) {
    perms.setPermission("minecraft.command.ban", value);
    perms.setPermission("minecraft.command.banlist", value);
    perms.setPermission("minecraft.command.kick", value);
    perms.setPermission("minecraft.command.pardon", value);
  }

  private void setAdminPermissions(PermissionAttachment perms, boolean value) {
    perms.setPermission("worldedit.*", value);
  }

  public void alertMods(String message) {
    for (User user : this.onlineUsers) {
      Player p = this.plugin.getServer().getPlayer(user.getUuid());

      if (p != null && user.hasAccessLevel(User.MODERATOR)) {
        p.sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "[" + message + "]");
      }
    }
  }

  public void alertAdmins(String message) {
    for (User user : this.onlineUsers) {
      Player p = this.plugin.getServer().getPlayer(user.getUuid());

      if (p != null && user.hasAccessLevel(User.ADMIN)) {
        p.sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "[" + message + "]");
      }
    }
  }

  public void alertRestricting(Player target, boolean restricted) {
    if (restricted) {
      target.sendMessage(ChatColor.RED + "You have are restricted, building and interaction rights have been revoked.");
    } else {
      target.sendMessage(ChatColor.GREEN + "You are no longer restricted, building and interaction rights have been restored.");
    }
  }
}


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/handlers/UserHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
