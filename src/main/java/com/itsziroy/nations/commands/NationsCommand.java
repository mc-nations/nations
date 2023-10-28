package com.itsziroy.nations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Util;
import com.itsziroy.nations.util.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.UUID;

@CommandAlias("nations|n")
@CommandPermission("nations.default")
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

        ConfigurationSection teams = plugin.getConfig().getConfigurationSection(Config.Path.TEAMS);
        if (teams != null) {
            for (String teamName : teams.getKeys(false)) {
                if (Objects.equals(teamName, teamNameArg)) {
                    ConfigurationSection teamBorder = plugin.getConfig().getConfigurationSection(Config.Path.Team.border(teamName));
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

    @Subcommand("teams|t")
    public class TeamCommands extends BaseCommand {
        @Subcommand("reload")
        public void onReload(Player player) {
            try {
                plugin.getPlayerTeamManager().reload();
                player.sendMessage(ChatColor.GREEN + "Successfully reloaded Player Teams.");
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Reload failed.");
            }

        }

        @Subcommand("info")
        public void onTeamsInfo(Player player) {
            StringBuilder message = new StringBuilder();
            for(PlayerTeam playerTeam: plugin.getPlayerTeamManager().getPlayerTeams()) {
                UUID uuid = plugin.getDiscordSRV().getAccountLinkManager().getUuid(playerTeam.discordId());
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
                message.append(" - ").append(offlinePlayer.getName()).append(";").append(playerTeam.team());
            }
            player.sendMessage(message.toString());
        }

        @Subcommand("remove")
        @CommandCompletion("@teams|all")
        public void onRemoveAllTeams(Player player, String[] args) {

            if(args[0].equals("all")) {
                ConfigurationSection teams = plugin.getConfig().getConfigurationSection(Config.Path.TEAMS);
                if(teams != null) {
                    for (String scoreBoardTeam : teams.getKeys(false)) {
                        Team team = player.getScoreboard().getTeam(scoreBoardTeam);
                        if (team != null) {
                            team.unregister();
                        }
                    }
                    player.sendMessage(ChatColor.GREEN + "Removed all teams!");
                } else {
                    player.sendMessage(ChatColor.RED + "Removal failed.");
                }
            } else {
                Team team = player.getScoreboard().getTeam(args[0].replace("@", ""));
                if(team != null) {
                    team.unregister();
                    player.sendMessage(ChatColor.GREEN +"Removed " + args[0].replace("@", ""));
                } else {
                    player.sendMessage(ChatColor.RED +"Team not found!");
                }
            }
        }
    }
}
