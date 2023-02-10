package skysurge.net;

import org.bukkit.plugin.java.JavaPlugin;
import skysurge.net.Database.Database;

import javax.xml.crypto.Data;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    private Database db;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        db = new Database(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Database getDb() {
        return db;
    }
}
