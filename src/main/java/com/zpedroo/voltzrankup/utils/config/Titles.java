package com.zpedroo.voltzrankup.utils.config;

import com.zpedroo.voltzrankup.utils.FileUtils;
import org.bukkit.ChatColor;

public class Titles {

    public static final String RANKUP_TITLE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.rankup.title"));

    public static final String RANKUP_SUBTITLE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.rankup.subtitle"));

    private static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}