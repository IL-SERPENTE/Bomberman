package net.samagames.bomberman.commands;

import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.powerup.BombPowerup;
import net.samagames.bomberman.powerup.BoosterPowerup;
import net.samagames.bomberman.powerup.CadeauPowerup;
import net.samagames.bomberman.powerup.RadiusPowerup;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor of /spawnpowerup command
 *
 * @author Azuxul
 * @version 1.0
 */
public class CommandSpawnPowerup implements CommandExecutor {

    private final GameManager gameManager;

    public CommandSpawnPowerup(GameManager gameManager) {

        this.gameManager = gameManager;
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

    private Powerup getPowerupFormString(String powerupType) {

        Powerup type;

        if ("bomb".equalsIgnoreCase(powerupType)) {
            type = new BombPowerup();
        } else if ("booster".equalsIgnoreCase(powerupType)) {
            type = new BoosterPowerup();
        } else if ("cadeau".equalsIgnoreCase(powerupType)) {
            type = new CadeauPowerup();
        } else if ("radius".equalsIgnoreCase(powerupType)) {
            type = new RadiusPowerup();
        } else {
            type = null;
        }

        return type;
    }
}
