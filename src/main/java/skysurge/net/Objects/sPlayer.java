package skysurge.net.Objects;

import org.bukkit.Bukkit;

import java.util.UUID;

public class sPlayer {

    private String name;
    private UUID uuid;
    private int joined;

    private int gems;

    public sPlayer(UUID u) {
        this.uuid = u;
        this.name = Bukkit.getPlayer(u).getName();
        this.gems = 0;
    }

}
