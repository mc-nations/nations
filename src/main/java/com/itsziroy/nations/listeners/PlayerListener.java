package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import com.itsziroy.nations.Permission;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.itsziroy.nations.Util.addPlayerToScoreboardTeam;
import static com.itsziroy.nations.Util.getScoreboardTeamForPlayer;

public class PlayerListener implements Listener {

    private final Nations plugin;

    public PlayerListener(Nations plugin) {
        this.plugin = plugin;
    }


    @EventHandler()
    public void onPlayerJoinSetWorldBorder(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean(Config.Path.TEAM_BORDERS_ENABLED)) {
            if (!event.getPlayer().hasPermission(Permission.BYPASS_TEAM_BORDERS)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getPlayerTeamManager().setWorldBorderForPlayer(event.getPlayer()), 10);
            } else {
                plugin.getLogger().info("Player " + event.getPlayer().getName() + " has permission to bypass team borders.");
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String discordId = this.plugin.getDiscordSRV().getAccountLinkManager().getDiscordId(player.getUniqueId());

        String team = this.plugin.getPlayerTeamManager().getTeamForPlayer(discordId);
        Bukkit.getLogger().info("Player " + player.getName() + " joined with discord id " + discordId + " and team " + team);
        if (team != null) {
            if (getScoreboardTeamForPlayer(player) == null) {

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> addPlayerToScoreboardTeam(player, team), 10);

                if (!event.getPlayer().hasPermission(Permission.BYPASS_TEAM_BORDERS)) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getPlayerTeamManager().setWorldBorderForPlayer(player), 20);
                } else {
                    plugin.getLogger().info("Player " + event.getPlayer().getName() + " has permission to bypass team borders.");
                }
                if (this.plugin.getConfig().getBoolean(Config.Path.ENABLE_RANDOM_SPAWN)) {
                    if (!event.getPlayer().hasPermission(Permission.BYPASS_RANDOM_SPAWN)) {
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                            this.plugin.getLogger().info("Teleporting player " + player.getName() + " to random spawn.");
                            Location spawnLocation = findSafeSpawnLocation(player, team);
                            // Spieler teleportieren
                            player.teleport(spawnLocation);
                            player.setBedSpawnLocation(spawnLocation, true);

                            // 5 gegartes Rindfleisch ins Inventar
                            ItemStack cookedBeef = new ItemStack(Material.COOKED_BEEF, 5);
                            player.getInventory().addItem(cookedBeef);

                            // Buch erstellen
                            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                            BookMeta bookMeta = (BookMeta) book.getItemMeta();
                            bookMeta.setTitle("Minecraft Nations");
                            bookMeta.setAuthor("Dein Server");
                            bookMeta.addPage(ChatColor.DARK_PURPLE + "Willkommen bei Minecraft Nations!" +
                                    "\n\n" + ChatColor.BLACK +
                                    "Diese Welt ist nicht ungef\u00e4hrlich. Hier ein paar Informationen, f\u00dcr den Fall, dass du noch keine gro\u00DFen Erfahrungen mit dem Spiel Minecraft gemacht hast:\n" +
                                            "Such dir am besten eine Gruppe, wo du",
                                    "erstmal mitmachen kannst, um deine \u00dcberlebenschancen zu erh\u00f6hen. " +
                                            "Ohne Essen regenerierst du keine Herzen. " +
                                            "Wir spielen anfangs auf der Schwierigkeit Mittel und nach 2 Tagen auf Hard! " +
                                            "Bitte beachte, dass die Mobs in H\u00f6hlen und bei Nacht sehr gef\u00e4hrlich sein ",
                                            "k\u00f6nnen. Ein Schild ist dein bester Freund um Pfeile und andere Attacken von Mobs zu blocken. \n\n" +
                                            "PROXIMITY CHAT IST PFLICHT ZU NUTZEN. \n\n" +
                                            "Wir w\u00fcnschen dir viel Erfolg und eine abenteuerliche Zeit bei dem Projekt Nations! ",
                                    ChatColor.DARK_PURPLE + "Lore\n\n" + ChatColor.BLACK +
                                    "In den Tiefen der Minecraft-Welt existierten einst drei antike, mythische Nationen: "+
                                            "die Nation des Schnees, die Nation der W\u00fcste und die Nation des Dschungels. "+
                                            "Diese Nationen lebten im Einklang mit einer \u00fcbernat\u00fcrlichen kosmischen Macht, die das Gleichgewicht in ihrer Welt aufrechterhielt",
                                    "Die Nation des Schnees erstreckte sich \u00fcber endlose verschneite Ebenen und majest\u00e4tische Berge. "+
                                    "Ihre Bewohner waren bekannt f\u00fcr ihre Weisheit und ihre F\u00e4higkeit, die Mysterien des Eises zu beherrschen. Die Nation der W\u00fcste bl\u00fchte inmitten riesiger",
                                    "Sandd\u00fcnen und versteckter Oasen. Sie waren geschickte Handwerker und H\u00e4ndler, die die W\u00fcstenressourcen meisterhaft nutzten. Die Nation des Dschungels erstreckte sich durch undurchdringliche W\u00e4lder, in denen die Natur in voller Pracht erbl\u00fchte. " +
                                            "Jede Nation hatte ihre einzigartige Kultur und",
                                    "Traditionen, aber sie alle teilten eine tiefe Verehrung f\u00fcr die kosmische Macht der Titanen, die \u00fcber ihre Welt wachte. Dies sorgte f\u00fcr Harmonie zwischen den Nationen und sch\u00fctzte sie vor Bedrohungen von au\u00DFen.\n" +
                                            "Jedoch hielt diese Harmonie nicht lange ",
                                    "und unter Einfluss einer dunklen Macht, welche Zwietracht zwischen den Nationen s\u00e4te, versank die Welt im Chaos. " +
                                            "Mithilfe der Titanen vereinten sich die Nationen und k\u00e4mpften Seite an Seite miteinander, zusammen mit den Titanen, um in einem finalen Kampf schlie\u00DFlich",
                                    "die dunkle Macht, das sogenannte \"Wirr\", in einem Berg der Schneenation zu versiegeln. " +
                                            "Nach so langer Zeit sind \u00dcberreste der antiken Nationen und die Spuren des Krieges kaum noch zu finden. " +
                                            "Allerdings munkelt man, ob das Wirr wirklich komplett",
                                    "versiegelt wurde und es nicht irgendwo noch sein Unwesen treibt. \n" +
                                            "Und so wurden die drei Nationen zu Legenden, die in Erz\u00e4hlungen der Nachfahren weiterlebten, als ein Beispiel f\u00fcr Einheit und Zusammenarbeit in Zeiten der Dunkelheit. \n",
                                            ChatColor.BOLD + "" + ChatColor.RED + " \n\n\nAber wird diese Einheit bestehen bleiben?"

                                   );
                            book.setItemMeta(bookMeta);

                            player.getInventory().addItem(book);
                        }, 10);

                    } else {
                        plugin.getLogger().info("Player " + player.getName() + " bypasses random spawns.");
                    }
                } else {
                    plugin.getLogger().info("Random spawns are disabled.");
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerLoginEvent event) {

        // Check if event started or not
        if(!event.getPlayer().hasPermission(Permission.BYPASS_EVENT_START)) {
            if (this.plugin.getConfig().getBoolean(Config.Path.EVENT_START_DISABLE_JOIN)) {
                if (this.plugin.getEventStartDate() != null) {
                    long diff = this.plugin.getEventStartDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                    if (diff > 0) {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Das Event ist noch nicht gestartet!");
                        return;
                    }
                }
            }
        }

        // Check if we are enforcing teams
        if(this.plugin.getConfig().getBoolean(Config.Path.ENFORCE_TEAMS)) {
            Player player = event.getPlayer();
            if(!player.hasPermission(Permission.BYPASS_ENFORCE_TEAMS)) {
                String discordId = this.plugin.getDiscordSRV().getAccountLinkManager().getDiscordId(player.getUniqueId());

                String team = this.plugin.getPlayerTeamManager().getTeamForPlayer(discordId);
                if (team == null) {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Dir wurde noch kein Biom zugewiesen. Bitte wende dich an einen Server Administrator wenn dieser Fehler weiterhin auftritt.");
                }
            } else {
                plugin.getLogger().info("Player " + player.getName() + " has Permission to join without Team");
            }
        }
    }

    private Location findSafeSpawnLocation(Player player, String team) {


        ConfigurationSection teamBorder = plugin.getConfig().getConfigurationSection(Config.Path.Team.border(team));
        ConfigurationSection spawn_exlusion = plugin.getConfig().getConfigurationSection(Config.Path.SPAWN_EXLCUSION );


        double size = teamBorder.getDouble("size") / 2;

        double xMin = teamBorder.getDouble("x") - size;
        double xMax = teamBorder.getDouble("x") + size;
        double zMin = teamBorder.getDouble("z") - size;
        double zMax = teamBorder.getDouble("z") + size;

        double x = Math.random() * (xMax - xMin) + xMin;
        double z = Math.random() * (zMax - zMin) + zMin;
        int y = player.getWorld().getHighestBlockYAt((int) x, (int) z);

        // nicht im vulkan im dchungel spawnen
        if(spawn_exlusion != null) {
            List<Map<?, ?>> regions = spawn_exlusion.getMapList("regions");
            for(Map<?,?> region: regions) {

                int region_size = (int) region.get("size");

                double ex_xMin = (int) region.get("x") - region_size;
                double ex_xMax = (int) region.get("x") + region_size;
                double ex_zMin = (int) region.get("z") - region_size;
                double ex_zMax = (int) region.get("z") + region_size;
                if (x > ex_xMin && x < ex_xMax && z > ex_zMin && z < ex_zMax) {
                    return findSafeSpawnLocation(player, team);
                }
            }
            int max_height = plugin.getConfig().getInt(Config.Path.SPAWN_EXLCUSION_MAX_HEIGHT + "." + team);
            //nicht \u00dcber max height spawnen
            if(max_height != 0  && y+1 > max_height) {
                return findSafeSpawnLocation(player, team);
            }
        }
        //nicht in wasser/lava/ice/powder snow spawnen
        this.plugin.getLogger().info("Checking if " + x + ", " + y +1  + ", " + z + " is safe.");
        Block block = player.getWorld().getBlockAt((int) x, y+1 , (int) z);
        Block block_below = player.getWorld().getBlockAt((int) x, y , (int) z);
        plugin.getLogger().info("Block: " + block.getType() + " Block below: " + block_below.getType());
        Material block_below_type = block_below.getType();
        if (block.isLiquid()
                || block.getType() == Material.POWDER_SNOW
                || block_below.isLiquid()
                || block_below_type == Material.POWDER_SNOW
                || block_below_type == Material.ICE
                || block_below_type == Material.PACKED_ICE
                || block_below_type == Material.FROSTED_ICE
                || block_below_type == Material.BLUE_ICE
                || block_below_type == Material.KELP
        ) {
            return findSafeSpawnLocation(player, team);
        }
        if(block.getType() == Material.SNOW) {
           y = y +1;
        }

        return new Location(player.getWorld(), x, y+1, z);
    }
}
