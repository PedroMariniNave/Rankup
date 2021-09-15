package com.zpedroo.voltzrankup.managers.cache;

import com.zpedroo.voltzrankup.objects.PlayerData;
import com.zpedroo.voltzrankup.objects.Rank;
import org.bukkit.entity.Player;

import java.util.*;

public class DataCache {

    private Map<Integer, Rank> ranks;
    private Map<Player, PlayerData> playerData;

    public DataCache() {
        this.ranks = new HashMap<>(24);
        this.playerData = new HashMap<>(128);
    }

    public Map<Integer, Rank> getRanks() {
        return ranks;
    }

    public Map<Player, PlayerData> getPlayerData() {
        return playerData;
    }
}