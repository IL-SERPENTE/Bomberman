package net.samagames.bomberman;

import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.Status;
import net.samagames.bomberman.map.MapManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.PowerupManager;
import net.samagames.bomberman.scoreboard.ScoreboardBomberman;
import net.samagames.bomberman.timer.TimerBomberman;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.RulesBook;
import net.samagames.tools.Titles;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GameManager of Bomberman plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class GameManager extends Game<PlayerBomberman> {

    private final Server server;
    private final TimerBomberman timer;
    private final PowerupManager powerupManager;
    private final ScoreboardBomberman scoreboardBomberman;
    private final List<Location> playerSpawnList;
    private final MapManager mapManager;
    private final Plugin plugin;
    private final int bombY;
    private final ItemStack rulesBook;
    private Location spawn;
    private Location specSpawn;
    private Music music;


    public GameManager(JavaPlugin plugin) {

        super("bomberman", "Bomberman", "", PlayerBomberman.class);

        this.plugin = plugin;
        this.server = plugin.getServer();
        this.timer = new TimerBomberman(this);
        this.powerupManager = new PowerupManager();
        this.scoreboardBomberman = new ScoreboardBomberman(this);
        this.playerSpawnList = new ArrayList<>();
        this.music = Music.WAITING;

        final JsonObject configs = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs();
        Location pos = LocationUtils.str2loc(configs.get("higher-loc").getAsString());
        Location neg = LocationUtils.str2loc(configs.get("smaller-loc").getAsString());

        this.bombY = pos.getBlockY();

        this.mapManager = new MapManager(this, neg, pos);

        initLocations();

        this.rulesBook = new RulesBook(ChatColor.RED + "§lBomberman").addOwner("Azuxul").addContributor("LordFinn")
                .addPage("§lBut du jeu", " Le but est de faire\n" +
                        " exploser les autres\n joueurs et d'être\n" +
                        " le dernier joueur\n à être en vie !\n" +
                        " La partie se termine\n" +
                        " lorsqu'il reste un\n joueur ou que le\n" +
                        " timer est arrivé à 0.")
                .addPage("§lBombes", " Le nombre de bombes\n est à 1 par defaut,\n" +
                        " mais vous pouvez\n récuperer des\n" +
                        " powerups afin de\n l'augmenter tout\n" +
                        " comme la force de\n l'explosion. Les\n" +
                        " bombes cassent le\n premier bloc qu'elles\n" +
                        " rencontrent et\n explosent en chaine !")
                .addPage("§lBooster 1/2", " Différents boosters\n" +
                        " apparaîtront\n lorsqu'un mur\n" +
                        " explosera, il en\n exsiste de trois\n" +
                        " types : Les booster\n positifs, les négatifs\n" +
                        " et ceux de vitesse.\n" +
                        " Le booster est infini\n jusqu'à en\n" +
                        " récuperer un autre.\n En se déplaçant\n")
                .addPage("§lBooster 2/2", " en sneak, vous\n" +
                        " ne récupérez pas\n les boosters.")
                .toItemStack();
    }

    public boolean isTestServer() {

        return SamaGamesAPI.get().getServerName().startsWith("TestServer_");
    }

    /**
     * Initialize locations from game.json file
     */
    private void initLocations() {

        final JsonObject configs = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs();

        this.spawn = LocationUtils.str2loc(configs.get("waiting-lobby").getAsString());
        this.specSpawn = LocationUtils.str2loc(configs.get("spectators-spawn").getAsString());

        // Add spawn locations in list
        configs.get("spawn-locations").getAsJsonArray().forEach(location -> playerSpawnList.add(LocationUtils.str2loc(location.getAsString()).add(0, 2, 0)));
    }

    public int getBombY() {
        return bombY;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ScoreboardBomberman getScoreboardBomberman() {
        return scoreboardBomberman;
    }

    public Server getServer() {
        return server;
    }

    public TimerBomberman getTimer() {
        return timer;
    }

    public PowerupManager getPowerupManager() {
        return powerupManager;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getSpecSpawn() {
        return specSpawn;
    }

    private List<Location> getPlayerSpawnList() {
        return playerSpawnList;
    }

    private List<PlayerBomberman> getPlayerBombermanList() {

        return new ArrayList<>(this.getInGamePlayers().values());
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    @Override
    public void startGame() {

        List<PlayerBomberman> playerBombermanList = getPlayerBombermanList();
        List<ArmorValue> armorValues = Arrays.asList(ArmorValue.values());
        Collections.shuffle(getPlayerSpawnList());
        Collections.shuffle(armorValues);
        JsonObject jsonPlayer = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("players").getAsJsonObject();
        final int maxHealth = jsonPlayer.get("max-health").getAsInt();
        final int health = jsonPlayer.get("start-health").getAsInt();
        int spawnIndex = 0;
        int armorIndex = 0;

        music = Music.GAME;

        for (PlayerBomberman playerBomberman : playerBombermanList) {

            Player player = playerBomberman.getPlayerIfOnline();

            if (player != null) {

                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(getPlayerSpawnList().get(spawnIndex));
                player.setBedSpawnLocation(getPlayerSpawnList().get(spawnIndex), true);
                player.getInventory().setHeldItemSlot(0);
                spawnIndex++;
                armorIndex++;

                playerBomberman.setBombNumber(1);
                playerBomberman.setRadius(2);

                if (spawnIndex >= getPlayerSpawnList().size()) {
                    spawnIndex = 0;
                }

                if (armorIndex >= armorValues.size()) {
                    armorIndex = 0;
                }

                playerBomberman.stopWaitingRecord(spawn);
                playerBomberman.playMusic(Music.START, player.getLocation());
                playerBomberman.setRecordPlayTime(Music.START.getTime() * -1);
                playerBomberman.setMaxHealth(maxHealth);
                playerBomberman.setHealth(health);
                playerBomberman.updateInventory();
                playerBomberman.setArmorValue(armorValues.get(armorIndex));
                playerBomberman.setArmor();
            }
        }

        specSpawn.getWorld().setSpawnLocation(specSpawn.getBlockX(), specSpawn.getBlockY(), specSpawn.getBlockZ());
        super.startGame();
    }

    public ItemStack getRulesBook() {
        return rulesBook;
    }

    public Music getMusic() {
        return music;
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(spawn);
        player.getInventory().clear();

        ItemStack record = new ItemStack(Material.RECORD_4);
        ItemMeta itemMeta = record.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Désactiver la musique !");
        record.setItemMeta(itemMeta);

        player.getInventory().setItem(8, record);
        player.getInventory().setItem(4, getRulesBook());
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);

        if (getConnectedPlayers() <= 1 && getStatus().equals(Status.IN_GAME))
            endGame();
    }

    public void endGame() {

        timer.setToZero();

        List<PlayerBomberman> playerBombermanList = getPlayerBombermanList();

        for (PlayerBomberman playerBomberman : getRegisteredGamePlayers().values()) {
            Titles.sendTitle(playerBomberman.getPlayerIfOnline(), 10, 60, 10, ChatColor.GOLD + "Fin de la partie !", ChatColor.GREEN + "Vous avez fait " + playerBomberman.getKills() + " kill(s) !");
            playerBomberman.playMusic(Music.END, playerBomberman.getPlayerIfOnline().getLocation());
        }

        if (!playerBombermanList.isEmpty()) {

            Player player = playerBombermanList.get(0).getPlayerIfOnline();

            getCoherenceMachine().getTemplateManager().getPlayerWinTemplate().execute(player);
            playerBombermanList.get(0).addCoins(30, "Partie gagnée");
            playerBombermanList.get(0).addStars(1, "Partie gagnée");

            for (int i = 3; i >= 0; i--) {
                Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();

                FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.STAR).trail(true).flicker(true).withColor(Color.ORANGE, Color.RED).withFade(Color.BLUE, Color.GREEN).build();

                fireworkMeta.addEffect(effect);
                fireworkMeta.setPower(i);

                firework.setFireworkMeta(fireworkMeta);
            }
        }

        this.handleGameEnd();
    }
}
