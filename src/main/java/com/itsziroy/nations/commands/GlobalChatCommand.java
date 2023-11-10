package com.itsziroy.nations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Util;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

@CommandAlias("global|g|all|a")
@CommandPermission("nations.basic")
public class GlobalChatCommand extends BaseCommand {
    private final com.itsziroy.nations.Nations plugin;

    public GlobalChatCommand(com.itsziroy.nations.Nations plugin) {
        this.plugin = plugin;
    }


    @Default
    public void onGlobalChat(Player sender, String message) {
        boolean chatFormatEnabled = this.plugin.getConfig().getBoolean(Config.Path.Chat.enabled);
        if(!chatFormatEnabled) return;
        String message_text = message;

        Team team = Util.getScoreboardTeamForPlayer(sender);
        String message_prefix = this.plugin.getConfig().getString(Config.Path.Chat.global);

        message_prefix = PlaceholderAPI.setPlaceholders(sender,message_prefix);
        message_prefix =  message_prefix.replace("%message%", message_text).replace("%displayname%", sender.getDisplayName()).replace("%team_color%", team.getColor().toString()).replace("%team_name%", team.getDisplayName());


        String finalMessage_prefix = message_prefix;
        this.plugin.getServer().getOnlinePlayers().forEach(p -> {
            if (p != null) {
                p.sendMessage(finalMessage_prefix);
            }
        });

    }
}
