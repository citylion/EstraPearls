package net.estra.EstraPearls;

import net.estra.EstraPearls.command.*;
import net.estra.EstraPearls.listener.DmgListener;
import net.estra.EstraPearls.listener.PlayerListener;
import net.estra.EstraPearls.listener.PearlTrackListener;
import net.estra.EstraPearls.model.CombatTagManager;
import net.estra.EstraPearls.model.DamageLogManager;
import net.estra.EstraPearls.model.PearlManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import vg.civcraft.mc.civmodcore.ACivMod;

import java.util.logging.Logger;

public class PearlPlugin extends ACivMod {

    public static PearlPlugin instance;
    public static Configuration config;
    public static PearlManager pearlManager;
    public static DamageLogManager damageLogManager;
    public CombatTagManager combatTagManager;
    public static PearlDAO pearlDAO;

    public static Logger logger;

    //In Days by default.
    public static int pearlTime;

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();
        config = this.getConfig();
        saveDefaultConfig();
        reloadConfig();

        parseSqlConfig();
        pearlManager = new PearlManager();
        damageLogManager = new DamageLogManager();
        combatTagManager = new CombatTagManager();

        pearlTime = config.getInt("pearlTime");

        pearlManager.loadPearls();

        this.getServer().getPluginManager().registerEvents(new DmgListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new PearlTrackListener(), this);
        this.getCommand("ep").setExecutor(new EpLocateCommand());
        //this.getCommand("debug").setExecutor(new Debug());
        this.getCommand("epfree").setExecutor(new FreeCommand());
        this.getCommand("pearl").setExecutor(new PearlCommand());
        this.getCommand("eplocate").setExecutor(new EpLocateCommand());
        this.getCommand("forcefree").setExecutor(new ForceFree());

        pearlManager.verifyPearls(); //Immediately verify pearls.

        //Verify our pearls every 10 minutes so we dont fucking die.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> pearlManager.verifyPearls(), 12000, 12000);
    }

    @Override
    protected String getPluginName() {
        return "EstraPearls";
    }

    @Override
    public void onDisable() {
        //For now we'll just save pearls on shutdown. This is easiest with the current configuration.
        pearlManager.savePearls();
    }

    public CombatTagManager getCombatTagManager() { return combatTagManager; }

    public void parseSqlConfig() {
        ConfigurationSection sql = config.getConfigurationSection("sql");
        String host = sql.getString("host");
        String user = sql.getString("username");
        String pass = sql.getString("password");
        String dbName = sql.getString("dbname");
        int port = sql.getInt("port");
        pearlDAO = new PearlDAO(this, user, pass, host, port, dbName, 5, 1000L, 600000L, 7200000L);
    }


    public DamageLogManager getDamageLogManager() { return damageLogManager;}

    public long getPearlTimeInMilis() {
        return (long) pearlTime * 86400000;
    }
}
