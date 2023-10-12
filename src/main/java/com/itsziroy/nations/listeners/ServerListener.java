package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.servertimelock.ServerTimeLockPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;


public class ServerListener implements Listener {

    private final Nations plugin;

    public ServerListener(Nations plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {

        Boolean enabled = this.plugin.getConfig().getBoolean(Config.Path.MOTD_COUNTDOWN);

        if(enabled) {
            String date = this.plugin.getConfig().getString(Config.Path.EVENT_START_DATE);
            String time = this.plugin.getConfig().getString(Config.Path.EVENT_START_TIME);
            if (date != null && time != null) {
                Calendar calendar = Calendar.getInstance();
                Calendar currentCalendar = Calendar.getInstance();


                int[] dateArray = Arrays.stream(date.split("-")).mapToInt(Integer::parseInt).toArray();
                int[] timeArray =  Arrays.stream(time.split(":")).mapToInt(Integer::parseInt).toArray();

                calendar.set(Calendar.YEAR, dateArray[0]);
                calendar.set(Calendar.MONTH, dateArray[1] - 1);
                calendar.set(Calendar.DAY_OF_MONTH, dateArray[2]);
                calendar.set(Calendar.HOUR_OF_DAY, timeArray[0]);
                calendar.set(Calendar.MINUTE, timeArray[1]);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long diff = calendar.getTimeInMillis() - currentCalendar.getTimeInMillis();

                if(diff < 0) {
                    Duration duration = Duration.ofMillis(diff);

                    event.setMotd(ChatColor.DARK_PURPLE + "Minecraft Nations " + ChatColor.GRAY + "startet in " + durationToFormattedString(duration) + ".");
                } else {
                    ServerTimeLockPlugin serverTimeLock = plugin.getServerTimeLockPlugin();
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
