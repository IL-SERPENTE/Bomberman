package fr.azuxul.bomberman.timer;

import com.google.gson.JsonPrimitive;
import fr.azuxul.bomberman.GameManager;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Status;
import org.bukkit.Server;

/**
 * Timer for Bomberman
 *
 * @author Azuxul
 * @version 1.0
 */
public class TimerBomberman implements Runnable {

    private final GameManager gameManager;
    private final Server server;
    private short seconds;
    private short minutes;

    /**
     * Class constructor
     *
     * @param gameManager game manager
     */
    public TimerBomberman(GameManager gameManager) {

        this.gameManager = gameManager;
        this.server = gameManager.getServer();
        this.minutes = SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("timer", new JsonPrimitive("8")).getAsShort();
    }

    /**
     * Game timer, run this method every 20 ticks
     */
    @Override
    public void run() {

        Status gameStatus = gameManager.getStatus();

        if (gameStatus.equals(Status.IN_GAME)) {

            // GAME TIMER

            seconds--;
            if (seconds <= 0) {
                minutes--;
                seconds = 60;
                if (minutes <= 0)
                    gameManager.endGame();
            }
        }

        // Update scoreboard to all player
        server.getOnlinePlayers().forEach(gameManager.getScoreboardBomberman()::display);
    }

    /**
     * Set timer to 0 minutes and 0 seconds
     */
    public void setToZero() {

        minutes = 0;
        seconds = 0;
    }

    /**
     * Get seconds remaining before end
     *
     * @return seconds
     */
    public short getSeconds() {
        return seconds;
    }

    /**
     * Get minutes remaining before end
     *
     * @return minutes
     */
    public short getMinutes() {
        return minutes;
    }
}
