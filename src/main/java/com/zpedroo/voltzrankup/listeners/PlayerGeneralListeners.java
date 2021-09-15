package com.zpedroo.voltzrankup.listeners;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.zpedroo.voltzrankup.managers.DataManager;
import com.zpedroo.voltzrankup.objects.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
}