package com.itsziroy.nations.manager;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.util.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.itsziroy.nations.Util.getScoreboardTeamForPlayer;

public class PlayerTeamManager {

    private final Nations plugin;

    private final List<PlayerTeam> playerTeams = new ArrayList<>();

    public PlayerTeamManager(Nations plugin) {
        this.plugin = plugin;
    }

    public void load() throws IOException {
        String csvPath = this.plugin.getConfig().getString(Config.Path.PLAYER_CSV_FILE);
        String delimiter = this.plugin.getConfig().getString(Config.Path.PLAYER_CSV_DELIMITER);
        try (BufferedReader br = new BufferedReader(new FileReader(this.plugin.getDataFolder() + "/" + csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                assert delimiter != null;
                String[] values = line.split(delimiter);
                playerTeams.add(new PlayerTeam(values[0], values[1]));
            }
        } catch (IOException e) {
            this.plugin.getLogger().warning("PlayerTeam File "+ csvPath + " could not be loaded!");
        }
    }

    public void reload() throws IOException {
        this.playerTeams.clear();
        this.load();
    }

    public List<PlayerTeam> getPlayerTeams() {
        return playerTeams;
    }

    public String getTeamForPlayer(UUID uuid) {
        return getTeamForPlayer(uuid.toString());
    }

    public String getTeamForPlayer(String uuid) {
        for(PlayerTeam playerTeam: playerTeams) {
            if(playerTeam.discordId().equals(uuid)) {
                return playerTeam.team();
            }
        }
        return null;
    }

    public void registerTeams() {
        ConfigurationSection teamsConfig = this.plugin.getConfig().getConfigurationSection(Config.Path.TEAMS);
        if(teamsConfig != null) {
            for (String team : teamsConfig.getKeys(false)) {
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                if (manager != null && manager.getMainScoreboard().getTeam(team) == null) {
                    Team scoreboardTeam = manager.getMainScoreboard().registerNewTeam(team);
                    String teamColor = this.plugin.getConfig().getString(Config.Path.Team.color(team));
                    if(teamColor != null) scoreboardTeam.setColor(ChatColor.valueOf(teamColor));
                }
            }
        }
    }

    public void setWorldBorderForPlayer(Player player) {
        Team playerTeam = getScoreboardTeamForPlayer(player);

        if (playerTeam != null) {
            ConfigurationSection teamBorder = plugin.getConfig().getConfigurationSection(Config.Path.Team.border(playerTeam.getName()));
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
