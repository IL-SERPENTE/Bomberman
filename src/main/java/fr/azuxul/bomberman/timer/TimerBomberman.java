package fr.azuxul.bomberman.timer;

import com.google.gson.JsonPrimitive;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.Music;
import fr.azuxul.bomberman.player.PlayerBomberman;
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
            if (seconds <= -1) {
                minutes--;
                seconds = 59;
                if (minutes <= -1) {
                    setToZero();
                    gameManager.getServer().getScheduler().runTask(gameManager.getPlugin(), gameManager::endGame);
                    gameManager.endGame();
                }
            }

            // Update scoreboard to all player
            server.getOnlinePlayers().forEach(gameManager.getScoreboardBomberman()::display);
        }

        if (!gameStatus.equals(Status.FINISHED)) {

            gameManager.getInGamePlayers().values().forEach(PlayerBomberman::update);
            gameManager.getInGamePlayers().values().forEach(this::sendMusicToPlayer);
            gameManager.getSpectatorPlayers().values().forEach(playerBomberman -> sendMusicToPlayer(playerBomberman));
        }
    }

    private void sendMusicToPlayer(PlayerBomberman playerBomberman) {

        Music music = gameManager.getMusic();

        int recordPlayTime = playerBomberman.getRecordPlayTime() + 1;

        if (recordPlayTime > music.getTime() || recordPlayTime == -1) {

            if (music.equals(Music.WAITING))
                playerBomberman.playMusic(music, gameManager.getSpawn());
            else
                playerBomberman.playMusic(music, playerBomberman.getPlayerIfOnline().getLocation());

            playerBomberman.setRecordPlayTime(0);
        } else {
            playerBomberman.setRecordPlayTime(recordPlayTime);
        }
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
