package net.estra.EstraPearls;

import net.estra.EstraPearls.model.Pearl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PearlDAO extends ManagedDatasource {
    public PearlDAO(ACivMod plugin, String user, String pass, String host, int port, String database, int poolSize, long connectionTimeout, long idleTimeout, long maxLifetime) {
        super(plugin, user, pass, host, port, database, poolSize, connectionTimeout, idleTimeout, maxLifetime);
        prepareMigrations();
        updateDatabase();
    }

    /**
     * Lets keep in mind, pearls that are held in player inventories or on the ground are not going to be saved
     * by the plugin in DB. This just creates an unnecessary hassle and technically the pearl isn't even "pearled" yet.
     */
    private void prepareMigrations() {
        registerMigration(0, false, "CREATE TABLE IF NOT EXISTS pearls ( `uuid` VARCHAR(100) NOT NULL, `playerName` VARCHAR(50) NOT NULL,`killerName` VARCHAR(50) NOT NULL,`date` BIGINT NOT NULL,`dateFreed` BIGINT NOT NULL,`x` INT NOT NULL,`y` INT NOT NULL,`z` INT NOT NULL,`world` VARCHAR(50) NOT NULL);");
    }

    public List<Pearl> loadPearls() {
        List<Pearl> list = new ArrayList<>();
        try {
            Connection conn = this.getConnection();
            PreparedStatement ps = conn.prepareStatement("select uuid, playerName, killerName, date, dateFreed, x, y, z, world FROM pearls;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString(1);
                String playerName = rs.getString(2);
                String killerName = rs.getString(3);
                long datePearl = rs.getLong(4);
                long dateFree = rs.getLong(5);
                int x = rs.getInt(6);
                int y = rs.getInt(7);
                int z = rs.getInt(8);
                String world = rs.getString(9);
                Pearl pearl = new Pearl(uuid, playerName, killerName, datePearl, dateFree, x, y ,z, world);
                list.add(pearl);
            }
        } catch (SQLException ex) {
            PearlPlugin.logger.severe("FAILED TO LOAD PEARLS, STOP THIS SERVER." + ex.getMessage());
            //Warn people that way pearls fucking up doesn't ruin the server.
            Bukkit.getScheduler().scheduleSyncRepeatingTask(PearlPlugin.instance, () -> {
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "[EP] Has failed to load pearls, contact an admin ASAP.");
            }, 200, 200);
        }
        return list;
    }

    public void addPearl(Pearl pearl) {
        try { Connection conn = this.getConnection();
            PreparedStatement ps = conn.prepareStatement("insert into pearls(uuid, playerName, killerName, date, dateFreed, x, y, z, world) values (?,?,?,?,?,?,?,?,?);");
            {
                ps.setString(1, pearl.getPlayer().toString());
                ps.setString(2, pearl.getPearlName());
                ps.setString(3, pearl.getKillerName());
                ps.setLong(4, pearl.getPearlDate().getTime());
                ps.setLong(5, pearl.getFreeDate().getTime());
                ps.setInt(6, pearl.getHolder().getLocation().getBlockX());
                ps.setInt(7, pearl.getHolder().getLocation().getBlockY());
                ps.setInt(8, pearl.getHolder().getLocation().getBlockZ());
                ps.setString(9, pearl.getHolder().getLocation().getWorld().getName());
                ps.execute();
            }
        } catch(SQLException ex) {
            PearlPlugin.logger.severe("Failed to save Pearl to DB, UUID: " + pearl.getPlayer().toString());
        }
    }

    public void removePearl(Pearl pearl) {
        try {
            Connection conn = this.getConnection();
            PreparedStatement ps = conn.prepareStatement("delete from pearls where uuid = ?");
            {
                ps.setString(1, pearl.getPlayer().toString());
                ps.execute();
            }
        } catch (SQLException ex) {
            PearlPlugin.logger.severe("Failed to remove Pearl from DB, UUID: " + pearl.getPlayer().toString());
        }
    }
}
