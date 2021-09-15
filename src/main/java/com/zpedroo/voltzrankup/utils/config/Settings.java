package com.zpedroo.voltzrankup.utils.config;

import com.zpedroo.voltzrankup.utils.FileUtils;

import java.util.List;

public class Settings {

    public static final String RANKS_COMMAND = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.ranks.command");

    public static final List<String> RANKS_ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.commands.ranks.aliases");

    public static final String RANKUP_COMMAND = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.rankup.command");

    public static final List<String> RANKUP_ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.commands.rankup.aliases");
}