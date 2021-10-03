package com.zpedroo.voltzrankup.managers.cache;

import com.zpedroo.voltzrankup.objects.PlayerData;
import com.zpedroo.voltzrankup.objects.Rank;
import com.zpedroo.voltzrankup.utils.FileUtils;
import com.zpedroo.voltzrankup.utils.builder.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DataCache {

    private Map<Integer, Rank> ranks;
    private Map<Player, PlayerData> playerData;
    private ItemStack rankUpItem;

    public DataCache() {
        this.ranks = new HashMap<>(24);
        this.playerData = new HashMap<>(128);
        this.rankUpItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Rankup-Item").build();
    }

    public Map<Integer, Rank> getRanks() {
        return ranks;
    }

    public Map<Player, PlayerData> getPlayerData() {
        return playerData;
    }

    public ItemStack getRankUpItem() {
        NBTItem nbt = new NBTItem(rankUpItem.clone());
        nbt.addCompound("RankUpItem");

        return nbt.getItem();
    }
}