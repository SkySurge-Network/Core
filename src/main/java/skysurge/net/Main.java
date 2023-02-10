package skysurge.net;

import org.bukkit.plugin.java.JavaPlugin;
import skysurge.net.Database.Database;

import javax.xml.crypto.Data;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
