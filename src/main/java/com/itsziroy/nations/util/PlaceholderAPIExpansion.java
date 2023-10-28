package com.itsziroy.nations.util;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    private Nations plugin;

    public PlaceholderAPIExpansion(Nations plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "mc-nations";
    }

    @Override
    public String getIdentifier() {
        return "nations";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }


    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {

        if (params.equalsIgnoreCase("prefix")) {
            Player player = offlinePlayer.getPlayer();


            if (player == null) return null;
            Set<Team> teams = player.getScoreboard().getTeams();

            for (Team team : teams) {
                if (team.hasEntry(player.getName())) {
                    String prefix = plugin.getConfig().getString(Config.Path.Team.prefix(team.getName()));
                    return team.getColor() +  prefix;
                }
            }
            return ""; // Player is in no team
        }

        return null; // Placeholder is unknown by the Expansion
    }

}
