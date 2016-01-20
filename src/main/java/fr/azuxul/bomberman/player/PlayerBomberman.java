package fr.azuxul.bomberman.player;

import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Player for Bomberman plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerBomberman extends GamePlayer {

    private PowerupTypes powerupTypes;
    private ObjectiveSign objectiveSign;
    private int bombNumber;
    private int radius;

    public PlayerBomberman(Player player) {
        super(player);
        powerupTypes = null;
        objectiveSign = null;
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

    public ObjectiveSign getObjectiveSign() {
        return objectiveSign;
    }

    public void setObjectiveSign(ObjectiveSign objectiveSign) {
        this.objectiveSign = objectiveSign;
    }

    /**
     * Update player stats (active effects)
     * 1 update/s
     */
    public void update() {

        Player player = getPlayerIfOnline();

        if (powerupTypes != null) {

            if (powerupTypes.equals(PowerupTypes.SPEED))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 0), true);

            else if (powerupTypes.equals(PowerupTypes.SLOWNESS))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 0), true);
        }

    }
}
