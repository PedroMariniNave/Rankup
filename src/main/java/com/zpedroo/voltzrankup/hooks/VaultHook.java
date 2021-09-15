package com.zpedroo.voltzrankup.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigInteger;

public class VaultHook {

    private static Economy economy;

    public void hook() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;

        economy = rsp.getProvider();
    }

    public static BigInteger getMoney(Player player) {
        return new BigInteger(String.format("%.0f", economy.getBalance(player)));
    }

    public static void removeMoney(Player player, BigInteger amount) {
        economy.withdrawPlayer(player.getName(), player.getWorld().getName(), amount.doubleValue());
    }
}