package net.samagames.bomberman.timer;

import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Status;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.Music;
import net.samagames.bomberman.player.PlayerBomberman;
import org.bukkit.Server;

/*
 * This file is part of Bomberman.
 *
 * Bomberman is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bomberman is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bomberman.  If not, see <http://www.gnu.org/licenses/>.
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
