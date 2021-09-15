package com.zpedroo.voltzrankup.utils.config;

import com.zpedroo.voltzrankup.utils.FileUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    public static final List<String> INSUFFICIENT_REQUIREMENTS = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.insufficient-requirements"));

    public static final String LAST_RANK = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.last-rank"));

    private static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private static List<String> getColored(List<String> list) {
        List<String> colored = new ArrayList<>(list.size());
        for (String str : list) {
            colored.add(getColored(str));
        }

        return colored;
    }
}