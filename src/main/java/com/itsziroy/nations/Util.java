package com.itsziroy.nations;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class Util {

    public static boolean isOnlinePlayer(String name) {
        return Bukkit.getPlayer(name) != null;
    }

    public static void addPlayerToScoreboardTeam(Player player, String name) {
        Set<Team> teams = player.getScoreboard().getTeams();
        for (Team team : teams) {
            if (team.getName().equals(name)) {
                team.addEntry(player.getName());
            }
        }
    }


    public static Team getScoreboardTeamForPlayer(Player player) {
        Set<Team> teams = player.getScoreboard().getTeams();

        for (Team team : teams) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }
}
