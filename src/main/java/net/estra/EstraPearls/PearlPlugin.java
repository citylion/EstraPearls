package net.estra.EstraPearls;

import net.estra.EstraPearls.command.Debug;
import net.estra.EstraPearls.command.FreeCommand;
import net.estra.EstraPearls.listener.DmgListener;
import net.estra.EstraPearls.listener.PlayerListener;
import net.estra.EstraPearls.model.DamageLogManager;
import net.estra.EstraPearls.model.PearlManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PearlPlugin extends JavaPlugin {

    public static PearlPlugin instance;
    public static Configuration config;
    public static PearlManager pearlManager;
    public static DamageLogManager damageLogManager;

    public static Logger logger;

    //In Days by default.
    public static int pearlTime;

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();
        config = this.getConfig();
        pearlManager = new PearlManager();
        damageLogManager = new DamageLogManager();

        pearlTime = config.getInt("pearlTime");

        this.getServer().getPluginManager().registerEvents(new DmgListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getCommand("debug").setExecutor(new Debug());
        this.getCommand("free").setExecutor(new FreeCommand());
    }

    @Override
    public void onDisable() {

    }

    public DamageLogManager getDamageLogManager() { return damageLogManager;}

    public long getPearlTimeInMilis() {
        return (long) pearlTime * 86400000;
    }
}
