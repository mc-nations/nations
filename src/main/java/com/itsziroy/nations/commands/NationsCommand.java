package com.itsziroy.nations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("nations|n")
public class NationsCommand extends BaseCommand {

    private final com.itsziroy.nations.Nations plugin;

    public NationsCommand(com.itsziroy.nations.Nations plugin) {
        this.plugin = plugin;
    }


    @Subcommand("tp|teleport")
    @CommandCompletion("@teams|@players @teams")
    public void onTeleport(Player player, String[] args) {
        String teamNameArg = args[0].replace("@", "");
        if(Util.isOnlinePlayer(args[0])) {
            player = Bukkit.getPlayer(args[0]);
            teamNameArg = args[1].replace("@", "");
        }

        ConfigurationSection teamBorders = plugin.getConfig().getConfigurationSection(Config.Path.TEAM_BORDERS_TEAMS);
        if (teamBorders != null) {
            for (String teamName : teamBorders.getKeys(false)) {
                if (Objects.equals(teamName, teamNameArg)) {
                    ConfigurationSection teamBorder = plugin.getConfig().getConfigurationSection(Config.Path.TEAM_BORDERS_TEAMS + "." + teamName);
                    if (teamBorder != null) {
                        if (player != null) {
                            double x = teamBorder.getDouble("x");
                            double z = teamBorder.getDouble("z");
                            double y = player.getWorld().getHighestBlockAt((int) x, (int) z).getY();
                            player.teleport(new Location(player.getWorld(), x, y, z));
                        }
                    }
                }
            }

        }
    }
}
