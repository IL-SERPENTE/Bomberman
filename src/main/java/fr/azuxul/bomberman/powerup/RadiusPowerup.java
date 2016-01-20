package fr.azuxul.bomberman.powerup;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class description
 *
 * @author Azuxul
 */
public class RadiusPowerup implements Powerup {

    @Override
    public void onPickup(Player player) {

        GameManager gameManager = Bomberman.getGameManager();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        int radius = playerBomberman.getRadius() + 1;

        playerBomberman.setRadius(radius);
        gameManager.getScoreboardBomberman().display(player);
    }

    @Override
    public String getName() {
        return ChatColor.RED + "Puissance";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.EMERALD_BLOCK);
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
