package net.mittnett.reke.Rekeverden.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.mittnett.reke.Rekeverden.Rekeverden;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class User {
  public static final int GUEST = 0;
  public static final int BUILDER = 1;
  public static final int MODERATOR = 3;
  public static final int ADMIN = 4;

  private final int id;
  private final UUID uuid;
  private final String name;
  private int accessLevel;
  private boolean enabledSelectTool;
  private Location selectToolPoint1;
  private Location selectToolPoint2;
  private Set<Group> groups;
  private Set<GroupInvite> groupInvites;
  private ChatColor displayColor;
  private String displayPrefix;

  public User(int id, UUID uuid, String name, int accessLevel) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.accessLevel = accessLevel;
    this.enabledSelectTool = false;
    this.groups = new HashSet<>();
    this.groupInvites = new HashSet<>();

    this.setDisplayColor();
    this.setDisplayPrefix();

    this.selectToolPoint1 = null;
    this.selectToolPoint2 = null;
  }

  /**
   * Create a new user from another user object. Constructor copy strategy.
   * @param user
   */
  public User(User user) {
    this(user.getId(), user.getUuid(), user.getName(), user.getAccessLevel());
  }

  public int getId() {
    return this.id;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public String getName() {
    return this.name;
  }

  /**
   * Indicates if this user is a guest.
   *
   * @return Boolean
   */
  public Boolean isGuest() { return this.accessLevel == GUEST; }

  /**
   * Provides the access level of this user. Use the final ints in this class instead of direct integers when comparing.
   *
   * 0 => Guest
   * 1 => Builder
   * 2 => (not in use)
   * 3 => Moderator
   * 4 => Admin
   *
   * @return int
   */
  public int getAccessLevel() {
    return this.accessLevel;
  }

  /**
   * Determine if this user has the provided access.
   * @param level int
   * @return boolean
   */
  public boolean hasAccessLevel(int level) {
    return this.accessLevel >= level;
  }

  public void setAccessLevel(int level) {
    this.accessLevel = level;
    this.setDisplayColor();
    this.setDisplayPrefix();
  }

  public boolean hasEnabledSelectTool() {
    return this.enabledSelectTool;
  }

  public void setHasEnabledSelectTool(boolean newValue) {
    this.enabledSelectTool = newValue;
  }

  public Location getSelectToolPoint1() {
    return this.selectToolPoint1;
  }

  public void setSelectToolPoint1(Location selectToolPoint1) {
    this.selectToolPoint1 = selectToolPoint1;
  }

  public Location getSelectToolPoint2() {
    return this.selectToolPoint2;
  }

  public void setSelectToolPoint2(Location selectToolPoint2) {
    this.selectToolPoint2 = selectToolPoint2;
  }


  public Location getMinimumSelectedPoint() {
    return new Location(getSelectToolPoint1().getWorld(), Math.min(getSelectToolPoint1().getBlockX(), getSelectToolPoint2().getBlockX()), Math.min(getSelectToolPoint1().getBlockY(), getSelectToolPoint2().getBlockY()), Math.min(getSelectToolPoint1().getBlockZ(), getSelectToolPoint2().getBlockZ()));
  }


  public Location getMaximumSelectedPoint() {
    return new Location(getSelectToolPoint1().getWorld(), Math.max(getSelectToolPoint1().getBlockX(), getSelectToolPoint2().getBlockX()), Math.max(getSelectToolPoint1().getBlockY(), getSelectToolPoint2().getBlockY()), Math.max(getSelectToolPoint1().getBlockZ(), getSelectToolPoint2().getBlockZ()));
  }


  public int countSelection() {
    int counted = 0;

    for (int x = getMinimumSelectedPoint().getBlockX(); x <= getMaximumSelectedPoint().getBlockX(); x++) {
      for (int y = getMinimumSelectedPoint().getBlockY(); y <= getMaximumSelectedPoint().getBlockY(); y++) {
        for (int z = getMinimumSelectedPoint().getBlockZ(); z <= getMaximumSelectedPoint().getBlockZ(); z++) {
          Block block = getMinimumSelectedPoint().getWorld().getBlockAt(x, y, z);
          if ((block != null) &&
            (block.getType() != Material.AIR) &&
            (block.getType() != Material.LAVA) &&
            (block.getType() != Material.WATER)) {
            counted++;
          }
        }
      }
    }

    return counted;
  }


  public Set<Group> getGroups() {
    return this.groups;
  }


  public void addGroup(Group group) {
    this.groups.add(group);
  }


  public void removeGroup(Group group) {
    this.groups.remove(group);
  }


  public void setGroups(Set<Group> groups) {
    this.groups = groups;
  }


  public boolean isMemberOf(Group group) {
    if (this.groups.size() > 0) {
      for (Group g : this.groups) {
        if (g.equals(group)) {
          return true;
        }
      }
    }
    return false;
  }


  public boolean sharesAGroup(User user) {
    if ((this.groups.size() > 0) && (user.getGroups().size() > 0)) {
      for (Group group : this.groups) {
        if (user.isMemberOf(group) == true) {
          return true;
        }
      }
    }

    return false;
  }


  public Set<GroupInvite> getGroupInvites() {
    return this.groupInvites;
  }


  public void addGroupInvite(GroupInvite groupInvite) {
    this.groupInvites.add(groupInvite);
  }


  public void removeGroupInvite(GroupInvite groupInvite) {
    this.groupInvites.remove(groupInvite);
  }


  public void setGroupInvites(Set<GroupInvite> groupInvites) {
    this.groupInvites = groupInvites;
  }


  public boolean removeInvitesOf(Group group) {
    if (getGroupInvites().size() > 0) {
      for (GroupInvite gi : getGroupInvites()) {
        if (gi.getToGroup().getGroupID() == group.getGroupID()) {
          this.groupInvites.remove(gi);
          Rekeverden.getInstance().getGroupHandler().removeGroupInviteCache(gi);
        }
      }
    }

    return false;
  }


  public boolean hasInviteToGroup(Group group) {
    if (getGroupInvites().size() > 0) {
      for (GroupInvite gi : getGroupInvites()) {
        if (gi.getToGroup().getGroupID() == group.getGroupID()) {
          return true;
        }
      }
    }
    return false;
  }


  public boolean isOwnerOfAGroup() {
    if (this.groups.size() > 0) {
      for (Group g : this.groups) {
        if (g.getOwner().getId() == getId()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean equals(User user) {
    return user != null && user.getId() == this.getId();
  }

  private void setDisplayColor() {
    ChatColor color;

    switch (this.accessLevel) {
      case GUEST:
        color = ChatColor.GRAY;
        break;

      case BUILDER:
        color = ChatColor.WHITE;
        break;

      case MODERATOR:
        color = ChatColor.BLUE;
        break;

      case ADMIN:
        color = ChatColor.GOLD;
        break;

      default:
        color = ChatColor.WHITE;
        break;
    }

    this.displayColor = color;
  }

  private void setDisplayPrefix() {
    String prefix = "";

    switch (this.accessLevel) {
      case GUEST:
        prefix = "Guest";
        break;
      case BUILDER:
        break;
      case MODERATOR:
        prefix = "Mod";
        break;
      case ADMIN:
        prefix = "Adm";
    }

    this.displayPrefix = prefix;
  }

  public ChatColor getDisplayColor() {
    return this.displayColor;
  }

  public String getDisplayPrefix() {
    return this.displayPrefix;
  }

  public String getDisplayPrefixName() {
    String prefix = "";

    switch (this.accessLevel) {
      case GUEST:
        prefix = "Guest";
        break;
      case BUILDER:
        prefix = "Builder";
        break;
      case MODERATOR:
        prefix = "Mod";
        break;
      case ADMIN:
        prefix = "Adm";
    }

    return prefix;
  }

  public String getDisplayName() {
    return this.displayColor + (this.displayPrefix.length() > 0 ? "[" + this.displayPrefix + "] " : "") + this.name + ChatColor.RESET;
  }
}
