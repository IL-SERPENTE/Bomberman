package fr.azuxul.bomberman.player;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.map.CaseMap;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.entity.Player;
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
    private CaseMap caseMap;
    private int bombNumber;
    private int radius;
    private int placedBombs;

    public PlayerBomberman(Player player) {
        super(player);
        powerupTypes = null;
        objectiveSign = null;
        bombNumber = 1;
        radius = 2;
        caseMap = Bomberman.getGameManager().getMapManager().getCaseAtWorldLocation(player.getLocation());
    }

    public int getPlacedBombs() {
        return placedBombs;
    }

    public void setPlacedBombs(int placedBombs) {
        this.placedBombs = placedBombs;
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

    public CaseMap getCaseMap() {
        return caseMap;
    }

    public void setCaseMap(CaseMap caseMap) {
        this.caseMap = caseMap;
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
