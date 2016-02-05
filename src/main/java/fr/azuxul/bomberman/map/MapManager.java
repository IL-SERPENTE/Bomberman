package fr.azuxul.bomberman.map;

import fr.azuxul.bomberman.GameManager;
import org.bukkit.Location;

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

    public MapManager(GameManager gameManager, Location smallerLoc, Location higherLoc) {

        this.smallerLoc = smallerLoc;

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

}
