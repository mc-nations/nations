package com.itsziroy.nations;

import com.itsziroy.nations.listeners.PlayerListener;
import com.itsziroy.nations.listeners.ServerListener;
import com.itsziroy.servertimelock.ServerTimeLockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Nations extends JavaPlugin {


    private ServerTimeLockPlugin serverTimeLockPlugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        serverTimeLockPlugin = (ServerTimeLockPlugin) Bukkit.getPluginManager().getPlugin("ServerTimeLock");

        if(serverTimeLockPlugin == null) {
            this.setEnabled(false);
        }

        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        registerConfig();

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

    public ServerTimeLockPlugin getServerTimeLockPlugin() {
        return serverTimeLockPlugin;
    }
}
