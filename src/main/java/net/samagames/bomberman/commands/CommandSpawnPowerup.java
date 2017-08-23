package net.samagames.bomberman.commands;

import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.powerup.*;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
public class CommandSpawnPowerup implements CommandExecutor {

    private final GameManager gameManager;

    public CommandSpawnPowerup(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    private static Powerup getPowerupFormString(String powerupType) {

        Powerup type;

        if ("bomb".equalsIgnoreCase(powerupType)) {
            type = new BombPowerup();
        } else if ("booster".equalsIgnoreCase(powerupType)) {
            type = new BoosterPowerup();
        } else if ("cadeau".equalsIgnoreCase(powerupType)) {
            type = new CadeauPowerup();
        } else if ("radius".equalsIgnoreCase(powerupType)) {
            type = new RadiusPowerup();
        } else if ("mBomb".equalsIgnoreCase(powerupType)) {
            type = new BombModifierPowerup();
        } else if ("speed".equalsIgnoreCase(powerupType)) {
            type = new SpeedPowerup();
        } else {
            type = null;
        }

        return type;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (!gameManager.isTestServer() || !(sender instanceof Player)) {
            return true;
        }

        if (args.length > 0) {

            Powerup type = getPowerupFormString(args[0]);

            if (type == null) {
                return false;
            }

            gameManager.getPowerupManager().spawnPowerup(type, ((Player) sender).getLocation());
            sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "DEBUG" + ChatColor.GOLD + "] " + ChatColor.GREEN + "Le powerup vient de spawn !");

            return true;
        }

        return false;
    }
}
