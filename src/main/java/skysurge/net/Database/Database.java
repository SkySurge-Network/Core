package skysurge.net.Database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Database {

    private MongoClientOptions options;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoClientURI uri;
    private MongoCollection<Document> collection;
    private String databaseName = "SkySurge";

    public Database(String uri) {

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder().connectionsPerHost(100).maxWaitTime(100000);
        MongoClientURI clientURI = new MongoClientURI(uri, optionsBuilder);
        this.mongoClient = new MongoClient(clientURI);

        System.out.println("SkySurge | Mongo client has been connected!");
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.mongoClient.getDatabase(databaseName).getCollection(name);
    }


}
