package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.Util;
import com.itsziroy.nations.manager.PlayerTeamManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

public class ChatListener implements Listener {
    private final Nations plugin;

    public ChatListener(Nations plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        boolean chatFormatEnabled = this.plugin.getConfig().getBoolean(Config.Path.Chat.enabled);
        if(!chatFormatEnabled) return;
        if(event.isCancelled()) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        Team team = Util.getScoreboardTeamForPlayer(player);

        String message_text = event.getMessage();

        String message_prefix = this.plugin.getConfig().getString(Config.Path.Chat.team);

        message_prefix = PlaceholderAPI.setPlaceholders(player,message_prefix);
        message_prefix =  message_prefix.replace("%message%", message_text).replace("%displayname%", player.getDisplayName()).replace("%team_color%", team.getColor().toString()).replace("%team_name%", team.getDisplayName());


        String finalMessage_prefix = message_prefix;
        team.getEntries().forEach(entry -> {
            Player p = this.plugin.getServer().getPlayer(entry);
            if (p != null) {
                p.sendMessage(finalMessage_prefix);
            }
        });
    }
}
