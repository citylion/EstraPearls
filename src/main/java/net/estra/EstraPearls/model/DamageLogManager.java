package net.estra.EstraPearls.model;


import net.estra.EstraPearls.PearlPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class DamageLogManager implements Runnable{

    private PearlPlugin plugin;
    private boolean scheduled;
    private final Map<String, DamageLog> logs;

    public DamageLogManager() {
        plugin = PearlPlugin.instance;

        scheduled = false;
        logs = new HashMap<String, DamageLog>();
    }

    public List<Player> getDamagers(Player player) {
        DamageLog log = logs.get(player.getName());
        if (log != null)
            return log.getDamagers(3);
        else
            return new ArrayList<Player>();
    }

    public void removeDamage(String name) {
        logs.remove(name);
    }

    public boolean hasDamageLog(Player player) {
        return logs.containsKey(player.getName());
    }

    public void recordDamage(Player player, Player damager, double amt) {
        DamageLog log = logs.get(player.getName());
        if (log == null) {
            log = new DamageLog(player);
            logs.put(player.getName(), log);
        }

        long ticks = 600;
        log.recordDamage(damager, (int)amt, getNowTick() + ticks);
        scheduleExpireTask(ticks);
    }

    public void run() {
        scheduled = false;

        long nowtick = getNowTick();

        Iterator<DamageLog> i = logs.values().iterator();
        long minremaining = Long.MAX_VALUE;
        while (i.hasNext()) {
            DamageLog log = i.next();
            long remaining = nowtick-log.getExpiresTick();

            if (remaining <= 600/20) {
                i.remove();
                continue;
            }

            minremaining = Math.min(minremaining, remaining);
        }

        if (minremaining < Long.MAX_VALUE)
            scheduleExpireTask(minremaining);
    }

    private void scheduleExpireTask(long ticks) {
        if (scheduled)
            return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, ticks);
        scheduled = true;
    }

    private long getNowTick() {
        return Bukkit.getWorlds().get(0).getFullTime();
    }
}
