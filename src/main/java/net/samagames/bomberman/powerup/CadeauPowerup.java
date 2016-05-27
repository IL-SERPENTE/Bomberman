package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.Utils;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.Titles;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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

    private final Powerups type;
    private final GameManager gameManager;

    public CadeauPowerup() {
        this.type = Powerups.getRandomPowerupType(Types.CADEAU);
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        switch (type) {

            case CAT:
                Utils.spanwCat(player, gameManager);
                break;
            case ENDERMITE_SPAWN:
                for (int i = 0; i <= 3; i++) {
                    player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.ENDERMITE);
                }
                break;
            case BLINDNESS:
                sendPotionEffect(player);
                break;
            case NAUSEA:
                sendPotionEffect(player);
                break;
            case SWAP:
                playerBomberman.swap();
                break;
            case FIREWORKS:
                for (int i = 2; i >= 0; i--) {
                    Utils.spawnRandomFirework(player.getLocation().add(0, 4, 0));
                }
                break;
            default:
                setOthersCadeau(playerBomberman);
                break;
        }

        player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + "Tu viens de récuperer " + ChatColor.GOLD + type.getName() + ChatColor.GREEN + " !");
    }

    private void setOthersCadeau(PlayerBomberman playerBomberman) {
        if (type.equals(Powerups.AUTO_PLACE)) {
            Titles.sendTitle(playerBomberman.getPlayerIfOnline(), 10, 60, 10, ChatColor.RED + "\u26A0 Malus \\\"AutoPlace\\\" activé ! \u26A0", ChatColor.GOLD + "Il place automatiquement des bombes sous vos pieds");
        } else if (type.equals(Powerups.INVISIBILITY)) {
            playerBomberman.removeArmor();
        }

        playerBomberman.setPowerup(type);
    }

    private void sendPotionEffect(Player player) {
        final PotionEffect effect;

        if (type.equals(Powerups.BLINDNESS)) {

            effect = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);
            gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GRAY + " vient de lancer de l'encre !");
        } else if (type.equals(Powerups.NAUSEA)) {

            effect = new PotionEffect(PotionEffectType.CONFUSION, 100, 10);
            gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GREEN + " a déclancher de la nausée !");
        } else {
            effect = null;
        }

        gameManager.getInGamePlayers().values().forEach(playerBomberman1 -> {

            Player p = playerBomberman1.getPlayerIfOnline();

            if (p != null && !p.equals(player))
                p.addPotionEffect(effect, true);
        });
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
