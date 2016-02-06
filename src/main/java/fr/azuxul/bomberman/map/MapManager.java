package fr.azuxul.bomberman.map;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

/**
 * Map manager
 *
 * @author Azuxul
 */
public class MapManager {

    private final CaseMap[][] map;
    private final int height;
    private final int wight;
    private final Location smallerLoc;
    private final GameManager gameManager;

    public MapManager(GameManager gameManager, Location smallerLoc, Location higherLoc) {

        this.smallerLoc = smallerLoc;
        this.gameManager = gameManager;

        this.wight = higherLoc.getBlockX() - smallerLoc.getBlockX() + 1;
        this.height = higherLoc.getBlockZ() - smallerLoc.getBlockZ() + 1;

        this.map = new CaseMap[wight][height];

        for (int x = smallerLoc.getBlockX(); x <= higherLoc.getBlockX(); x++) {
            for (int z = smallerLoc.getBlockZ(); z <= higherLoc.getBlockZ(); z++) {

                int mapX = worldLocXToMapLocX(x);
                int mapY = worldLocZToMapLocY(z);

                map[mapX][mapY] = new CaseMap(gameManager, new Location(smallerLoc.getWorld(), x, smallerLoc.getY(), z), map, mapX, mapY);
            }
        }
    }

    public CaseMap[][] getMap() {
        return map;
    }

    public int worldLocXToMapLocX(int xWorld) {

        return xWorld + Math.abs(smallerLoc.getBlockX());
    }

    public int worldLocZToMapLocY(int zWorld) {

        return zWorld + Math.abs(smallerLoc.getBlockZ());
    }

    public int getHeight() {
        return height;
    }

    public int getWight() {
        return wight;
    }

    public CaseMap getCaseAtWorldLocation(Location location) {

        CaseMap result = null;

        int x = worldLocXToMapLocX(location.getBlockX());
        int y = worldLocZToMapLocY(location.getBlockZ());

        if (x < wight && x > -1 && y < height && y > -1)
            result = map[x][y];

        return result;
    }

    @SuppressWarnings("deprecation")
    public void spawnBomb(Location location, PlayerBomberman player) {

        Block block = location.getBlock();

        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation(location);

        if (caseMap != null) {
            player.setPlacedBombs(player.getPlacedBombs() + 1);

            block.setType(Material.CARPET);
            block.setData((byte) 8);

            gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> {

                block.setType(Material.AIR);

                int fuseTicks = player.getPowerupTypes() != null && player.getPowerupTypes().equals(PowerupTypes.RANDOM_FUSE) ? (RandomUtils.nextInt(4) + 1) * 20 : 50;
                Bomb bomb = new Bomb(((CraftWorld) location.getWorld()).getHandle(), location.getX() + 0.5, location.getY() + 0.7, location.getZ() + 0.5, fuseTicks, player.getRadius(), player.getPlayerIfOnline());

                caseMap.setBomb(bomb);
                ((CraftWorld) location.getWorld()).getHandle().addEntity(bomb);

            }, 20L);
        }
    }

}
