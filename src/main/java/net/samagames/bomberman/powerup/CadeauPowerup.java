package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Powerup cadeau
 *
 * @author Azuxul
 * @version 1.0
 */
public class CadeauPowerup implements Powerup {

    private final PowerupTypes type;
    private final GameManager gameManager;

    public CadeauPowerup() {
        this.type = PowerupTypes.getRandomPowerupType(true);
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (type.equals(PowerupTypes.BLINDNESS) || type.equals(PowerupTypes.NAUSEA)) {

            final PotionEffect effect;

            if (type.equals(PowerupTypes.BLINDNESS)) {

                effect = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);
                gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GRAY + " vient de lancer de l'encre !");
            } else if (type.equals(PowerupTypes.NAUSEA)) {

                effect = new PotionEffect(PotionEffectType.CONFUSION, 60, 1);
                gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GREEN + " a déclancher de la nausée !");
            } else {
                effect = null;
            }

            gameManager.getInGamePlayers().values().forEach(playerBomberman1 -> {

                Player p = playerBomberman1.getPlayerIfOnline();

                if (p != null && !p.equals(player))
                    p.addPotionEffect(effect, true);
            });

        } else if(type.equals(PowerupTypes.SWAP)) {
            playerBomberman.swap();
        } else
            playerBomberman.setPowerup(type);

        gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + player.getName() + ChatColor.GOLD + " vient de recuperer un cadeau");
    }

    @Override
    public String getName() {
        return ChatColor.DARK_PURPLE + "Cadeau";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BEACON);
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
