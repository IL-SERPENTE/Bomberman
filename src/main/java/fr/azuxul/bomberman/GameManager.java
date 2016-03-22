package fr.azuxul.bomberman;

import com.google.gson.JsonObject;
import fr.azuxul.bomberman.map.MapManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupManager;
import fr.azuxul.bomberman.scoreboard.ScoreboardBomberman;
import fr.azuxul.bomberman.timer.TimerBomberman;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.Status;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.RulesBook;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
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

        this.rulesBook = new RulesBook(ChatColor.RED + "§lBomberman").addOwner("Azuxul")
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
        Collections.shuffle(getPlayerSpawnList());
        int spawnIndex = 0;

        ItemStack bomb = new ItemStack(Material.CARPET, 1, (short) 8);
        ItemMeta itemMeta = bomb.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Bomb");
        bomb.setItemMeta(itemMeta);

        music = Music.GAME;

        for (PlayerBomberman playerBomberman : playerBombermanList) {

            Player player = playerBomberman.getPlayerIfOnline();

            if (player != null) {

                player.getInventory().clear();
                player.getInventory().addItem(bomb);
                playerBomberman.updateInventory();
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(getPlayerSpawnList().get(spawnIndex));
                player.getInventory().setHeldItemSlot(0);
                spawnIndex++;

                playerBomberman.setBombNumber(1);
                playerBomberman.setRadius(2);

                if (spawnIndex >= getPlayerSpawnList().size()) {
                    spawnIndex = 0;
                }

                playerBomberman.stopWaitingRecord(spawn);
                playerBomberman.playMusic(Music.START, player.getLocation());
                playerBomberman.setRecordPlayTime(Music.START.getTime() * -1);

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
        itemMeta.setDisplayName(ChatColor.RED + "Desactiver la musique !");
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
            playerBomberman.getPlayerIfOnline().sendTitle(ChatColor.GOLD + "Fin de la partie !", ChatColor.GREEN + "Vous avez fait " + playerBomberman.getKills() + " kill(s) !");
            playerBomberman.playMusic(Music.END, playerBomberman.getPlayerIfOnline().getLocation());
        }

        if (!playerBombermanList.isEmpty()) {

            Player player = playerBombermanList.get(0).getPlayerIfOnline();

            getCoherenceMachine().getTemplateManager().getPlayerWinTemplate().execute(player);
            playerBombermanList.get(0).addCoins(30, "Partie gagné");
            playerBombermanList.get(0).addStars(1, "Partie gagné");
        }

        this.handleGameEnd();
    }
}
