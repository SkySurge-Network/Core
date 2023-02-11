package skysurge.net.Objects;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class sPlayer {
    private static Map<UUID, sPlayer> players = new HashMap<>();


    private String name;
    private UUID uuid;
    private int joined;

    private int gems;

    //Shouldnt be used unless they dont exist
    public sPlayer(UUID u) {
        this.uuid = u;
        this.name = Bukkit.getPlayer(u).getName();
        this.gems = 0;

        players.put(u, this);
    }

    //basically should either get the player or return a new one!
    public sPlayer getPlayer(UUID u) {
        if(players.containsKey(u)) return players.get(u);

        return new sPlayer(u);
    }


    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getJoined() {
        return joined;
    }

    public int getGems() {
        return gems;
    }
}
