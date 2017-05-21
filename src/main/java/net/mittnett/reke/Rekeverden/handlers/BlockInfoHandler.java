package net.mittnett.reke.Rekeverden.handlers;

import net.mittnett.reke.Rekeverden.Rekeverden;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

public class BlockInfoHandler
        implements Handler {
    private Rekeverden plugin;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy (HH:mm:ss)");

    public BlockInfoHandler(Rekeverden plugin) {
        this.plugin = plugin;
    }


    public void onEnable() {
    }


    public void onDisable() {
    }


    public void log(User user, Location loc, Block block, BlockAction action) {
        log(user.getId(), loc
                .getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), block
                .getTypeId(), block.getData(), action);
    }


    public void log(int uid, int x, int y, int z, String world, int blockID, int blockData, BlockAction action) {
        Connection conn = null;
        PreparedStatement ps = null;

        int actionInt = action.getActionID();

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("INSERT INTO `r_blocklog`(`uid`, `x`, `y`, `z`, `world`, `block_id`, `block_data`, `timestamp`, `action`)VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, uid);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, world);
            ps.setInt(6, blockID);
            ps.setInt(7, blockData);
            ps.setInt(8, (int) (System.currentTimeMillis() / 1000L));
            ps.setInt(9, actionInt);

            if (ps.executeUpdate() < 1)
                throw new SQLException("Unexpected amount of rows changed from last query");
            return;
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while logging a block.", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception (under lukking)", ex);
            }
        }
    }


    public ArrayList<String> getBlockLog(Location loc) {
        return getBlockLog(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }


    public ArrayList<String> getBlockLog(int x, int y, int z, String world) {
        ArrayList<String> logLines = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = this.plugin.getConnection();
            ps = conn.prepareStatement("SELECT `uid`, `action`, `timestamp`, `block_id`, `block_data` FROM `r_blocklog` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?");
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setString(4, world);

            rs = ps.executeQuery();
            while (rs.next()) {
                Date date = new Date(rs.getLong(3) * 1000L);
                logLines.add(this.dateFormat.format(date) + " -- " + this.plugin.getUserHandler().getUser(rs.getInt(1)).getName() + " " + actionToString(rs.getInt(2)) + " " + Material.getMaterial(rs.getInt(4)));
            }


        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "[Rekeverden] SQL Exception while fetching blocklog.", e);
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

        return logLines;
    }


    public String actionToString(int action) {
        BlockAction ba = BlockAction.fromActionID(action);
        return (ba != null) ? ba.getActionString() : "unknown";
    }
}
