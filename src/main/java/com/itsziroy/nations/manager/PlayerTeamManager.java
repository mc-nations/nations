package com.itsziroy.nations.manager;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.util.PlayerTeam;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerTeamManager {

    private static final String COMMA_DELIMITER = ";";
    private final Nations plugin;

    private final List<PlayerTeam> playerTeams = new ArrayList<>();

    public PlayerTeamManager(Nations plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.plugin.getDataFolder() + "players.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                playerTeams.add(new PlayerTeam(values[0], values[1]));
            }
        } catch (Exception e) {

        }
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
}
