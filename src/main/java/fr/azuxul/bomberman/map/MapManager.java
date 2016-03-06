package fr.azuxul.bomberman.map;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.tools.Area;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

/**
 * Map manager
 *
 * @author Azuxul
 * @version 1.0
 */
public class MapManager {

    private final CaseMap[][] map;
    private final int height;
    private final int wight;
    private final Location smallerLoc;
    private final GameManager gameManager;
    private final Area area;

    public MapManager(GameManager gameManager, Location smallerLoc, Location higherLoc) {

        this.smallerLoc = smallerLoc;
        this.gameManager = gameManager;

        this.area = new Area(smallerLoc, higherLoc);

        this.wight = area.getSizeX() + 1;
        this.height = area.getSizeZ() + 1;

        this.map = new CaseMap[wight][height];

        for (int x = area.getMin().getBlockX(); x <= area.getMax().getBlockX(); x++) {
            for (int z = area.getMin().getBlockZ(); z <= area.getMax().getBlockZ(); z++) {

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

        return xWorld - area.getMin().getBlockX();
    }

    public int worldLocZToMapLocY(int zWorld) {

        return zWorld - area.getMin().getBlockZ();
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

        System.out.println(x < wight && x > -1 && y < height && y > -1);
        System.out.println(y < height && y > -1);
        System.out.println(x < wight && x > -1);

        if (x < wight && x > -1 && y < height && y > -1) {
            result = map[x][y];
            System.out.println("meow");
        }

        return result;
    }

    public void movePlayer(Player player, Location locTo) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        CaseMap caseMap = playerBomberman.getCaseMap();

        if (caseMap != null)
            caseMap.getPlayers().remove(playerBomberman);

        caseMap = getCaseAtWorldLocation(locTo);
        System.out.println(caseMap);

        if (caseMap != null) {
            playerBomberman.setCaseMap(caseMap);
            caseMap.getPlayers().add(playerBomberman);

            if (playerBomberman.getPowerupTypes() != null && playerBomberman.getPowerupTypes().equals(PowerupTypes.AUTO_PLACE) && caseMap.getBomb() == null && playerBomberman.getBombNumber() > playerBomberman.getPlacedBombs())
                gameManager.getMapManager().spawnBomb(locTo.getBlock().getLocation(), playerBomberman);
        } else
            player.kickPlayer("Out of map");
    }

    @SuppressWarnings("deprecation")
    public void spawnBomb(Location location, PlayerBomberman player) {

        location.setY(gameManager.getBombY());
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
