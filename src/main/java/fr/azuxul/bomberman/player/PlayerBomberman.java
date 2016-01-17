package fr.azuxul.bomberman.player;

import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.api.games.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Player for Bomberman plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerBomberman extends GamePlayer {

    PowerupTypes powerupTypes;
    int bombNumber;
    int radius;

    public PlayerBomberman(Player player) {
        super(player);
        powerupTypes = null;
        bombNumber = 3;
        radius = 2;
    }

    public PowerupTypes getPowerupTypes() {
        return powerupTypes;
    }

    public void setPowerup(PowerupTypes powerupTypes) {
        this.powerupTypes = powerupTypes;
    }

    public int getBombNumber() {
        return bombNumber;
    }

    public void setBombNumber(int bombNumber) {
        this.bombNumber = bombNumber;

        getPlayerIfOnline().getInventory().setItem(0, new ItemStack(Material.TNT, bombNumber > 64 ? 64 : bombNumber));
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
