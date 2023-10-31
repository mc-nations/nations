package com.itsziroy.nations.manager;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.util.PlayerTeam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
}
