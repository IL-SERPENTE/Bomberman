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

        final JsonObject configs = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs();
        Location pos = LocationUtils.str2loc(configs.get("higher-loc").getAsString());
        Location neg = LocationUtils.str2loc(configs.get("smaller-loc").getAsString());

        this.bombY = pos.getBlockY();

        this.mapManager = new MapManager(this, neg, pos);

        initLocations();
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
        player.teleport(spawn);
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

        if (!playerBombermanList.isEmpty()) {

            Player player = playerBombermanList.get(0).getPlayerIfOnline();

            getCoherenceMachine().getTemplateManager().getPlayerWinTemplate().execute(player);
            playerBombermanList.get(0).addCoins(30, "Partie gagné");
            playerBombermanList.get(0).addStars(1, "Partie gagné");
        }

        this.handleGameEnd();
    }
}
