package com.itsziroy.nations.commands;

import co.aikar.commands.CommandManager;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public final class TabCompletions {

    public static void register(CommandManager<?, ?, ?, ?, ?, ?> manager, Nations plugin) {
        manager.getCommandCompletions().registerCompletion("teams", c -> {

            Set<String> completions = new HashSet<>();
            ConfigurationSection teams = plugin.getConfig().getConfigurationSection(Config.Path.TEAMS);
            if (teams != null) {
                completions.addAll(teams.getKeys(false).stream().map(s -> "@" + s).toList());
            }
            return completions;
        });
    }
}
