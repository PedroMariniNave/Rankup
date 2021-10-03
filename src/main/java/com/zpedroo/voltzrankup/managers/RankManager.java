package com.zpedroo.voltzrankup.managers;

import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.Currency;
import com.zpedroo.voltzrankup.hooks.VaultHook;
import com.zpedroo.voltzrankup.objects.PlayerData;
import com.zpedroo.voltzrankup.objects.Rank;
import com.zpedroo.voltzrankup.utils.FileUtils;
import com.zpedroo.voltzrankup.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zpedroo.voltzrankup.utils.config.Titles.*;

public class RankManager extends DataManager {

    private static RankManager instance;
    public static RankManager getInstance() { return instance; }

    public RankManager() {
        instance = this;
        this.loadRanks();
    }

    public void upgradeRank(Player player, Boolean takeRequirements) {
        PlayerData data = load(player);
        if (data == null) return;

        Rank rank = data.getRank();
        if (rank == null) return;

        Rank nextRank = data.getNextRank();
        if (nextRank == null) return;

        data.setRank(nextRank);
        if (takeRequirements) takeRequirements(player, nextRank);

        for (String cmd : nextRank.getRankUPCommands()) {
            if (cmd == null) continue;

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(cmd, new String[]{
                    "{player}",
                    "{old_rank}",
                    "{new_rank}"
            }, new String[]{
                    player.getName(),
                    rank.getName(),
                    nextRank.getName()
            }));
        }

        player.sendTitle(RANKUP_TITLE, RANKUP_SUBTITLE);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }

    public Boolean canUpgradeRank(Player player) {
        Rank nextRank = load(player).getNextRank();
        if (nextRank == null) return false;

        for (Map.Entry<String, BigInteger> entry : nextRank.getRequirements().entrySet()) {
            String requirement = entry.getKey();
            BigInteger price = entry.getValue();

            switch (requirement) {
                case "VAULT" -> {
                    return VaultHook.getMoney(player).compareTo(price) > 0;
                }
                default -> {
                    Currency currency = CurrencyAPI.getCurrency(requirement);
                    if (currency == null) return false;

                    return CurrencyAPI.getCurrencyAmount(player, currency).compareTo(price) > 0;
                }
            }
        }

        return true;
    }

    private void takeRequirements(Player player, Rank rank) {
        for (Map.Entry<String, BigInteger> entry : rank.getRequirements().entrySet()) {
            String requirement = entry.getKey();
            BigInteger price = entry.getValue();

            switch (requirement) {
                case "VAULT" -> VaultHook.removeMoney(player, price);
                default -> {
                    Currency currency = CurrencyAPI.getCurrency(requirement);
                    if (currency == null) return;

                    CurrencyAPI.removeCurrencyAmount(player, currency, price);
                }
            }
        }
    }

    private void loadRanks() {
        FileUtils.Files file = FileUtils.Files.CONFIG;

        int id = 0;
        for (String ranks : FileUtils.get().getSection(file, "Ranks")) {
            if (ranks == null) continue;

            String tag = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Ranks." + ranks + ".tag"));
            Map<String, BigInteger> requirements = new HashMap<>(2);
            for (String str : FileUtils.get().getStringList(file, "Ranks." + ranks + ".requirements")) {
                if (str == null) continue;

                String[] split = str.split(",");

                String requirement = split[0];
                BigInteger price = NumberFormatter.getInstance().filter(split[1]);

                requirements.put(requirement, price);
            }

            List<String> rankupCommands = FileUtils.get().getStringList(file, "Ranks." + ranks + ".rankup-commands");

            getCache().getRanks().put(++id, new Rank(ranks, tag, requirements, rankupCommands, id));
        }
    }

    public Rank getRank(String rankName) {
        for (Rank rank : getCache().getRanks().values()) {
            if (!rank.getName().equals(rankName)) continue;

            return rank;
        }

        return null;
    }

    public Rank getRank(Integer id) {
        return getCache().getRanks().get(id);
    }
}