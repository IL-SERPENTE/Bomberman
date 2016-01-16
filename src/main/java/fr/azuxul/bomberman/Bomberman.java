package fr.azuxul.bomberman;

import fr.azuxul.bomberman.event.PlayerEvent;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of Bomberman plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class Bomberman extends JavaPlugin {

    private static GameManager gameManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

    @Override
    public void onEnable() {

        SamaGamesAPI samaGamesAPI = SamaGamesAPI.get();

        synchronized (this) {
            gameManager = new GameManager(this);
        }

        samaGamesAPI.getGameManager().registerGame(gameManager); // Register game on SamaGameAPI
        samaGamesAPI.getGameManager().getGameProperties(); // Get properties

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerEvent(), this);

        // Kick players
        getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(""));

        Location spawn = gameManager.getSpawn();
        org.bukkit.World world = spawn.getWorld();

        world.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY() + 3, spawn.getBlockZ()); // Set spawn location
        world.setDifficulty(Difficulty.NORMAL); // Set difficulty
        world.setGameRuleValue("doMobSpawning", "false"); // Set doMobSpawning game rule
        world.setGameRuleValue("reducedDebugInfo", "true"); // Reduce debug info (Mask location)
        world.setStorm(false); // Clear storm
        world.setThundering(false); // Clear weather
        world.setThunderDuration(0); // Clear weather
        world.setWeatherDuration(0); // Clear weather

    }
}
