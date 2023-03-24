package net.estra.EstraPearls;

import net.estra.EstraPearls.command.*;
import net.estra.EstraPearls.config.ConfigManager;
import net.estra.EstraPearls.listener.DmgListener;
import net.estra.EstraPearls.listener.PlayerListener;
import net.estra.EstraPearls.listener.PearlTrackListener;
import net.estra.EstraPearls.model.CombatTagManager;
import net.estra.EstraPearls.model.DamageLogManager;
import net.estra.EstraPearls.model.PearlManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.dao.ConnectionPool;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;

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

        createDAO();
        pearlManager = new PearlManager();
        damageLogManager = new DamageLogManager();
        combatTagManager = new CombatTagManager();

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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> pearlManager.verifyPearls(), 12000, 100L);
    }

    //protected String getPluginName() {
   //    return "EstraPearls";
   // }

    @Override
    public void onDisable() {
        //For now we'll just save pearls on shutdown. This is easiest with the current configuration.
        pearlManager.savePearls();
    }

    public CombatTagManager getCombatTagManager() { return combatTagManager; }

    public void createDAO() {
        pearlDAO = new PearlDAO(this, ConfigManager.getConpolFromConfig());
    }


    public DamageLogManager getDamageLogManager() { return damageLogManager;}

    public long getPearlTimeInMilis() {
        return (long) pearlTime * 86400000;
    }
}
