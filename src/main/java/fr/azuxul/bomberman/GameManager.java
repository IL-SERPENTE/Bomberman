package fr.azuxul.bomberman;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.timer.TimerBomberman;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * GameManager of Bomberman plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class GameManager extends Game<PlayerBomberman> {

    private Server server;
    private Logger logger;
    private TimerBomberman timer;
    private Location spawn;
    private List<Location> playerSpawnList;

    public GameManager(JavaPlugin plugin) {

        super("bomberman", "Bomberman", "", PlayerBomberman.class);

        this.server = plugin.getServer();
        this.logger = plugin.getLogger();
        this.timer = new TimerBomberman(this);

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
        this.playerSpawnList = new ArrayList<>();

        // Add spawn locations in list
        gameProperties.getOption("spawn-locations", defaultObject).getAsJsonArray().forEach(location -> playerSpawnList.add(LocationUtils.str2loc(location.getAsString()).add(0, 2, 0)));
    }

    public Server getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public TimerBomberman getTimer() {
        return timer;
    }

    public Location getSpawn() {
        return spawn;
    }

    public List<Location> getPlayerSpawnList() {
        return playerSpawnList;
    }

    public List<PlayerBomberman> getPlayerBombermanList() {

        return new ArrayList<>(this.getInGamePlayers().values());
    }

    @Override
    public void startGame() {

        List<PlayerBomberman> playerBombermanList = getPlayerBombermanList();
        int spawnIndex = 0;

        for (PlayerBomberman playerBomberman : playerBombermanList) {

            Player player = playerBomberman.getPlayerIfOnline();

            if (player != null) {

                player.teleport(getPlayerSpawnList().get(spawnIndex));
                spawnIndex++;

                if (spawnIndex >= getPlayerSpawnList().size()) {
                    spawnIndex = 0;
                }
            }
        }

        super.startGame();
    }

    public void endGame() {

        this.handleGameEnd();
    }
}
