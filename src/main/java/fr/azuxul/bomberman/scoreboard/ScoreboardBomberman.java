package fr.azuxul.bomberman.scoreboard;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.tools.chat.ActionBarAPI;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Scoreboard
 *
 * @author Azuxul
 * @version 1.0
 */
public class ScoreboardBomberman {

    private final GameManager gameManager;

    public ScoreboardBomberman(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    private ObjectiveSign generateObjectiveSign() {

        ObjectiveSign objectiveSign = new ObjectiveSign("BombermanSc", ChatColor.AQUA + gameManager.getGameName());

        objectiveSign.setLine(0, ChatColor.GRAY + "00:00");
        objectiveSign.setLine(1, "Temps restant :");
        objectiveSign.setLine(2, " ");
        objectiveSign.setLine(3, ChatColor.GREEN + "Aucun");
        objectiveSign.setLine(4, "Booster actif :");
        objectiveSign.setLine(5, "Speed :" + ChatColor.GREEN + " 0");
        objectiveSign.setLine(6, "Nombre de bombes :" + ChatColor.GREEN + " 0");
        objectiveSign.setLine(7, "Puissance d'explosion :" + ChatColor.GRAY + " 0");
        objectiveSign.setLine(8, "  ");
        objectiveSign.setLine(9, "Joueurs restants :" + ChatColor.GOLD + " 0");

        return objectiveSign;
    }

    public void display(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());
        ObjectiveSign objectiveSign = playerBomberman.getObjectiveSign();

        if (objectiveSign == null) {

            objectiveSign = generateObjectiveSign();
            playerBomberman.setObjectiveSign(objectiveSign);
            objectiveSign.addReceiver(player);
        }

        try {
            objectiveSign.setLine(0, ChatColor.GRAY + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds()));
        } catch (Exception e) {
            gameManager.getServer().getLogger().info(String.valueOf(e));
        }

        PowerupTypes powerup = playerBomberman.getPowerupTypes();
        String displayPowerup = powerup == null ? "Aucun" : powerup.getName();

        objectiveSign.setLine(3, ChatColor.GREEN + displayPowerup);
        objectiveSign.setLine(5, "Vitesse : " + ChatColor.GREEN + (Math.round(playerBomberman.getSpeed() * 10) - 2));
        objectiveSign.setLine(6, "Nombre de bombes : " + ChatColor.GREEN + playerBomberman.getBombNumber());
        objectiveSign.setLine(7, "Puissance d'explosion : " + ChatColor.GREEN + playerBomberman.getRadius());

        objectiveSign.setLine(9, "Joueurs restants : " + ChatColor.GOLD + gameManager.getConnectedPlayers());

        objectiveSign.updateLines(false);

        if (!player.getGameMode().equals(GameMode.SPECTATOR))
            ActionBarAPI.sendMessage(player, ChatColor.GREEN + "Booster : " + ChatColor.GOLD + displayPowerup);
    }
}
