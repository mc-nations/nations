package com.itsziroy.nations;

import org.bukkit.Bukkit;

public class Util {

    public static boolean isOnlinePlayer(String name) {
        return Bukkit.getPlayer(name) != null;
    }

}
