package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Bomb powerup
 *
 * @author Azuxul
 * @version 1.0
 */
public class BombPowerup implements Powerup {

    @Override
    public void onPickup(Player player) {

        GameManager gameManager = Bomberman.getGameManager();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        playerBomberman.setBombNumber(playerBomberman.getBombNumber() + 1);
        gameManager.getScoreboardBomberman().display(player);
        playerBomberman.updateInventory();
    }

    @Override
    public String getName() {
        return "Bombe";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_BLOCK);
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
