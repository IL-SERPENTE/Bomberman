package fr.azuxul.bomberman.timer;

import com.google.gson.JsonPrimitive;
import fr.azuxul.bomberman.GameManager;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Status;
import org.bukkit.Server;

/**
 * Class description
 *
 * @author Azuxul
 */
public class TimerBomberman implements Runnable {

    private final GameManager gameManager;
    private final Server server;
    private short seconds, minutes;

    public TimerBomberman(GameManager gameManager) {

        this.gameManager = gameManager;
        this.server = gameManager.getServer();
        this.minutes = SamaGamesAPI.get().getGameManager().getGameProperties().getOption("timer", new JsonPrimitive("8")).getAsShort();
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
    }
}
