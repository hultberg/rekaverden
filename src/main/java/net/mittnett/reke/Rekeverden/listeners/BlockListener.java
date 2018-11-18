package net.mittnett.reke.Rekeverden.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.mittnett.reke.Rekeverden.Rekeverden;
import net.mittnett.reke.Rekeverden.handlers.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements org.bukkit.event.Listener {
    private UserHandler userHandler;
    private BlockProtectionHandler bpHandler;
    private BlockInfoHandler blockInfoHandler;

    public BlockListener(Rekeverden plugin) {
      this.userHandler = plugin.getUserHandler();
      this.bpHandler = plugin.getBlockProtectionHandler();
      this.blockInfoHandler = plugin.getBlockInfoHandler();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
      event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Location l = block.getLocation();
        Player player = event.getPlayer();

        User user = this.userHandler.getUser(player.getUniqueId());

        if (!user.hasBuildingRights()) {
          event.setCancelled(true);

          if (user.isGuest()) {
            this.userHandler.explainDeniedAction(player);
          }
          return;
        }

        if (block.getType() == Material.BEDROCK && !user.hasAccessLevel(User.MODERATOR)) {
          event.setCancelled(true);
          return;
        }

        if (!user.hasAccessLevel(User.MODERATOR) && (block.getType() == Material.LAVA || block.getType() == Material.WATER)) {
          event.setCancelled(true);
          return;
        }

        if (block.getType() == Material.TNT && !user.hasAccessLevel(User.MODERATOR)) {
          event.setCancelled(true);
          return;
        }

        if ((block.getType() == Material.SPONGE) && (user.getAccessLevel() > 2)) {
            player.sendMessage(ChatColor.BLUE + "--------- BlockLog ---------");

            new Thread(() -> {
              Object localObject;
              String s;

              ArrayList<String> rows = this.blockInfoHandler.getBlockLog(block.getLocation());
              if (rows.size() > 0) {
                for (localObject = rows.iterator(); ((Iterator) localObject).hasNext(); ) {
                  s = (String) ((Iterator) localObject).next();
                  player.sendMessage(s);
                }
              } else {
                player.sendMessage("(no log found on this location)");
              }

              player.sendMessage("");
            }).start();


            event.setCancelled(true);
            return;
        }

        // Validate that a user can create double chest.
        if (block.getType() == Material.CHEST) {
            // Check every direction possible for double chests.
            BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace face : faces) {
                Block relativeChest = block.getRelative(face); // Put into variable.

                // Validate that the target block is a chest.
                if ((relativeChest != null) && (relativeChest.getType() == Material.CHEST)) {
                    // Fetch owner, if it is owned and the user is not current. Cancel event.
                    User owner = this.bpHandler.getOwnerUser(relativeChest.getLocation());
                    if ((owner != null) && (owner.getId() != user.getId())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        new Thread(() -> {
          this.blockInfoHandler.log(user
            .getId(), l
            .getBlockX(), l
            .getBlockY(), l
            .getBlockZ(), l
            .getWorld().getName(), block
            .getType(), block
            .getData(), BlockAction.PLACED);
        }).start();

        if ((block.getType() != Material.DIRT) &&
                (block.getType() != Material.GRASS) &&
                (block.getType() != Material.SAND) &&
                (block.getType() != Material.GRAVEL) &&
                (block.getType() != Material.WATER) &&
                (block.getType() != Material.LAVA) &&
                (block.getType() != Material.AIR)) {
            this.bpHandler.protect(
              user.getId(),
              l.getBlockX(),
              l.getBlockY(),
              l.getBlockZ(),
              l.getWorld().getName()
            );
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Block blockOver = block.getRelative(BlockFace.UP);
        Location l = block.getLocation();
        Player player = event.getPlayer();

        User user = this.userHandler.getUser(player.getUniqueId());

        if (!user.hasBuildingRights()) {
          event.setCancelled(true);

          if (user.isGuest()) {
            this.userHandler.explainDeniedAction(player);
          }
          return;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.BOOK && user.hasEnabledSelectTool()) {
            user.setSelectToolPoint1(l);
            player.sendMessage(ChatColor.AQUA + "[SEL] Point #1 has been set/updated. (" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ")");
            event.setCancelled(true);
            return;
        }

        if (block.getType() == Material.BEDROCK && !user.hasAccessLevel(User.MODERATOR)) {
          event.setCancelled(true);
          return;
        }

        User owner = this.bpHandler.getOwnerUser(l);
        HashSet<Block> blocks = new HashSet<>();

        switch (block.getType()) {
            case WALL_SIGN:
                Sign sign = (Sign) block.getState();
                if ((sign.getLine(1).trim().contains("[private]")) && (!owner.equals(user))) {
                    event.setCancelled(true);
                    return;
                }

                break;
            case CHEST:
            case FURNACE:
                Sign privateSign = getPrivateSignOfChest(block);
                if (privateSign != null) {
                    User ownerOfSign = this.bpHandler.getOwnerUser(privateSign.getLocation());
                    if ((ownerOfSign != null) && (ownerOfSign.equals(owner)) && (!owner.equals(user))) {
                        event.setCancelled(true);
                        return;
                    }
                }
                break;
        }


        if (blockOver != null) {
            switch (blockOver.getType()) {
                case POWERED_RAIL:
                case DETECTOR_RAIL:
                case ACTIVATOR_RAIL:
                case FLOWER_POT:
                case ROSE_RED:
                case DANDELION_YELLOW:
                case DANDELION:
                    User ownerOfUp = this.bpHandler.getOwnerUser(blockOver.getLocation());
                    if ((ownerOfUp != null) && (!user.equals(ownerOfUp)) && (!user.sharesAGroup(ownerOfUp))) {
                        player.sendMessage(ChatColor.RED + "The block over this block is owned by " + ChatColor.WHITE + ownerOfUp.getName() + ChatColor.RED + ".");
                        player.sendMessage(ChatColor.RED + "If you are sharing the blocks you must be a member of the same group. See /gr for information of how");
                        player.sendMessage(ChatColor.RED + "to create a group or invite a user.");
                        event.setCancelled(true);
                        return;
                    } else if (ownerOfUp != null) {
                      blocks.add(blockOver);
                    }

                    break;
            }

        }


        if (owner != null) {
            if ((!user.equals(owner)) && (!user.sharesAGroup(owner))) {
                player.sendMessage(ChatColor.RED + "This block is owned by " + ChatColor.WHITE + owner.getName() + ChatColor.RED + ". If you are sharing the blocks");
                player.sendMessage(ChatColor.RED + "you must be a member of the same group. See /gr for information of how to create a group or invite a user.");
                event.setCancelled(true);
                return;
            }

            blocks.add(block);
        }

        if (!blocks.isEmpty()) {
          // Remove protection of this block.
          new Thread(() -> {
            for (Block blockToRemove : blocks) {
              Location locOfBlock = blockToRemove.getLocation();

              this.bpHandler.unProtect(
                locOfBlock.getBlockX(),
                locOfBlock.getBlockY(),
                locOfBlock.getBlockZ(),
                locOfBlock.getWorld().getName()
              );

              this.blockInfoHandler.log(
                user.getId(),
                locOfBlock.getBlockX(),
                locOfBlock.getBlockY(),
                locOfBlock.getBlockZ(),
                locOfBlock.getWorld().getName(),
                blockToRemove.getType(),
                blockToRemove.getData(),
                BlockAction.REMOVED
              );
            }
          }).start();
        }
    }


    private Sign getPrivateSignOfChest(Block chestBlock) {
        Sign initialSign = getSignOfChest(chestBlock);


        if (initialSign != null) {
            return initialSign;
        }


        Block relativeChest = getChestRelativeWithSign(chestBlock);
        if (relativeChest == null) {
            return null;
        }
        Sign relativeSign = getSignOfChest(relativeChest);
        if (relativeSign == null) {
            return null;
        }
        return relativeSign;
    }

    private boolean hasChestPrivateSign(Block chestBlock) {
        return getPrivateSignOfChest(chestBlock) != null;
    }

    private Sign getSignOfChest(Block originalBlock) {
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (BlockFace face : faces) {
            Block signBlock = originalBlock.getRelative(face);
            if ((signBlock != null) && (signBlock.getType() == Material.WALL_SIGN)) {
                Sign sign = (Sign) signBlock.getState();
                if (sign.getLine(1).trim().equalsIgnoreCase("[private]")) {
                    return sign;
                }
            }
        }

        return null;
    }

    private Block getChestRelativeWithSign(Block originalBlock) {
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (BlockFace face : faces) {
            Block thisBlock = originalBlock.getRelative(face);
            if ((thisBlock != null) && (thisBlock.getType() == Material.CHEST)) {
                return thisBlock;
            }
        }

        return null;
    }
}


/* Location:              /Users/edvin/Rekeverden-0.0.1-SNAPSHOT.jar!/net/mittnett/reke/Rekeverden/listeners/BlockListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
