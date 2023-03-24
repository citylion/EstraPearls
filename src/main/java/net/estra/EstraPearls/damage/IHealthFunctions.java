package net.estra.EstraPearls.damage;

import java.util.Date;

public interface IHealthFunctions {
    //Interface for pearl health functions most broadly speaking
    //An interface is a contract that classes who implement this interface must follow
    //The contract specifies methods that the class must implement

    double calculateAccumulatedDamage(long milisPearled);//WHAT is accumulated damage?? Accumulated damage is the total damage the pearl
    //accumulates simply by existing over time. The function reports how much damage the pearl has accumulated, according to whatever
    //specifications that function has in the config.

    //All functions required to be invertible. "definition of a function"

    long milisWhenFree(long milisPearled, double pearlHealth);

    //Date calculateEstFreeDate(long milisPearled, double pearlHealth);



}
