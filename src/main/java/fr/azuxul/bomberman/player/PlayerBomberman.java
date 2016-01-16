package fr.azuxul.bomberman.player;

import fr.azuxul.bomberman.powerup.Powerups;
import net.samagames.api.games.GamePlayer;
import org.bukkit.entity.Player;

/**
 * Player for Bomberman plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerBomberman extends GamePlayer {

    Powerups powerups;

    public PlayerBomberman(Player player) {
        super(player);
    }

    public Powerups getPowerups() {
        return powerups;
    }

    public void setPowerup(Powerups powerups) {
        this.powerups = powerups;
    }
}
