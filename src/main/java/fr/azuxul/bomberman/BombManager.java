package fr.azuxul.bomberman;

import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.player.PlayerBomberman;
import org.bukkit.Location;
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

    public BombManager() {

        this.bombs = new ArrayList<>();
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public void spawnBomb(Location location, PlayerBomberman player) {

        Bomb bomb = new Bomb(((CraftWorld) location.getWorld()).getHandle(), location.getX() + 0.5, location.getY() + 0.1, location.getZ() + 0.5, 60, player.getRadius(), player.getPlayerIfOnline());

        bombs.add(bomb);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(bomb);
    }
}
