package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.servertimelock.ServerTimeLock;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.time.Duration;
import java.util.Calendar;


public class ServerListener implements Listener {

    private final Nations plugin;

    public ServerListener(Nations plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {

        boolean enabled = this.plugin.getConfig().getBoolean(Config.Path.MOTD_COUNTDOWN);

        if(enabled) {
            Calendar eventStartDate = this.plugin.getEventStartDate();
            if (eventStartDate != null) {

                Calendar currentCalendar = Calendar.getInstance();


                long diff = eventStartDate.getTimeInMillis() - currentCalendar.getTimeInMillis();

                if(diff > 0) {
                    Duration duration = Duration.ofMillis(diff);

                    event.setMotd(ChatColor.DARK_PURPLE + "Minecraft Nations " + ChatColor.GRAY + "startet in " + durationToFormattedString(duration) + ".");
                } else {
                    ServerTimeLock serverTimeLock = plugin.getServerTimeLockPlugin();
                    if(serverTimeLock.isLocked()) {
                        Duration duration = Duration.ofSeconds(serverTimeLock.getRemainingCloseTime());


                        event.setMotd(ChatColor.GRAY + "Server \u00F6ffnet wieder in " + durationToFormattedString(duration)+ ".");
                    } else {
                        Duration duration = Duration.ofSeconds(serverTimeLock.getRemainingTime());


                        event.setMotd(ChatColor.GRAY + "Willkommen bei Minecraft Nations! Der Server schlie\u00DFt wieder in " + durationToFormattedString(duration) + ".");
                    }
                }
            }
        }
    }

    public String durationToFormattedString(Duration duration) {
        return ""+ ChatColor.AQUA +
                duration.toDaysPart() + ChatColor.GRAY + (duration.toDaysPart() == 1 ? " Tag, " : " Tagen, ")
                + ChatColor.AQUA + duration.toHoursPart()  + ChatColor.GRAY + (duration.toHoursPart() == 1 ? " Stunde und " : " Stunden und ")
                + ChatColor.AQUA +  duration.toMinutesPart() + ChatColor.GRAY + (duration.toMinutesPart() == 1 ? " Minute " : " Minuten");
    }
}
