package kr.lucymc.here.lucy_fly;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import static kr.lucymc.here.lucy_fly.Lucy_FLY.*;

class Fly_PAPI extends PlaceholderExpansion {

    private final Lucy_FLY plugin;
    FileConfiguration config = Lucy_FLY.getInstance().getConfig();

    public Fly_PAPI(Lucy_FLY plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "LUCY-FLY";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) return "";
        if(map.containsKey(""+player.getUniqueId())) {
            if (identifier.equals("FLY-TIME-FORMAT")) {
                return TimeFormat(config, "TimeFormat", map.get("" + player.getUniqueId()));
            } else if (identifier.equals("FLY-TIME-H")) {
                return String.valueOf(map.get("" + player.getUniqueId()) / 3600);
            } else if (identifier.equals("FLY-TIME-M")) {
                return String.valueOf((map.get("" + player.getUniqueId()) % 3600) / 60);
            } else if (identifier.equals("FLY-TIME-S")) {
                return String.valueOf(map.get("" + player.getUniqueId()) % 60);
            } else if (player.getPlayer().isFlying()) {
                switch (identifier) {
                    case "IF-FLY-TIME-FORMAT" -> {
                        return TimeFormat(config, "TimeFormat", map.get("" + player.getUniqueId()));
                    }
                    case "IF-FLY-TIME-H" -> {
                        return String.valueOf(map.get("" + player.getUniqueId()) / 3600);
                    }
                    case "IF-FLY-TIME-M" -> {
                        return String.valueOf((map.get("" + player.getUniqueId()) % 3600) / 60);
                    }
                    case "IF-FLY-TIME-S" -> {
                        return String.valueOf(map.get("" + player.getUniqueId()) % 60);
                    }
                }
            }
        }else{
            return "R?oading..";
        }
        return null;
    }
}
