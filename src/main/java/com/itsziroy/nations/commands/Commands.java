package com.itsziroy.nations.commands;

import co.aikar.commands.CommandManager;
import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;

public final class Commands {

    public static void register(CommandManager<?, ?, ?, ?, ?, ?> manager, Nations plugin) {
        manager.registerCommand(new NationsCommand(plugin));
        boolean chatFormatEnabled = plugin.getConfig().getBoolean(Config.Path.Chat.enabled);
        if(chatFormatEnabled){
            manager.registerCommand(new GlobalChatCommand(plugin));
        }
    }
}
