package com.zpedroo.voltzrankup.objects;

import com.zpedroo.voltzrankup.managers.DataManager;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private Rank rank;
    private Boolean update;

    public PlayerData(UUID uuid, Rank rank) {
        this.uuid = uuid;
        this.rank = rank;
        this.update = false;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Rank getRank() {
        return rank;
    }

    public Boolean isQueueUpdate() {
        return update;
    }

    public Boolean hasNextRank() {
        return getNextRank() != null;
    }

    public Rank getNextRank() {
        return DataManager.getInstance().getCache().getRanks().get(rank.getId() + 1);
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        this.update = true;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }
}