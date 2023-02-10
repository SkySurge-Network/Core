package skysurge.net.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {

    private String prefix = "&f&lSkySurge &7| ";

    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String colorWithPrefix(String s) {
        return color(prefix) + color(s);
    }

    public void sendColoredMessage(Player p, String s) {
        p.sendMessage(color(s));
    }

    public void sendColoredMessageWithPrefix(Player p, String s) {
        p.sendMessage(colorWithPrefix(s));
    }

}
