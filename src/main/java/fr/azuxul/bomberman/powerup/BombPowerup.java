package fr.azuxul.bomberman.powerup;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class description
 *
 * @author Azuxul
 */
public class BombPowerup implements Powerup {

    @Override
    public void onPickup(Player player) {

        GameManager gameManager = Bomberman.getGameManager();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        int bombNb = playerBomberman.getBombNumber() + 1;

        playerBomberman.setBombNumber(bombNb);
    }

    @Override
    public String getName() {
        return "Bomb";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.TNT);
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
