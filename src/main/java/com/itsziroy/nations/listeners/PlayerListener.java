package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.Permission;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class PlayerListener implements Listener {

    private final Nations plugin;

    public PlayerListener(Nations plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoinSetWorldBorder(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean(Config.Path.TEAM_BORDERS_ENABLED)) {
            if (!event.getPlayer().hasPermission(Permission.BYPASS_TEAM_BORDERS)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Player player = event.getPlayer();
                    Team playerTeam = getTeamForPlayer(player);


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


    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String discordId = this.plugin.getDiscordSRV().getAccountLinkManager().getDiscordId(player.getUniqueId());

        String team = this.plugin.getPlayerTeamManager().getTeamForPlayer(discordId);

        if (team != null) {
            if (getTeamForPlayer(event.getPlayer()) != null) {

                ConfigurationSection teamBorder = plugin.getConfig().getConfigurationSection(Config.Path.TEAM_BORDERS_TEAMS + "." + team);

                double size = teamBorder.getDouble("size") / 2;

                double xMin = teamBorder.getDouble("x") - size;
                double xMax = teamBorder.getDouble("x") + size;
                double zMin = teamBorder.getDouble("z") - size;
                double zMax = teamBorder.getDouble("z") + size;

                double x = Math.random() * (xMax - xMin) + xMin;
                double z = Math.random() * (zMax - zMin) + zMin;
                int y = player.getWorld().getHighestBlockYAt((int) x, (int) z);


                // Spieler teleportieren
                player.teleport(new Location(player.getWorld(), x, y, z));

                // 5 gegartes Rindfleisch ins Inventar
                ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 5);
                player.getInventory().addItem(cookedBeef);

                // Buch erstellen
                ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta bookMeta = (BookMeta) book.getItemMeta();
                bookMeta.setTitle("Mein Buch");
                bookMeta.setAuthor("Dein Server");
                bookMeta.addPage(ChatColor.BLACK + "Hier Text einfügen");
                book.setItemMeta(bookMeta);

                player.getInventory().addItem(book);

            }
        } else {
            event.getPlayer().kickPlayer("KICK");
        }
    }

    public Team getTeamForPlayer(Player player) {
        Set<Team> teams = player.getScoreboard().getTeams();

        for (Team team : teams) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }
}
