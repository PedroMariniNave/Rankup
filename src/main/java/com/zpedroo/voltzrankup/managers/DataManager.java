package com.zpedroo.voltzrankup.managers;

import com.zpedroo.voltzrankup.managers.cache.DataCache;
import com.zpedroo.voltzrankup.mysql.DBConnection;
import com.zpedroo.voltzrankup.objects.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class DataManager {

    private static DataManager instance;
    public static DataManager getInstance() { return instance; }

    private DataCache dataCache;

    public DataManager() {
        instance = this;
        this.dataCache = new DataCache();
    }

    public PlayerData load(Player player) {
        PlayerData data = dataCache.getPlayerData().get(player);
        if (data == null) {
            data = DBConnection.getInstance().getDBManager().loadData(player);
            dataCache.getPlayerData().put(player, data);
        }

        return data;
    }

    public void save(Player player) {
        PlayerData data = dataCache.getPlayerData().get(player);
        if (data == null) return;
        if (!data.isQueueUpdate()) return;

        DBConnection.getInstance().getDBManager().saveData(data);
        data.setUpdate(false);
    }

    public void saveAll() {
        new HashSet<>(dataCache.getPlayerData().keySet()).forEach(this::save);
    }

    public DataCache getCache() {
        return dataCache;
    }
}