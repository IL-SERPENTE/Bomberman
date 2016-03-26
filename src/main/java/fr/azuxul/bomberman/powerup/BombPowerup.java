package fr.azuxul.bomberman.powerup;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        int bombNb = playerBomberman.getBombNumber() + 1;

        playerBomberman.setBombNumber(bombNb);
        gameManager.getScoreboardBomberman().display(player);

        ItemStack bomb = new ItemStack(Material.CARPET, 1, (short) 8);
        ItemMeta itemMeta = bomb.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Bombe");
        bomb.setItemMeta(itemMeta);
        bomb.setAmount(playerBomberman.getBombNumber() - playerBomberman.getPlacedBombs());

        player.getInventory().setItem(0, bomb);
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
