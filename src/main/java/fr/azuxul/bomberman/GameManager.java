package fr.azuxul.bomberman;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupManager;
import fr.azuxul.bomberman.scoreboard.ScoreboardBomberman;
import fr.azuxul.bomberman.timer.TimerBomberman;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.tools.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
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
    private final BombManager bombManager;
    private final List<Location> playerSpawnList;
    private Location spawn;

    public GameManager(JavaPlugin plugin) {

        super("bomberman", "Bomberman", "", PlayerBomberman.class);

        this.server = plugin.getServer();
        this.timer = new TimerBomberman(this);
        this.powerupManager = new PowerupManager();
        this.bombManager = new BombManager();
        this.scoreboardBomberman = new ScoreboardBomberman(this);
        this.playerSpawnList = new ArrayList<>();

        initLocations();
    }

    /**
     * Initialize locations from game.json file
     */
    private void initLocations() {

        IGameProperties gameProperties = SamaGamesAPI.get().getGameManager().getGameProperties();

        // Generate default json array  of spawnLocations
        JsonArray defaultObject = new JsonArray();
        defaultObject.add(new JsonPrimitive("world, -23, 70, -23, 0, 0"));
        defaultObject.add(new JsonPrimitive("world, -23, 70, 25, 0, 0"));
        defaultObject.add(new JsonPrimitive("world, 25, 70, -23, 0, 0"));
        defaultObject.add(new JsonPrimitive("world, 25, 70, 25, 0, 0"));

        this.spawn = LocationUtils.str2loc(gameProperties.getOption("wating-lobby", new JsonPrimitive("world, 0, 90, 0, 0, 0")).getAsString());

        // Add spawn locations in list
        gameProperties.getOption("spawn-locations", defaultObject).getAsJsonArray().forEach(location -> playerSpawnList.add(LocationUtils.str2loc(location.getAsString()).add(0, 2, 0)));
    }

    public BombManager getBombManager() {
        return bombManager;
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

    private List<Location> getPlayerSpawnList() {
        return playerSpawnList;
    }

    private List<PlayerBomberman> getPlayerBombermanList() {

        return new ArrayList<>(this.getInGamePlayers().values());
    }

    @Override
    public void startGame() {

        List<PlayerBomberman> playerBombermanList = getPlayerBombermanList();
        int spawnIndex = 0;

        for (PlayerBomberman playerBomberman : playerBombermanList) {

            Player player = playerBomberman.getPlayerIfOnline();

            if (player != null) {

                player.getInventory().clear();
                player.getInventory().addItem(new ItemStack(Material.TNT));
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

        super.startGame();
    }

    public void endGame() {

        timer.setToZero();

        List<PlayerBomberman> playerBombermanList = getPlayerBombermanList();

        if (!playerBombermanList.isEmpty()) {

            Player player = playerBombermanList.get(0).getPlayerIfOnline();

            getCoherenceMachine().getTemplateManager().getPlayerWinTemplate().execute(player);
        }

        this.handleGameEnd();
    }
}
