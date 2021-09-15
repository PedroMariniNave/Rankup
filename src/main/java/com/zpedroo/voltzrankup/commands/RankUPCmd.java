package com.zpedroo.voltzrankup.commands;

import com.zpedroo.voltzrankup.managers.DataManager;
import com.zpedroo.voltzrankup.objects.PlayerData;
import com.zpedroo.voltzrankup.utils.config.Messages;
import com.zpedroo.voltzrankup.utils.menus.Menus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankUPCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        PlayerData data = DataManager.getInstance().load(player);
        if (data.getNextRank() == null) {
            player.sendMessage(Messages.LAST_RANK);
            return true;
        }

        Menus.getInstance().openRankUPMenu(player);
        return false;
    }
}