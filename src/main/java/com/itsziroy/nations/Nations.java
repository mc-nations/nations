package com.itsziroy.nations;

import co.aikar.commands.PaperCommandManager;
import com.itsziroy.nations.commands.Commands;
import com.itsziroy.nations.commands.TabCompletions;
import com.itsziroy.nations.listeners.PlayerListener;
import com.itsziroy.nations.listeners.ServerListener;
import com.itsziroy.nations.manager.PlayerTeamManager;
import com.itsziroy.nations.util.PlaceholderAPIExpansion;
import com.itsziroy.servertimelock.ServerTimeLock;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Nations extends JavaPlugin {


    private ServerTimeLock serverTimeLockPlugin;

    private DiscordSRV discordSRV;

    private final PlayerTeamManager playerTeamManager = new PlayerTeamManager(this);

    @Override
    public void onEnable() {
        // Plugin startup logic
        serverTimeLockPlugin = (ServerTimeLock) Bukkit.getPluginManager().getPlugin("ServerTimeLock");
        discordSRV = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if(serverTimeLockPlugin == null || discordSRV == null) {
            this.setEnabled(false);
            return;
        }


        // Regster Command and Tab Completion
        PaperCommandManager manager = new PaperCommandManager(this);

        Commands.register(manager, this);
        TabCompletions.register(manager, this);


        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);


        try {
            playerTeamManager.load();
        } catch (IOException e) {
            getLogger().warning("Player file could not be loaded!");
            throw new RuntimeException(e);
        }




        registerConfig();
        setGameRules();
        registerTeams();

        // Small check to make sure that PlaceholderAPI is installed
        this.getLogger().info("helllllo");
        this.getLogger().info(Bukkit.getPluginManager().getPlugin("PlaceholderAPI").toString());
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hooked into Placeholder API.");
            new PlaceholderAPIExpansion(this).register();
        }

    }

    public DiscordSRV getDiscordSRV() {
        return discordSRV;
    }


    public PlayerTeamManager getPlayerTeamManager() {
        return playerTeamManager;
    }


    public void registerConfig(){
        File config = new File(this.getDataFolder(), "config.yml");
        if(!config.exists()){
            this.getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ServerTimeLock getServerTimeLockPlugin() {
        return serverTimeLockPlugin;
    }

    public void setGameRules() {
        if(this.getConfig().getBoolean(Config.Path.GAME_RULES_ENABLED)) {
            ConfigurationSection gameRulesConfig = this.getConfig().getConfigurationSection(Config.Path.GAME_RULES);
            if (gameRulesConfig != null) {
                Set<String> worlds = gameRulesConfig.getKeys(false);
                for (String worldName : worlds) {
                    World world = Bukkit.getWorld(worldName);
                    ConfigurationSection gameRules = this.getConfig().getConfigurationSection(Config.Path.GAME_RULES + "." + worldName);
                    if (gameRules != null) {
                        Set<String> gameRuleKeys = gameRules.getKeys(false);
                        for (String gameRuleKey : gameRuleKeys) {
                            String gameRuleValue = gameRules.getString(gameRuleKey);
                            GameRule<?> gameRule = GameRule.getByName(gameRuleKey);
                            if (gameRule != null) {
                                if (gameRuleValue != null) {
                                    getLogger().info("Setting Game Rule " +
                                            gameRule.getName()  + " for "
                                            + worldName  + " to "
                                            + gameRuleValue );
                                    world.setGameRuleValue(gameRuleKey, gameRuleValue);
                                }

                            }
                        }

                    }
                }
            }
        }
    }

    public Calendar getEventStartDate() {
        String date = this.getConfig().getString(Config.Path.EVENT_START_DATE);
        String time = this.getConfig().getString(Config.Path.EVENT_START_TIME);
        if (date != null && time != null) {
            Calendar calendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();


            int[] dateArray = Arrays.stream(date.split("-")).mapToInt(Integer::parseInt).toArray();
            int[] timeArray = Arrays.stream(time.split(":")).mapToInt(Integer::parseInt).toArray();

            calendar.set(Calendar.YEAR, dateArray[0]);
            calendar.set(Calendar.MONTH, dateArray[1] - 1);
            calendar.set(Calendar.DAY_OF_MONTH, dateArray[2]);
            calendar.set(Calendar.HOUR_OF_DAY, timeArray[0]);
            calendar.set(Calendar.MINUTE, timeArray[1]);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            return calendar;
        } else {
            return null;
        }
    }

    public void registerTeams() {
        ConfigurationSection teamsConfig = this.getConfig().getConfigurationSection(Config.Path.TEAMS);
        if(teamsConfig != null) {
            for (String team : teamsConfig.getKeys(false)) {
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                if (manager != null && manager.getMainScoreboard().getTeam(team) == null) {
                    Team scoreboardTeam = manager.getMainScoreboard().registerNewTeam(team);
                    String teamColor = this.getConfig().getString(Config.Path.Team.color(team));
                    if(teamColor != null) scoreboardTeam.setColor(ChatColor.valueOf(teamColor));
                }
            }
        }
    }
}
