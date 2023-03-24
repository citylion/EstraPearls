package net.estra.EstraPearls.model;

import net.estra.EstraPearls.PearlPlugin;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.NpcManager;
import net.minelink.ctplus.TagManager;
import net.minelink.ctplus.compat.base.NpcIdentity;
import net.minelink.ctplus.compat.base.NpcPlayerHelper;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * @Import PrisonPearl
 */
public class CombatTagManager {
    private NpcPlayerHelper combatTagPlusApi;

    private boolean combatTagPlusEnabled = false;
    private TagManager combatTagPlusTagManager;

    public boolean isEnabled() {
        return combatTagPlusEnabled;
    }

    public CombatTagManager() {
        if (PearlPlugin.instance.getServer().getPluginManager().getPlugin("CombatTagPlus") != null){
            combatTagPlusApi = ((CombatTagPlus) PearlPlugin.instance.getServer().getPluginManager().getPlugin("CombatTagPlus")).getNpcPlayerHelper();
            combatTagPlusTagManager = ((CombatTagPlus) PearlPlugin.instance.getServer().getPluginManager().getPlugin("CombatTagPlus")).getTagManager();
            combatTagPlusEnabled = true;
        }
    }

    public boolean isCombatTagPlusNPC(Player player) {
        return combatTagPlusEnabled && ((CombatTagPlus) PearlPlugin.instance.getServer().getPluginManager().getPlugin("CombatTagPlus")).getNpcPlayerHelper().isNpc(player);
    }

    public boolean isCombatTagged(Player player) {
        if (player == null) // If a player is on another server.
            return false;
        return (combatTagPlusEnabled && ((CombatTagPlus) PearlPlugin.instance.getServer().getPluginManager().getPlugin("CombatTagPlus")).getNpcPlayerHelper() != null && ((CombatTagPlus) PearlPlugin.instance.getServer().getPluginManager().getPlugin("CombatTagPlus")).getTagManager().isTagged(player.getUniqueId()));
    }

    public boolean isCombatTagged(String playerName) {
        return isCombatTagged(PearlPlugin.instance.getServer().getPlayer(playerName));
    }

    public NpcIdentity getCombatTagPlusNPCIdentity(Player player){
        return ((CombatTagPlus) PearlPlugin.instance.getServer().getPluginManager().getPlugin("CombatTagPlus")).getNpcPlayerHelper().getIdentity(player);
    }
}
