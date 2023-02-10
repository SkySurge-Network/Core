package skysurge.net.Database;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ConnectionPoolSettings;
import com.zaxxer.hikari.HikariDataSource;
import org.bson.Document;
import skysurge.net.Main;

public class Database {

    private Main main;
    private MongoClientOptions options;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoClientURI uri;
    private MongoCollection<Document> collection;
    private HikariDataSource dataSource;
    private String databaseName = "SkySurge";

    public Database(Main main) {
        this.main = main;

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder().connectionsPerHost(100).maxWaitTime(100000);
        MongoClientURI clientURI = new MongoClientURI("mongodb+srv://Core:ciWECEGO7EEr9b83@skysurge.yjqzj2q.mongodb.net/?retryWrites=true&w=majority", optionsBuilder);
        this.mongoClient = new MongoClient(clientURI);

        System.out.println("SkySurge | Mongo client has been connected!");
    }

}
