package skysurge.net.Objects;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.plugin.Plugin;
import skysurge.net.Database.Database;
import skysurge.net.Main;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerManager {

    private Plugin plugin;
    private Database database;

    private Map<UUID, SurgePlayer> players = new HashMap<>();
    private List<SurgePlayer> playerList = new ArrayList<>();

    public PlayerManager(Plugin plugin, Database db) {
        this.plugin = plugin;
        this.database = db;
    }

    public SurgePlayer getPlayer(UUID u) {
        return players.get(u);
    }

    public List<SurgePlayer> getTop10Cane() {
        return playerList.stream().sorted(Comparator.comparingInt(SurgePlayer::getCane).reversed()).limit(10).collect(Collectors.toList());
    }

    public void createPlayer(UUID u) {
        SurgePlayer sp = new SurgePlayer(u);

        this.players.put(u, sp);
        this.playerList.add(sp);
    }

    public void addPlayer(SurgePlayer sp) {
        this.playerList.add(sp);
        this.players.put(sp.getId(), sp);
    }

    public void save(SurgePlayer sp) {
        Gson gson = new Gson();
        String json = gson.toJson(sp).trim();
        database.getCollection("players").updateOne(Filters.eq("id", sp.getId().toString()), new Document("$set", Document.parse(json)), new UpdateOptions().upsert(true));
    }

    public void savePlayers(){
        for(SurgePlayer sp : playerList) {
            save(sp);
        }
    }

    public void loadPlayers() {
        Gson gson = new Gson();
        FindIterable<Document> iterable = database.getCollection("players").find();
        for(Document doc : iterable) {
            SurgePlayer sp = gson.fromJson(doc.toJson(), SurgePlayer.class);
            players.put(sp.getId(), sp);
            playerList.add(sp);
        }
    }
}