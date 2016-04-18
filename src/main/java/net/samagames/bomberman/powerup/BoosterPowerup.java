package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.Titles;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Booster powerup
 *
 * @author Azuxul
 * @version 1.0
 */
public class BoosterPowerup implements Powerup {

    private final PowerupTypes type;
    private final GameManager gameManager;

    public BoosterPowerup() {

        this.type = PowerupTypes.getRandomPowerupType(false);
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (type.equals(PowerupTypes.SPEED))
            playerBomberman.setSpeed(playerBomberman.getSpeed() + 0.1f);
        else if (type.equals(PowerupTypes.SLOWNESS))
            playerBomberman.setSpeed(playerBomberman.getSpeed() - 0.1f);
        else if (type.equals(PowerupTypes.CAT)){
            Ocelot cat = player.getWorld().spawn(player.getLocation() , Ocelot.class);
            cat.setCatType(Ocelot.Type.RED_CAT);
            Bukkit.getScheduler().runTaskLater(gameManager.getPlugin() , () -> {
                gameManager.getMapManager().getCaseAtWorldLocation(cat.getLocation().getBlockX() , cat.getLocation().getBlockZ()).explode(true , false , playerBomberman);
                cat.remove();
            } , PowerupTypes.CAT.getDuration());
        }
        else {
            if (type.equals(PowerupTypes.AUTO_PLACE)) {
                Titles.sendTitle(player, 10, 60, 10, ChatColor.RED + "\u26A0 Malus \\\"AutoPlace\\\" activé ! \u26A0", ChatColor.GOLD + "Il place automatiquement des bombs sous vos pieds");
            }

            playerBomberman.setPowerup(type);
        }

        gameManager.getScoreboardBomberman().display(player);
    }

    @Override
    public String getName() {
        if (type.equals(PowerupTypes.SPEED) || type.equals(PowerupTypes.SLOWNESS))
            return ChatColor.AQUA + "Vitesse modifiée";
        else
            return ChatColor.GOLD + "Booster";
    }

    @Override
    public ItemStack getIcon() {
        if (type.equals(PowerupTypes.SPEED) || type.equals(PowerupTypes.SLOWNESS))
            return new ItemStack(Material.REDSTONE_BLOCK);
        else
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
