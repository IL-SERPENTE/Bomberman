package net.samagames.bomberman.scoreboard;

import net.samagames.api.games.Status;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.Powerups;
import net.samagames.tools.chat.ActionBarAPI;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/*
 * This file is part of Bomberman.
 *
 * Bomberman is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bomberman is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bomberman.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ScoreboardBomberman {

    private final GameManager gameManager;

    public ScoreboardBomberman(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    private static ObjectiveSign generateObjectiveSign() {

        ObjectiveSign objectiveSign = new ObjectiveSign("BombermanSc", ChatColor.DARK_RED.toString() + ChatColor.BOLD + "BOMBER" + ChatColor.WHITE + "MAN");

        objectiveSign.setLine(12, " ");
        objectiveSign.setLine(11, ChatColor.DARK_RED + "\u26A1" + ChatColor.GOLD + "  Vitesse :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(10, ChatColor.DARK_RED + "\u283E" + ChatColor.GOLD + "   Nombre de bombes :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(9, ChatColor.DARK_RED + "\u26A0" + ChatColor.GOLD + "  Portée de l’explosion :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(8, "  ");
        objectiveSign.setLine(7, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "TYPE DE BOMBE");
        objectiveSign.setLine(6, ChatColor.RESET + "  -> Normal");
        objectiveSign.setLine(5, "   ");
        objectiveSign.setLine(4, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "BOOSTER ACTIF");
        objectiveSign.setLine(3, ChatColor.RESET + "  -> Aucun");
        objectiveSign.setLine(2, "    ");
        objectiveSign.setLine(1, ChatColor.DARK_RED + "\u263A" + ChatColor.GOLD + "  Joueurs restant :" + ChatColor.RESET + " -1");
        objectiveSign.setLine(0, ChatColor.DARK_RED + "\u231B" + ChatColor.GOLD + "   Temps restant : " + ChatColor.RESET + "00:00");

        return objectiveSign;
    }

    private static ObjectiveSign getObjectiveSign(PlayerBomberman playerBomberman) {

        ObjectiveSign objectiveSign = playerBomberman.getObjectiveSign();

        if (objectiveSign == null) {

            objectiveSign = generateObjectiveSign();
            playerBomberman.setObjectiveSign(objectiveSign);
            objectiveSign.addReceiver(playerBomberman.getPlayerIfOnline());
        }

        return objectiveSign;
    }

    public void display(Player player) {

        if (!gameManager.getStatus().equals(Status.IN_GAME)) // If game is not started
            return;

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (playerBomberman == null)
            return;

        ObjectiveSign objectiveSign = getObjectiveSign(playerBomberman);

        try {
            objectiveSign.setLine(0, ChatColor.DARK_RED + "\u231B" + ChatColor.GOLD + "   Temps restant : " + ChatColor.RESET + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds()));
        } catch (Exception e) {
            gameManager.getServer().getLogger().info(String.valueOf(e));
        }

        Powerups powerup = playerBomberman.getPowerups();

        objectiveSign.setLine(6, ChatColor.RESET + "  -> " + (playerBomberman.getBombModifier() == null ? "Normal" : playerBomberman.getBombModifier().getName()));
        objectiveSign.setLine(3, ChatColor.RESET + "  -> " + (powerup == null ? "Aucun" : powerup.getName()));
        objectiveSign.setLine(11, ChatColor.DARK_RED + "\u26A1" + ChatColor.GOLD + "   Vitesse : " + ChatColor.RESET + (Math.round(playerBomberman.getSpeed() * 10) - 2));
        objectiveSign.setLine(10, ChatColor.DARK_RED + "\u283E" + ChatColor.GOLD + "   Nombre de bombes : " + ChatColor.RESET + playerBomberman.getBombNumber());
        objectiveSign.setLine(9, ChatColor.DARK_RED + "\u26A0" + ChatColor.GOLD + "  Portée de l’explosion : " + ChatColor.RESET + playerBomberman.getRadius());

        objectiveSign.setLine(1, ChatColor.DARK_RED + "\u263A" + ChatColor.GOLD + "   Joueurs restant : " + ChatColor.RESET + gameManager.getConnectedPlayers());

        if (!player.getGameMode().equals(GameMode.SPECTATOR) && powerup != null)
            ActionBarAPI.sendMessage(player, ChatColor.GREEN + "Booster : " + ChatColor.GOLD + powerup.getName());

        objectiveSign.updateLines(false);
    }
}
