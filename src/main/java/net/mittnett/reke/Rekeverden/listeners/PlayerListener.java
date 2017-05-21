package net.mittnett.reke.Rekeverden.listeners;

import net.mittnett.reke.Rekeverden.Rekeverden;
import net.mittnett.reke.Rekeverden.handlers.BlockAction;
import net.mittnett.reke.Rekeverden.handlers.User;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static org.bukkit.Material.*;

public class PlayerListener implements org.bukkit.event.Listener {
    public Rekeverden plugin;

    public PlayerListener(Rekeverden instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();


        this.plugin.getUserHandler().loginPlayer(pl);
        this.plugin.getUserHandler().setDisplayName(pl);


        User user = this.plugin.getUserHandler().getUser(pl.getUniqueId());


        event.setJoinMessage(null);

        pl.sendMessage(ChatColor.GRAY + "|| " + ChatColor.GOLD + "---------- WELCOME TO Rekeverden");
        pl.sendMessage(ChatColor.GRAY + "|| " + ChatColor.RESET + "Dynmap: http://mc.rekalarsen.no:8123/");


        if (user.getGroupInvites().size() > 0) {
            pl.sendMessage(ChatColor.GRAY + "|| " + ChatColor.DARK_GREEN + "You have " + ChatColor.WHITE + user.getGroupInvites().size() + ChatColor.DARK_GREEN + " group invites pending.");
        }

        this.plugin.getServer().broadcastMessage(ChatColor.GRAY + "// " + event.getPlayer().getDisplayName() + ChatColor.GREEN + " joined the game.");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);


        if (event.getPlayer() != null) {
            this.plugin.getUserHandler().logoutPlayer(event.getPlayer());
        }

        this.plugin.getServer().broadcastMessage(ChatColor.GRAY + "// " + event.getPlayer().getDisplayName() + ChatColor.RED + " left the game.");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch (event.getHand()) {
            case HAND:
                this.handInteractEvent(event);
                break;
        }
    }

    private void handInteractEvent(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                this.handLeftClickBlock(event);
                break;

            case RIGHT_CLICK_BLOCK:
                this.handleRightClickBlock(event);
                break;
        }
    }

    private void handleRightClickBlock(PlayerInteractEvent event) {

        if (event.getClickedBlock() != null) {
            Player player = event.getPlayer();
            Block clickedBlock = event.getClickedBlock();
            User user = this.plugin.getUserHandler().getUser(player.getUniqueId());

            switch (clickedBlock.getType()) {
                case CHEST:
                case FURNACE:
                case BURNING_FURNACE:
                    User owner = this.plugin.getBlockProtectionHandler().getOwnerUser(clickedBlock.getLocation());
                    if (owner == null) {
                        player.sendMessage(ChatColor.RED + "This chest/furnace does not have an owner, please contact an admin!");
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    } else {
                        Sign privateSign = getPrivateSignOfChest(clickedBlock);
                        if (privateSign != null) {
                            User ownerOfSign = this.plugin.getBlockProtectionHandler().getOwnerUser(privateSign.getLocation());
                            if ((ownerOfSign != null) && (ownerOfSign.equals(owner)) && (!owner.equals(user))) {
                                player.sendMessage(ChatColor.RED + "This chest/furnace is private, you can not open it.");
                                event.setCancelled(true);
                                event.setUseInteractedBlock(Event.Result.DENY);
                            }
                        } else if ((user.equals(owner) == false) && (owner.sharesAGroup(user) == false)) {
                            player.sendMessage(ChatColor.RED + "User " + ChatColor.WHITE + owner.getName() + ChatColor.RED + " owns this chest/furnance.");
                            event.setCancelled(true);
                            event.setUseInteractedBlock(Event.Result.DENY);
                        }
                    }

                    return;
                    
                default:
                	return;
            }
        }
        
        if (event.getItem() != null) {
            Player player = event.getPlayer();
            Block clickedBlock = event.getClickedBlock();
            User user = this.plugin.getUserHandler().getUser(player.getUniqueId());
            
	        switch (event.getItem().getType()) {

	            case BOOK:
	                if (user.hasEnabledSelectTool()) {
	                    Location l = clickedBlock.getLocation();
	                    user.setSelectToolPoint2(l);
	
	                    String msg = ChatColor.AQUA + "[SEL] Point #2 has been set/updated (" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ")";
	                    if (user.getSelectToolPoint1() != null) {
	                        msg = msg + "(" + user.countSelection() + " blocks)";
	                    }
	                    msg = msg + ".";
	
	                    player.sendMessage(msg);
	                }
	                return;
	
	            case WATCH:
	                if (user.getAccessLevel() > 2) {
	                    player.sendMessage(ChatColor.BLUE + "--------- BlockBP ---------");
	
	                    ArrayList<String> rows = this.plugin.getBlockInfoHandler().getBlockLog(clickedBlock.getLocation());
	                    if (rows.size() > 0) {
	                        for (String s : rows) {
	                            player.sendMessage(s);
	                        }
	                    } else {
	                        player.sendMessage("(no log found on this location)");
	                    }
	
	                    User owner = this.plugin.getBlockProtectionHandler().getOwnerUser(clickedBlock.getLocation());
	                    player.sendMessage(ChatColor.WHITE + "Owned by: " + ChatColor.BLUE + (owner != null ? owner.getName() : "no owner"));
	
	                    player.sendMessage("");
	                }
	                return;
	
	            case STICK:
	                if (user.getAccessLevel() > 2) {
	                    Location l = clickedBlock.getLocation();
	                    this.plugin.getBlockProtectionHandler().unProtect(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
	                    this.plugin.getBlockInfoHandler().log(user, l, clickedBlock, BlockAction.ADMIN_STICKED);
	                    clickedBlock.setType(Material.AIR);
	                }
	                return;
	               
	            default:
	            	return;

	        }
        }
    }

    private void handLeftClickBlock(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            switch (event.getItem().getType()) {
                case DIAMOND_PICKAXE:
                case DIAMOND_AXE:
                case DIAMOND_HOE:
                case DIAMOND_SPADE:
                case GOLD_PICKAXE:
                case GOLD_AXE:
                case GOLD_SPADE:
                case GOLD_HOE:
                case IRON_PICKAXE:
                case IRON_AXE:
                case IRON_SPADE:
                case IRON_HOE:
                case STONE_PICKAXE:
                case STONE_AXE:
                case STONE_SPADE:
                case STONE_HOE:
                case WOOD_PICKAXE:
                case WOOD_AXE:
                case WOOD_SPADE:
                case WOOD_HOE:
                    event.getItem().setDurability((short) 65336);
                    return;

                case BOOK:
                    Player player = event.getPlayer();
                    Block clickedBlock = event.getClickedBlock();
                    User user = this.plugin.getUserHandler().getUser(player.getUniqueId());

                    if ((player.getGameMode() != GameMode.CREATIVE) && (user.hasEnabledSelectTool())) {
                        user.setSelectToolPoint1(clickedBlock.getLocation());
                        player.sendMessage(ChatColor.AQUA + "[SEL] Point #1 has been set/updated. (" + clickedBlock.getLocation().getBlockX() + "," + " " + clickedBlock
                                .getLocation().getBlockY() + ", " + clickedBlock.getLocation().getBlockZ() + ")");
                        event.setCancelled(true);
                    }
                    return;
                    
				default:
					return;
            }
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
