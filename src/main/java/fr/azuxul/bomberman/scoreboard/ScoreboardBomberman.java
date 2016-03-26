package fr.azuxul.bomberman.scoreboard;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.api.games.Status;
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

    private static ObjectiveSign generateObjectiveSign() {

        ObjectiveSign objectiveSign = new ObjectiveSign("BombermanSc", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "BOMBER" + ChatColor.WHITE + "MAN");

        objectiveSign.setLine(9, " ");
        objectiveSign.setLine(8, ChatColor.DARK_RED + "\u26A1" + ChatColor.GOLD + "  Vitesse :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(7, ChatColor.DARK_RED + "\u283E" + ChatColor.GOLD + "   Nombre de bombes :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(6, ChatColor.DARK_RED + "\u26A0" + ChatColor.GOLD + "  Portée de l’explosion :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(5, "  ");
        objectiveSign.setLine(4, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "BOOSTER ACTIF");
        objectiveSign.setLine(3, ChatColor.RESET + "  -> Aucun");
        objectiveSign.setLine(2, "   ");
        objectiveSign.setLine(1, ChatColor.DARK_RED + "\u263A" + ChatColor.GOLD + "  Joueurs restant :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(0, ChatColor.DARK_RED + "\u231B" + ChatColor.GOLD + "   Temps restant : " + ChatColor.RESET + "00:00");

        return objectiveSign;
    }

    public void display(Player player) {

        if (!gameManager.getStatus().equals(Status.IN_GAME)) // If game is not started
            return;

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (playerBomberman == null)
            return;

        ObjectiveSign objectiveSign = playerBomberman.getObjectiveSign();

        if (objectiveSign == null) {

            objectiveSign = generateObjectiveSign();
            playerBomberman.setObjectiveSign(objectiveSign);
            objectiveSign.addReceiver(player);
        }

        try {
            objectiveSign.setLine(0, ChatColor.DARK_RED + "\u231B" + ChatColor.GOLD + "   Temps restant : " + ChatColor.RESET + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds()));
        } catch (Exception e) {
            gameManager.getServer().getLogger().info(String.valueOf(e));
        }

        PowerupTypes powerup = playerBomberman.getPowerupTypes();

        objectiveSign.setLine(3, ChatColor.RESET + "  -> " + (powerup == null ? "Aucun" : powerup.getName()));
        objectiveSign.setLine(8, ChatColor.DARK_RED + "\u26A1" + ChatColor.GOLD + "   Vitesse : " + ChatColor.RESET + (Math.round(playerBomberman.getSpeed() * 10) - 2));
        objectiveSign.setLine(7, ChatColor.DARK_RED + "\u283E" + ChatColor.GOLD + "   Nombre de bombes : " + ChatColor.RESET + playerBomberman.getBombNumber());
        objectiveSign.setLine(6, ChatColor.DARK_RED + "\u26A0" + ChatColor.GOLD + "  Portée de l’explosion : " + ChatColor.RESET + playerBomberman.getRadius());

        objectiveSign.setLine(1, ChatColor.DARK_RED + "\u263A" + ChatColor.GOLD + "   Joueurs restant : " + ChatColor.RESET + gameManager.getConnectedPlayers());

        if (!player.getGameMode().equals(GameMode.SPECTATOR) && powerup != null)
            ActionBarAPI.sendMessage(player, ChatColor.GREEN + "Booster : " + ChatColor.GOLD + powerup.getName());

        objectiveSign.updateLines(false);
    }
}
