package fr.azuxul.bomberman;

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

        synchronized (this) {
            gameManager = new GameManager(this);
        }
    }
}
