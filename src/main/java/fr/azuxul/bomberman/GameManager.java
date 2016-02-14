package fr.azuxul.bomberman;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import fr.azuxul.bomberman.map.MapManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupManager;
import fr.azuxul.bomberman.scoreboard.ScoreboardBomberman;
import fr.azuxul.bomberman.timer.TimerBomberman;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.api.games.Status;
import net.samagames.tools.LocationUtils;
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
    private Location spawn;
    private Location specSpawn;

    public GameManager(JavaPlugin plugin) {

        super("bomberman", "Bomberman", "", PlayerBomberman.class);

        this.plugin = plugin;
        this.server = plugin.getServer();
        this.timer = new TimerBomberman(this);
        this.powerupManager = new PowerupManager();
        this.scoreboardBomberman = new ScoreboardBomberman(this);
        this.playerSpawnList = new ArrayList<>();

        IGameProperties gameProperties = SamaGamesAPI.get().getGameManager().getGameProperties();
        Location pos = LocationUtils.str2loc(gameProperties.getOption("higher-loc", new JsonPrimitive("world, 27, 71, 27, 0, 0")).getAsString());
        Location neg = LocationUtils.str2loc(gameProperties.getOption("smaller-loc", new JsonPrimitive("world, -25, 71, -25, 0, 0")).getAsString());

        this.mapManager = new MapManager(this, neg, pos);

        initLocations();
    }

    /**
     * Initialize locations from game.json file
     */
    private void initLocations() {

        IGameProperties gameProperties = SamaGamesAPI.get().getGameManager().getGameProperties();

        // Generate default json array of spawnLocations
        JsonArray defaultObject = new JsonArray();
        defaultObject.add(new JsonPrimitive("world, -23, 70, -23, 0, 0"));
        defaultObject.add(new JsonPrimitive("world, -23, 70, 25, 0, 0"));
        defaultObject.add(new JsonPrimitive("world, 25, 70, -23, 0, 0"));
        defaultObject.add(new JsonPrimitive("world, 25, 70, 25, 0, 0"));

        this.spawn = LocationUtils.str2loc(gameProperties.getOption("wating-lobby", new JsonPrimitive("world, 0, 90, 0, 0, 0")).getAsString());
        this.specSpawn = LocationUtils.str2loc(gameProperties.getOption("spectators-spawn", new JsonPrimitive("world, 1, 77, 1, 0, 0")).getAsString());

        // Add spawn locations in list
        gameProperties.getOption("spawn-locations", defaultObject).getAsJsonArray().forEach(location -> playerSpawnList.add(LocationUtils.str2loc(location.getAsString()).add(0, 2, 0)));
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
        Collections.shuffle(getPlayerBombermanList());
        int spawnIndex = 0;

        ItemStack bomb = new ItemStack(Material.CARPET, 1, (short) 8);
        ItemMeta itemMeta = bomb.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Bomb");
        bomb.setItemMeta(itemMeta);

        for (PlayerBomberman playerBomberman : playerBombermanList) {

            Player player = playerBomberman.getPlayerIfOnline();

            if (player != null) {

                player.getInventory().clear();
                player.getInventory().addItem(bomb);
                playerBomberman.updateInventory();
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(getPlayerSpawnList().get(spawnIndex));
                spawnIndex++;

                playerBomberman.setBombNumber(1);
                playerBomberman.setRadius(2);

                if (spawnIndex >= getPlayerSpawnList().size()) {
                    spawnIndex = 0;
                }
            }
        }

        specSpawn.getWorld().setSpawnLocation(specSpawn.getBlockX(), specSpawn.getBlockY(), specSpawn.getBlockZ());
        super.startGame();
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.setGameMode(GameMode.ADVENTURE);
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);

        if (getConnectedPlayers() - 1 <= 1 && getStatus().equals(Status.IN_GAME))
            endGame();
    }

    public void endGame() {

        timer.setToZero();

        List<PlayerBomberman> playerBombermanList = getPlayerBombermanList();

        if (!playerBombermanList.isEmpty()) {

            Player player = playerBombermanList.get(0).getPlayerIfOnline();

            getCoherenceMachine().getTemplateManager().getPlayerWinTemplate().execute(player);
            playerBombermanList.get(0).addCoins(30, "Partie gagné");
            playerBombermanList.get(0).addStars(1, "Partie gagné");
        }

        this.handleGameEnd();
    }
}
