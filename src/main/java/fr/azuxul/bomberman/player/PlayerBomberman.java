package fr.azuxul.bomberman.player;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.map.CaseMap;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    private float speed;

    public PlayerBomberman(Player player) {
        super(player);
        powerupTypes = null;
        objectiveSign = null;
        bombNumber = 1;
        radius = 2;
        speed = 0.2f;
        caseMap = Bomberman.getGameManager().getMapManager().getCaseAtWorldLocation(player.getLocation());
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {

        if (speed > 0.4) {
            this.speed = 0.4f;
        } else if (speed < 0.1) {
            this.speed = 0.1f;
        } else
            this.speed = speed;

        getPlayerIfOnline().setWalkSpeed(this.speed);
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
        updateInventory();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        updateInventory();
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

    public void updateInventory() {

        int itemBombNb = bombNumber > 64 ? 64 : bombNumber;
        ItemStack itemBombNumber = new ItemStack(Material.DIAMOND_BLOCK, itemBombNb);
        ItemMeta itemMeta = itemBombNumber.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "Nombre de bombes : " + ChatColor.GOLD + bombNumber);
        itemBombNumber.setItemMeta(itemMeta);

        int itemRadiusNb = radius > 64 ? 64 : radius;
        ItemStack itemRadiusNbumber = new ItemStack(Material.EMERALD_BLOCK, itemRadiusNb);
        itemMeta = itemRadiusNbumber.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "Puissance : " + ChatColor.GOLD + itemRadiusNb);
        itemRadiusNbumber.setItemMeta(itemMeta);

        Player player = getPlayerIfOnline();
        Inventory inventory = player.getInventory();

        inventory.setItem(8, itemRadiusNbumber);
        inventory.setItem(7, itemBombNumber);
    }
}
