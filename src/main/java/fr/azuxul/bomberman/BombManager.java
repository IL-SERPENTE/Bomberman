package fr.azuxul.bomberman;

import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Bomb manager
 *
 * @author Azuxul
 * @version 1.0
 */
public class BombManager {

    private final List<Bomb> bombs;
    private final GameManager gameManager;

    public BombManager(GameManager gameManager) {

        this.bombs = new ArrayList<>();
        this.gameManager = gameManager;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    @SuppressWarnings("deprecation")
    public void spawnBomb(Location location, PlayerBomberman player) {

        Block block = location.getBlock();

        player.setPlacedBombs(player.getPlacedBombs() + 1);

        block.setType(Material.CARPET);
        block.setData((byte) 8);

        gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> {

            block.setType(Material.AIR);

            int fuseTicks = player.getPowerupTypes() != null && player.getPowerupTypes().equals(PowerupTypes.RANDOM_FUSE) ? (RandomUtils.nextInt(4) + 1) * 20 : 50;
            Bomb bomb = new Bomb(((CraftWorld) location.getWorld()).getHandle(), location.getX() + 0.5, location.getY() + 0.7, location.getZ() + 0.5, fuseTicks, player.getRadius(), player.getPlayerIfOnline());

            bombs.add(bomb);
            ((CraftWorld) location.getWorld()).getHandle().addEntity(bomb);

        }, 20L);
    }
}
