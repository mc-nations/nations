package com.itsziroy.nations;

public interface Config {

    interface Path {
        String GAME_RULES = "game_rules";
        String GAME_RULES_ENABLED = "game_rules.enabled";
        String MOTD_COUNTDOWN = "motd_countdown";
        String EVENT_START = "event_start";
        String EVENT_START_DATE = "event_start.date";
        String EVENT_START_TIME = "event_start.time";

        String TEAMS = "teams";

        interface Team {
            static String color(String teamName) {
                return "teams." + teamName + ".color";
            }

            static String border(String teamName) {
                return "teams." + teamName + "." + "border";
            }

            static String prefix(String teamName) {
                return "teams." + teamName + "." + "prefix";
            }
        }

        String TEAM_BORDERS_ENABLED = "team_borders";

        String EVENT_START_DISABLE_JOIN = "event_start.disable_join";


        String SPAWN_EXLCUSION = "spawn_exclusion";
        String SPAWN_EXLCUSION_MAX_HEIGHT = "spawn_exclusion.max_height";
        String SPAWN_EXLCUSION_REGIONS = "spawn_exclusion.regions";


        String ENFORCE_TEAMS = "enforce_teams";
        String ENABLE_RANDOM_SPAWN = "enable_random_spawn";

        String DISABLE_MENDING = "disable_mending";


    }


}
