package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.Titles;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Booster powerup
 *
 * @author Azuxul
 * @version 1.0
 */
public class BoosterPowerup implements Powerup {

    private final Powerups type;
    private final GameManager gameManager;

    public BoosterPowerup() {

        this.type = Powerups.getRandomPowerupType(Types.BOOSTER);
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (type.equals(Powerups.AUTO_PLACE)) {
            Titles.sendTitle(player, 10, 60, 10, ChatColor.RED + "\u26A0 Malus \\\"AutoPlace\\\" activé ! \u26A0", ChatColor.GOLD + "Il place automatiquement des bombs sous vos pieds");
        }

        if (!playerBomberman.getPersistentPowerups().contains(type))
            playerBomberman.getPersistentPowerups().add(type);

        gameManager.getScoreboardBomberman().display(player);

        playerBomberman.updateInventoryBoosterStatus();
    }

    @Override
    public String getName() {
        return ChatColor.GOLD + "Booster";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_BLOCK);
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
