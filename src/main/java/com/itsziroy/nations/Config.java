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

        String EVENT_START_DISABLE_JOIN = "event_start.disable_join";
        String TEAM_BORDERS = "team_borders";
        String TEAM_BORDERS_TEAMS = "team_borders.teams";

        String TEAM_BORDERS_ENABLED = "team_borders.enabled";

        String SPAWN_EXLCUSION = "spawn_exclusion";
        String SPAWN_EXLCUSION_MAX_HEIGHT = "spawn_exclusion.max_height";
        String SPAWN_EXLCUSION_REGIONS = "spawn_exclusion.regions";


        String ENFORCE_TEAMS = "enforce_teams";
        String ENABLE_RANDOM_SPAWN = "enable_random_spawn";


    }


}
