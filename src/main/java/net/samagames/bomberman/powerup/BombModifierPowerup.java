package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * BombModifier powerup
 *
 * @author Azuxul
 * @version 1.0
 */
public class BombModifierPowerup implements Powerup {

    private final Powerups type;
    private final GameManager gameManager;

    public BombModifierPowerup() {

        this.type = Powerups.getRandomPowerupType(Types.BOMB_MODIFIER);
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        playerBomberman.setBombModifier(type);
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Type de bombe";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BEDROCK);
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
