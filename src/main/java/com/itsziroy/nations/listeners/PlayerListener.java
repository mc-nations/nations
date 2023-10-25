package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.Permission;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;
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
        Bukkit.getLogger().info("Player " + player.getName() + " joined with discord id " + discordId + " and team " + team);
        if (team != null) {
            if (getTeamForPlayer(event.getPlayer()) != null) {
                Location spawnLocation = findSafeSpawnLocation(player, team);
                // Spieler teleportieren
                player.teleport(spawnLocation);

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

    private Location findSafeSpawnLocation(Player player, String team) {


        ConfigurationSection teamBorder = plugin.getConfig().getConfigurationSection(Config.Path.TEAM_BORDERS_TEAMS + "." + team);
        ConfigurationSection spawn_exlusion = plugin.getConfig().getConfigurationSection(Config.Path.SPAWN_EXLCUSION );


        double size = teamBorder.getDouble("size") / 2;

        double xMin = teamBorder.getDouble("x") - size;
        double xMax = teamBorder.getDouble("x") + size;
        double zMin = teamBorder.getDouble("z") - size;
        double zMax = teamBorder.getDouble("z") + size;

        double x = Math.random() * (xMax - xMin) + xMin;
        double z = Math.random() * (zMax - zMin) + zMin;
        int y = player.getWorld().getHighestBlockYAt((int) x, (int) z);

        // nicht im vulkan im dchungel spawnen
        if(spawn_exlusion != null) {
            List<Map<?, ?>> regions = spawn_exlusion.getMapList("regions");
            for(Map<?,?> region: regions) {

                int region_size = (int) region.get("size");

                double ex_xMin = (int) region.get("x") - region_size;
                double ex_xMax = (int) region.get("x") + region_size;
                double ex_zMin = (int) region.get("z") - region_size;
                double ex_zMax = (int) region.get("z") + region_size;
                if (x > ex_xMin && x < ex_xMax && z > ex_zMin && z < ex_zMax) {
                    return findSafeSpawnLocation(player, team);
                }
            }
            int max_height = plugin.getConfig().getInt(Config.Path.SPAWN_EXLCUSION_MAX_HEIGHT + "." + team);
            //nicht über max height spawnen
            if(max_height != 0  && y+1 > max_height) {
                return findSafeSpawnLocation(player, team);
            }
        }
        //nicht in wasser/lava/ice/powder snow spawnen
        this.plugin.getLogger().info("Checking if " + x + ", " + y +1  + ", " + z + " is safe.");
        Block block = player.getWorld().getBlockAt((int) x, y+1 , (int) z);
        Block block_below = player.getWorld().getBlockAt((int) x, y , (int) z);
        plugin.getLogger().info("Block: " + block.getType() + " Block below: " + block_below.getType());
        Material block_below_type = block_below.getType();
        if (block.isLiquid()
                || block.getType() == Material.POWDER_SNOW
                || block_below.isLiquid()
                || block_below_type == Material.POWDER_SNOW
                || block_below_type == Material.ICE
                || block_below_type == Material.PACKED_ICE
                || block_below_type == Material.FROSTED_ICE
                || block_below_type == Material.BLUE_ICE
                || block_below_type == Material.KELP
        ) {
            return findSafeSpawnLocation(player, team);
        }
        if(block.getType() == Material.SNOW) {
           y = y +1;
        }

        return new Location(player.getWorld(), x, y+1, z);
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
