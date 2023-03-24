package net.estra.EstraPearls.config;

import net.estra.EstraPearls.PearlDAO;
import net.estra.EstraPearls.PearlPlugin;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import vg.civcraft.mc.civmodcore.dao.ConnectionPool;
import vg.civcraft.mc.civmodcore.dao.DatabaseCredentials;

import java.util.function.Function;

public class ConfigManager {

    private static Configuration config = PearlPlugin.config;
    public static int pearlTime = config.getInt("pearlTime");

    public static ConnectionPool getConpolFromConfig() {
        ConfigurationSection sqlConfig = config.getConfigurationSection("sql");
        String host = sqlConfig.getString("host");
        String user = sqlConfig.getString("username");
        String pass = sqlConfig.getString("password");
        String dbName = sqlConfig.getString("dbname");
        int port = sqlConfig.getInt("port");
        try {
            DatabaseCredentials sqlCreds = new DatabaseCredentials(user, pass, host, port, "mysql", dbName, 5, 5000L, 600000L, 7200000L);
            ConnectionPool conpol = new ConnectionPool(sqlCreds);
            return conpol;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static int getPearlHealthAccordingConfig(long milispearled){
        ConfigurationSection formulaDecaySect = config.getConfigurationSection("formulaDecay");
        String functionType = formulaDecaySect.getString("formulatype");
        Function function;
        if(functionType.equalsIgnoreCase("linear")){


        }




        return 1;
    }

}
