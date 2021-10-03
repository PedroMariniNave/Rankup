package com.zpedroo.voltzrankup.utils.menus;

import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.Currency;
import com.zpedroo.voltzrankup.hooks.VaultHook;
import com.zpedroo.voltzrankup.managers.DataManager;
import com.zpedroo.voltzrankup.managers.RankManager;
import com.zpedroo.voltzrankup.objects.PlayerData;
import com.zpedroo.voltzrankup.objects.Rank;
import com.zpedroo.voltzrankup.utils.FileUtils;
import com.zpedroo.voltzrankup.utils.builder.InventoryBuilder;
import com.zpedroo.voltzrankup.utils.builder.InventoryUtils;
import com.zpedroo.voltzrankup.utils.builder.ItemBuilder;
import com.zpedroo.voltzrankup.utils.config.Messages;
import com.zpedroo.voltzrankup.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Menus {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    private InventoryUtils inventoryUtils;

    private ItemStack nextPageItem;
    private ItemStack previousPageItem;

    public Menus() {
        instance = this;
        this.inventoryUtils = new InventoryUtils();
        this.nextPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Next-Page").build();
        this.previousPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Previous-Page").build();
    }

    public void openRankUPMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.RANKUP;

        String title = getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        PlayerData data = DataManager.getInstance().load(player);
        Rank rank = data.getRank();
        Rank nextRank = data.getNextRank();

        String status = RankManager.getInstance().canUpgradeRank(player) ? "can-upgrade" : "cant-upgrade";
        double generalProgress = 0;

        List<String> requirements = new ArrayList<>(nextRank.getRequirements().size());
        for (Map.Entry<String, BigInteger> entry : nextRank.getRequirements().entrySet()) {
            String requirement = entry.getKey();
            BigInteger need = entry.getValue();
            BigInteger has = BigInteger.ZERO;
            double progress = 0;

            switch (requirement) {
                case "VAULT" -> has = VaultHook.getMoney(player);
                default -> {
                    Currency currency = CurrencyAPI.getCurrency(requirement);
                    if (currency == null) break;

                    has = CurrencyAPI.getCurrencyAmount(player, currency);
                }
            }

            if (has.compareTo(need) > 0) has = need;
            if (need.signum() > 0) progress = has.doubleValue() / need.doubleValue();

            generalProgress += progress;

            requirements.add(getColored(StringUtils.replaceEach(FileUtils.get().getString(FileUtils.Files.CONFIG, "Requirements." + requirement + ".1"), new String[]{
                    "{has}",
                    "{need}",
                    "{progress}"
            }, new String[]{
                    NumberFormatter.getInstance().format(has),
                    NumberFormatter.getInstance().format(need),
                    NumberFormatter.getInstance().formatDecimal(progress * 100)
            })));
        }

        requirements.sort(Collections.reverseOrder()); // fix reverse order

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{player}",
                    "{rank}",
                    "{next_rank}",
                    "{requirements}",
                    "{status}",
                    "{progress}"
            }, new String[]{
                    player.getName(),
                    rank.getTag(),
                    nextRank.getTag(),
                    convertListToString(new ArrayList<>(requirements), "|"),
                    getColored(FileUtils.get().getString(file, "Lore-Filter." + status)),
                    NumberFormatter.getInstance().formatDecimal(generalProgress * 100 / nextRank.getRequirements().size())
            }).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            switch (action.toUpperCase()) {
                case "RANKUP" -> inventoryUtils.addAction(inventory, slot, () -> {
                    if (!RankManager.getInstance().canUpgradeRank(player)) {
                        player.closeInventory();
                        for (String msg : Messages.INSUFFICIENT_REQUIREMENTS) {
                            if (msg == null) continue;

                            player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                                    "{requirements}"
                            }, new String[]{
                                    convertListToString(new ArrayList<>(requirements), "\n")
                            }));
                        }
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return;
                    }

                    RankManager.getInstance().upgradeRank(player, true);
                    if (data.getNextRank() == null || data.getNextRank().getRequirements().isEmpty()) {
                        player.closeInventory();
                        return;
                    }

                    openRankUPMenu(player);
                }, InventoryUtils.ActionType.ALL_CLICKS);
                case "CANCEL" -> inventoryUtils.addAction(inventory, slot, player::closeInventory, InventoryUtils.ActionType.ALL_CLICKS);
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openRanksMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.RANKS;

        String title = getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);
        List<ItemBuilder> builders = new ArrayList<>(DataManager.getInstance().getCache().getRanks().size());
        String[] slots = FileUtils.get().getString(file, "Inventory.slots").replace(" ", "").split(",");
        int i = -1;
        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            if (++i >= slots.length) i = 0;

            Rank rank = RankManager.getInstance().getRank(str);
            if (rank == null) continue;

            PlayerData data = DataManager.getInstance().load(player);
            String status = data.getRank().getId() >= rank.getId() ? "unlocked" : "locked";

            List<String> requirements = new ArrayList<>(rank.getRequirements().size());
            for (Map.Entry<String, BigInteger> entry : rank.getRequirements().entrySet()) {
                String requirement = entry.getKey();
                BigInteger need = entry.getValue();
                BigInteger has = BigInteger.ZERO;
                double progress = 0;

                switch (requirement) {
                    case "VAULT" -> has = VaultHook.getMoney(player);
                    default -> {
                        Currency currency = CurrencyAPI.getCurrency(requirement);
                        if (currency == null) break;

                        has = CurrencyAPI.getCurrencyAmount(player, currency);
                    }
                }

                if (has.compareTo(need) > 0) has = need;
                if (need.signum() > 0) progress = has.doubleValue() / need.doubleValue();

                requirements.add(getColored(StringUtils.replaceEach(FileUtils.get().getString(FileUtils.Files.CONFIG, "Requirements." + requirement + "." + (data.getNextRank() == null ? "2" : (data.getNextRank().getId().equals(rank.getId()) ? "1" : "2"))), new String[]{
                        "{has}",
                        "{need}",
                        "{progress}"
                }, new String[]{
                        NumberFormatter.getInstance().format(has),
                        NumberFormatter.getInstance().format(need),
                        NumberFormatter.getInstance().formatDecimal(progress * 100)
                })));
            }

            requirements.sort(Collections.reverseOrder()); // fix reverse order

            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str + "." + status, new String[]{
                    "{requirements}",
                    "{status}"
            }, new String[]{
                    convertListToString(new ArrayList<>(requirements), "|"),
                    getColored(FileUtils.get().getString(file, "Lore-Filter." + status))
            }).build();
            int slot = Integer.parseInt(slots[i]);

            inventoryUtils.addAction(inventory, slot, null, InventoryUtils.ActionType.ALL_CLICKS); // cancel inventory click with null action

            builders.add(ItemBuilder.build(item, slot, null));
        }

        int nextPageSlot = FileUtils.get().getInt(file, "Inventory.next-page-slot");
        int previousPageSlot = FileUtils.get().getInt(file, "Inventory.previous-page-slot");

        InventoryBuilder.build(player, inventory, title, builders, nextPageSlot, previousPageSlot, nextPageItem, previousPageItem);
    }

    private String convertListToString(List<String> strList, String lineSeparator) {
        StringBuilder builder = new StringBuilder(strList.size());

        for (String str : strList) {
            builder.append(str).append(lineSeparator);
        }

        return builder.toString();
    }

    private String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}