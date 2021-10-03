package com.zpedroo.voltzrankup.commands;

import com.zpedroo.voltzrankup.managers.DataManager;
import com.zpedroo.voltzrankup.objects.PlayerData;
import com.zpedroo.voltzrankup.utils.config.Messages;
import com.zpedroo.voltzrankup.utils.formatter.NumberFormatter;
import com.zpedroo.voltzrankup.utils.menus.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;

public class RankUPCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (args.length >= 3) {
            switch (args[0].toUpperCase()) {
                case "ITEM":
                    if (!sender.hasPermission("rankup.admin")) break;

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) break;

                    BigInteger amount = NumberFormatter.getInstance().filter(args[2]);
                    if (amount.signum() <= 0) amount = BigInteger.ONE;
                    if (amount.compareTo(BigInteger.valueOf(2304)) > 0) amount = BigInteger.valueOf(2304);

                    ItemStack item = DataManager.getInstance().getCache().getRankUpItem();
                    item.setAmount(amount.intValue());

                    target.getInventory().addItem(item);
                    return true;
            }
        }

        Player player = (Player) sender;
        PlayerData data = DataManager.getInstance().load(player);
        if (data.getNextRank() == null) {
            player.sendMessage(Messages.LAST_RANK);
            return true;
        }

        if (data.getNextRank().getRequirements().isEmpty()) {
            player.sendMessage(Messages.NO_REQUIREMENTS);
            return true;
        }

        Menus.getInstance().openRankUPMenu(player);
        return false;
    }
}