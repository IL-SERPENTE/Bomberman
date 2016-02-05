package fr.azuxul.bomberman;

import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.entity.Powerup;
import fr.azuxul.bomberman.event.PlayerEvent;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        getServer().getPluginManager().registerEvents(new PlayerEvent(gameManager), this);

        // Register timer
        getServer().getScheduler().scheduleSyncRepeatingTask(this, gameManager.getTimer(), 0L, 20L);

        // Register entity
        registerEntity("Bomb", 69, Bomb.class);
        registerEntity("Powerup", 70, Powerup.class);

        // Kick players
        getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(""));

        Location spawn = gameManager.getSpawn();
        org.bukkit.World world = spawn.getWorld();

        world.setPVP(true); // Enable pvp for damage player
        world.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY() + 3, spawn.getBlockZ()); // Set spawn location
        world.setDifficulty(Difficulty.NORMAL); // Set difficulty
        world.setGameRuleValue("doMobSpawning", "false"); // Set doMobSpawning game rule
        world.setGameRuleValue("reducedDebugInfo", "true"); // Reduce debug info (Mask location)
        world.setStorm(false); // Clear storm
        world.setThundering(false); // Clear weather
        world.setThunderDuration(0); // Clear weather
        world.setWeatherDuration(0); // Clear weather

    }

    @Override
    public void onDisable() {

        gameManager.getPowerupManager().getPowerups().forEach(Entity::die);
    }

    /**
     * Register entity
     *
     * @param name  entity name
     * @param id    entity id
     * @param clazz entity class
     */
    private void registerEntity(String name, int id, Class clazz) {

        // Exception when plugin are reloaded

        try {
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class); // Get method for register new entity
            method.setAccessible(true); // Set accessible
            method.invoke(null, clazz, name, id); // Invoke

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            getLogger().warning(String.valueOf(e));
        }
    }
}
