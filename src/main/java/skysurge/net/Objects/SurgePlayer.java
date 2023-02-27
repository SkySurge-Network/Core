package skysurge.net.Objects;

import org.bukkit.Bukkit;

import java.util.UUID;

public class SurgePlayer {

    private String id;
    private String name;
    private int gems;
    private int mobs;
    private int cane;

    public SurgePlayer(UUID u) {
        this.id = u.toString();
        this.name = Bukkit.getOfflinePlayer(u).getName();

        this.gems = 0;
        this.mobs = 0;
        this.cane = 0;
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public void addGems(int amount) {
        this.gems += amount;
    }

    public void removeGems(int amount) {
        this.gems -= amount;
    }

    public void setId(UUID id) {
        this.id = id.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGems() {
        return gems;
    }

    public void addCane(int amount) {
        this.cane += amount;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public int getMobs() {
        return mobs;
    }

    public void setMobs(int mobs) {
        this.mobs = mobs;
    }

    public int getCane() {
        return cane;
    }

    public void setCane(int cane) {
        this.cane = cane;
    }
}