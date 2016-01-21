package fr.azuxul.bomberman.scoreboard;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Scoreboard
 *
 * @author Azuxul
 * @version 1.0
 */
public class ScoreboardBomberman {

    GameManager gameManager;

    public ScoreboardBomberman(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    private ObjectiveSign generateObjectiveSign() {

        ObjectiveSign objectiveSign = new ObjectiveSign("BombermanSc", ChatColor.AQUA + gameManager.getGameName());

        objectiveSign.setLine(0, "00:00");
        objectiveSign.setLine(1, ChatColor.GRAY + "Temps restant:");
        objectiveSign.setLine(2, " ");
        objectiveSign.setLine(3, "Aucun");
        objectiveSign.setLine(4, ChatColor.GRAY + "Booster actif:");
        objectiveSign.setLine(5, ChatColor.GRAY + "Nombre de bombes simultanés:" + ChatColor.RESET + " 0");
        objectiveSign.setLine(6, ChatColor.GRAY + "Puissance de l'explosion:" + ChatColor.RESET + " 0");
        objectiveSign.setLine(7, "  ");
        objectiveSign.setLine(8, ChatColor.GRAY + "Joueurs restants:" + ChatColor.RESET + " 0");

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
            objectiveSign.setLine(0, String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds()));
        } catch (Exception e) {
            gameManager.getLogger().info(String.valueOf(e));
        }

        PowerupTypes powerup = playerBomberman.getPowerupTypes();

        objectiveSign.setLine(3, powerup == null ? "Aucun" : powerup.getName());
        objectiveSign.setLine(5, ChatColor.GRAY + "Nombre de bombes simultanés: " + ChatColor.RESET + playerBomberman.getBombNumber());
        objectiveSign.setLine(6, ChatColor.GRAY + "Puissance de l'explosion: " + ChatColor.RESET + playerBomberman.getRadius());

        objectiveSign.setLine(8, ChatColor.GRAY + "Joueurs restants: " + ChatColor.RESET + gameManager.getConnectedPlayers());

        objectiveSign.updateLines(false);
    }
}
