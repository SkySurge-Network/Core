package skysurge.net.Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private final MongoClient client;
    private final MongoDatabase database;
    private final Map<String, MongoCollection<Document>> collections;

    private Plugin plugin;

    public Database(Plugin plugin) {
        this.plugin = plugin;

        this.client = MongoClients.create("mongodb+srv://admin:Mjking68@skysurge.o7bny.mongodb.net/?retryWrites=true&w=majority");
        this.database = client.getDatabase("SkySurge");
        this.collections = new HashMap<>();
        for(String name : database.listCollectionNames()) {
            collections.put(name, database.getCollection(name));
        }
    }

    public MongoCollection<Document> getCollection(String name) {
        return collections.get(name);
    }

    public void createCollection(String name) {
        database.createCollection(name);

        collections.put(name, database.getCollection(name));
    }

    public void close() {
        this.client.close();
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
