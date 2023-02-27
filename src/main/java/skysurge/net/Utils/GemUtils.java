package skysurge.net.Utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import skysurge.net.Objects.PlayerManager;

import java.util.UUID;

public class GemUtils {

    private Plugin main;
    private PlayerManager playerManager;

    public GemUtils(Plugin main, PlayerManager pm) {
        this.main = main;
        this.playerManager = pm;
    }

    public int getGems(Player p) {
        return playerManager.getPlayer(p.getUniqueId()).getGems();
    }

    public int getGems(UUID uuid) {
        return playerManager.getPlayer(uuid).getGems();
    }

    public void addGems(Player p, int amount) {
        playerManager.getPlayer(p.getUniqueId()).addGems(amount);
    }

    public void setGems(Player p, int amount) {
        playerManager.getPlayer(p.getUniqueId()).setGems(amount);
    }

    public void removeGems(Player p, int amount) {
        playerManager.getPlayer(p.getUniqueId()).removeGems(amount);
    }
}
