package com.itsziroy.nations;

import co.aikar.commands.PaperCommandManager;
import com.itsziroy.nations.commands.Commands;
import com.itsziroy.nations.commands.TabCompletions;
import com.itsziroy.nations.listeners.PlayerListener;
import com.itsziroy.nations.listeners.ServerListener;
import com.itsziroy.nations.manager.PlayerTeamManager;
import com.itsziroy.servertimelock.ServerTimeLock;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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

        playerTeamManager.load();



        registerConfig();
        setGameRules();

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
}
