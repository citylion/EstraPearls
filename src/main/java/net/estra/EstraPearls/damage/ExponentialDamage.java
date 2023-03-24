package net.estra.EstraPearls.damage;

import net.estra.EstraPearls.Logger;

import java.util.Date;

public class ExponentialDamage implements IHealthFunctions{
    //p * e^k(t)
    private static String fname = "exponential";
    private static int milisInDay = 86400000;

    @Override
    public double calculateAccumulatedDamage(long t) {//t is milliseconds duration pearled
        //p * e^k(t)

        if( ! DamageFunctionDS.getDatapointFromIdentifier("ftype").equalsIgnoreCase(fname)){
            Logger.Error("Code is calling calculate damage for ftype " + fname + ", but it is not set in the config.");
            //IF the code is trying to calculate damage for a function not in the config
            //THEN there has been some sort of operator error, do not report any damage
            return 0;
        }

        int p=1;
        int k=1;
        String p_s = DamageFunctionDS.getDatapointFromIdentifier("p"); if(p_s != null){ p = Integer.parseInt(p_s); }
        String k_s = DamageFunctionDS.getDatapointFromIdentifier("k"); if(k_s != null){ k = Integer.parseInt(k_s); }


        //"apply function" "per day"
        double dayFraction = t/milisInDay;

        return ( p * Math.exp(k * dayFraction));


    }

    @Override
    public long milisWhenFree(long milisPearled, double pearlHealth) {
        if( ! DamageFunctionDS.getDatapointFromIdentifier("ftype").equalsIgnoreCase(fname)){
            Logger.Error("Code is calling calculate damage for ftype " + fname + ", but it is not set in the config.");
            //IF the code is trying to calculate damage for a function not in the config
            //THEN there has been some sort of operator error, do not report any freedom
            return 31536000000L;//One year
        }

        int p=1;
        int k=1;
        String p_s = DamageFunctionDS.getDatapointFromIdentifier("p"); if(p_s != null){ p = Integer.parseInt(p_s); }
        String k_s = DamageFunctionDS.getDatapointFromIdentifier("k"); if(k_s != null){ k = Integer.parseInt(k_s); }

        double dayFrac = ( Math.log(pearlHealth/p)) / k;

        return (long) (dayFrac*milisInDay);


    }
}
