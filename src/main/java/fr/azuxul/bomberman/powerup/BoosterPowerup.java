package fr.azuxul.bomberman.powerup;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class description
 *
 * @author Azuxul
 */
public class BoosterPowerup implements Powerup {

    private final PowerupTypes type;
    private final GameManager gameManager;

    public BoosterPowerup() {

        this.type = PowerupTypes.values()[RandomUtils.nextInt(PowerupTypes.values().length)];
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        playerBomberman.setPowerup(type);
        gameManager.getScoreboardBomberman().display(player);
    }

    @Override
    public String getName() {
        return ChatColor.GOLD + "Booster";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.WOOD);
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

}
