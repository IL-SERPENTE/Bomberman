package fr.azuxul.bomberman.map;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.entity.Powerup;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.BombPowerup;
import fr.azuxul.bomberman.powerup.BoosterPowerup;
import fr.azuxul.bomberman.powerup.RadiusPowerup;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Case of map
 *
 * @author Azuxul
 * @version 1.0
 */
public class CaseMap {

    GameManager gameManager;
    Location worldLocation;
    Powerup powerup;
    Bomb bomb;
    Material block;
    List<PlayerBomberman> players;
    int xMap;
    int yMap;
    CaseMap[][] map;

    public CaseMap(GameManager gameManager, Location worldLocation, CaseMap[][] map, int xMap, int yMap) {

        this.worldLocation = worldLocation;
        this.xMap = xMap;
        this.yMap = yMap;

        this.players = new ArrayList<>();
        this.block = worldLocation.getBlock().getType();
        this.powerup = null;
        this.gameManager = gameManager;

        this.map = map;
    }

    public void explode(boolean cobblestone, PlayerBomberman source) {

        int radius = source.getRadius();
        EnumMap<BlockFace, Boolean> faces = new EnumMap<>(BlockFace.class);

        faces.put(BlockFace.NORTH, true);
        faces.put(BlockFace.EAST, true);
        faces.put(BlockFace.SOUTH, true);
        faces.put(BlockFace.WEST, true);

        explodeCase(cobblestone, source, 0);
        killEntitys(source);

        for (int i = 1; i <= radius; i++) {
            final int finalI = i;
            faces.entrySet().stream().filter(Map.Entry::getValue).forEach(entry -> {
                BlockFace face = entry.getKey();
                int x = xMap + finalI * face.getModX();
                int y = yMap + finalI * face.getModZ();

                if (x < gameManager.getMapManager().getWight() && x > -1 && y < gameManager.getMapManager().getHeight() && y > -1) {

                    CaseMap caseMap = map[x][y];

                    entry.setValue(!caseMap.explodeCase(cobblestone, source, finalI));
                }
            });
        }
    }

    public boolean explodeCase(boolean cobblestone, PlayerBomberman source, int indexRadius) {

        boolean explode = false;

        if (block.equals(Material.AIR))
            killEntitys(source);
        else {
            if (block.equals(Material.DIRT) || (cobblestone && block.equals(Material.COBBLESTONE))) {
                block = Material.AIR;
                spawnPowerup(worldLocation);

                explode = true;
            } else if (block.equals(Material.COBBLESTONE) || block.equals(Material.STONE))
                explode = true;
        }

        if (block.equals(Material.AIR))
            displayExplosion(indexRadius);

        updateInWorld();

        return explode;
    }

    private void killEntitys(PlayerBomberman source) {

        if (powerup != null && powerup.isAlive()) {
            powerup.die();
            powerup = null;
        }

        if (bomb != null && bomb.isAlive()) {
            bomb.explode();
            bomb = null;
        }

        if (!players.isEmpty()) {

            for (PlayerBomberman player : players)
                player.getPlayerIfOnline().damage(777.77D, source.getPlayerIfOnline());

            players.clear();
        }
    }

    public void updateInWorld() {

        for (int y = 0; y <= 2; y++) {
            worldLocation.clone().add(0, y, 0).getBlock().setType(block);
        }
    }

    @SuppressWarnings("deprecation")
    private void displayExplosion(int radius) {

        if (radius <= 3 || RandomUtils.nextInt(1000) >= (radius - 3) * 50) {

            Location location = worldLocation.clone().add(0, -1, 0);

            for (Player player : gameManager.getServer().getOnlinePlayers()) {

                player.sendBlockChange(location, Material.STAINED_CLAY, (byte) 14);

                gameManager.getServer().getScheduler().runTaskLaterAsynchronously(gameManager.getPlugin(), () -> player.sendBlockChange(location, Material.STONE, (byte) 0), RandomUtils.nextInt(30) + 30L);
            }
        }
    }

    private void spawnPowerup(Location location) {

        int random = RandomUtils.nextInt(1000);
        Location locationPowerup = location.clone().add(0.5, 0.8, 0.5);
        net.samagames.tools.powerups.Powerup powerupToSpawn = null;

        if (random <= 220)
            powerupToSpawn = new BoosterPowerup();

        else if (random <= 500)
            powerupToSpawn = new RadiusPowerup();

        else if (random <= 650)
            powerupToSpawn = new BombPowerup();

        if (powerupToSpawn != null)
            powerup = gameManager.getPowerupManager().spawnPowerup(powerupToSpawn, locationPowerup);
    }

    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }

    public Location getWorldLocation() {
        return worldLocation;
    }

    public Powerup getPowerup() {
        return powerup;
    }

    public Material getBlock() {
        return block;
    }

    public List<PlayerBomberman> getPlayers() {
        return players;
    }

    public int getxMap() {
        return xMap;
    }

    public int getyMap() {
        return yMap;
    }
}
