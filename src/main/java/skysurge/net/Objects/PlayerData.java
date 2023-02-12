package skysurge.net.Objects;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private static Map<UUID, sPlayer> players = new HashMap<>();

    public sPlayer getOrCreate(UUID u) {
        if(players.containsKey(u)) return players.get(u);

        return new sPlayer(u);
    }

    public class sPlayer {
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
            return gems == 0 ? 0 : gems;
        }

        public void addGems(int amount) {
            this.gems += amount;
        }

        public void removeGems(int amount) {
            this.gems -= amount;
        }
    }
}
