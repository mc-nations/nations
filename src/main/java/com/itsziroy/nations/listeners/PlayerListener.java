package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.Permission;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class PlayerListener implements Listener {

    private final Nations plugin;

    public PlayerListener(Nations plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean(Config.Path.TEAM_BORDERS_ENABLED)) {
            if (!event.getPlayer().hasPermission(Permission.BYPASS_TEAM_BORDERS)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Player player = event.getPlayer();
                    Set<Team> teams = player.getScoreboard().getTeams();

                    Team playerTeam = null;
                    for (Team team : teams) {
                        if (team.hasEntry(player.getName())) {
                            playerTeam = team;
                        }
                    }


                    if (playerTeam != null) {
                        ConfigurationSection teamBorders = plugin.getConfig().getConfigurationSection(Config.Path.TEAM_BORDERS_TEAMS);
                        if (teamBorders != null) {
                            Set<String> teamNames = teamBorders.getKeys(false);
                            for (String teamName : teamNames) {
                                if (playerTeam.getName().equals(teamName)) {
                                    ConfigurationSection teamBorder = plugin.getConfig().getConfigurationSection(Config.Path.TEAM_BORDERS_TEAMS + "." + teamName);
                                    if (teamBorder != null) {
                                        WorldBorder worldBorder = Bukkit.createWorldBorder();
                                        double x = teamBorder.getDouble("x");
                                        double z = teamBorder.getDouble("z");
                                        double size = teamBorder.getDouble("size");

                                        plugin.getLogger().info("Setting world border for " + player.getName() + " to "
                                                + x + ", "
                                                + z + ", "
                                                + size + ".");

                                        worldBorder.setCenter(x, z);
                                        worldBorder.setSize(size);

                                        player.setWorldBorder(worldBorder);
                                    }

                                }
                            }
                        }
                    }
                }, 10);
            } else {
                plugin.getLogger().info("Player " + event.getPlayer().getName() + " has permission to bypass team borders.");
            }
        }
    }

}
