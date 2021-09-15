package com.zpedroo.voltzrankup;

import com.zpedroo.voltzrankup.commands.RankUPCmd;
import com.zpedroo.voltzrankup.commands.RanksCmd;
import com.zpedroo.voltzrankup.hooks.PlaceholderAPIHook;
import com.zpedroo.voltzrankup.hooks.VaultHook;
import com.zpedroo.voltzrankup.listeners.PlayerGeneralListeners;
import com.zpedroo.voltzrankup.managers.DataManager;
import com.zpedroo.voltzrankup.managers.RankManager;
import com.zpedroo.voltzrankup.mysql.DBConnection;
import com.zpedroo.voltzrankup.utils.FileUtils;
import com.zpedroo.voltzrankup.utils.formatter.NumberFormatter;
import com.zpedroo.voltzrankup.utils.menus.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

import static com.zpedroo.voltzrankup.utils.config.Settings.*;

public class VoltzRankUP extends JavaPlugin {

    private static VoltzRankUP instance;
    public static VoltzRankUP get() { return instance; }

    public void onEnable() {
        instance = this;
        new FileUtils(this);

        if (!isMySQLEnabled(getConfig())) {
            getLogger().log(Level.SEVERE, "MySQL are disabled! You need to enable it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new DBConnection(getConfig());
        new NumberFormatter(getConfig());
        new DataManager();
        new RankManager();
        new VaultHook().hook();
        new PlaceholderAPIHook(this).register();
        new Menus();

        registerCommands();
        registerListeners();
    }

    public void onDisable() {
        if (!isMySQLEnabled(getConfig())) return;

        try {
            DataManager.getInstance().saveAll();
            DBConnection.getInstance().closeConnection();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An error occurred while trying to save data!");
            ex.printStackTrace();
        }
    }

    private void registerCommands() {
        registerCommand(RANKS_COMMAND, RANKS_ALIASES, new RanksCmd());
        registerCommand(RANKUP_COMMAND, RANKUP_ALIASES, new RankUPCmd());
    }

    private void registerCommand(String command, List<String> aliases, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCmd = constructor.newInstance(command, this);
            pluginCmd.setAliases(aliases);
            pluginCmd.setExecutor(executor);

            Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            commandMap.register(getName().toLowerCase(), pluginCmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerGeneralListeners(), this);
    }

    private Boolean isMySQLEnabled(FileConfiguration file) {
        if (!file.contains("MySQL")) return false;

        return file.getBoolean("MySQL.enabled");
    }
}