package com.zpedroo.voltzrankup.listeners;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.zpedroo.voltzrankup.managers.DataManager;
import com.zpedroo.voltzrankup.managers.RankManager;
import com.zpedroo.voltzrankup.objects.PlayerData;
import com.zpedroo.voltzrankup.utils.config.Messages;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        DataManager.getInstance().save(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatMessageEvent event) {
        if (!event.getTags().contains("rank")) return;

        PlayerData data = DataManager.getInstance().load(event.getSender());
        if (data.getRank() == null) return;

        event.setTagValue("rank", data.getRank().getTag());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getItem();
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("RankUpItem")) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerData data = DataManager.getInstance().load(player);
        if (data.getNextRank() == null) {
            player.sendMessage(Messages.LAST_RANK);
            return;
        }

        RankManager.getInstance().upgradeRank(player, false);

        item.setAmount(item.getAmount() - 1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 10f);
    }
}