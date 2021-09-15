package com.zpedroo.voltzrankup.hooks;

import com.zpedroo.voltzrankup.managers.DataManager;
import com.zpedroo.voltzrankup.objects.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private Plugin plugin;

    public PlaceholderAPIHook(Plugin plugin) {
        this.plugin = plugin;
    }

    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    public String getIdentifier() {
        return "rankup";
    }

    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        PlayerData data = DataManager.getInstance().load(player);
        return switch (identifier.toUpperCase()) {
            case "RANK" -> data.getRank() == null ? "-/-" : data.getRank().getTag();
            case "NEXT_RANK" -> data.getNextRank() == null ? "-/-" : data.getNextRank().getTag();
            default -> null;
        };
    }
}