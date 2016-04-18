package net.samagames.bomberman.map;

import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.entity.Bomb;
import net.samagames.bomberman.entity.Powerup;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.*;
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

    private final GameManager gameManager;
    private final Location worldLocation;
    private final int xMap;
    private final int yMap;
    private Powerup powerup;
    private Bomb bomb;
    private Material block;
    private List<PlayerBomberman> players;

    public CaseMap(GameManager gameManager, Location worldLocation, int xMap, int yMap) {

        this.worldLocation = worldLocation;
        this.xMap = xMap;
        this.yMap = yMap;

        this.players = new ArrayList<>();
        this.block = worldLocation.getBlock().getType();
        this.powerup = null;
        this.gameManager = gameManager;
    }

    public void explode(boolean cobblestone, boolean ignoreFirstBreak, PlayerBomberman source) {

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

                if (hasValidCoordinates(x, y)) {

                    CaseMap caseMap = gameManager.getMapManager().getMap()[x][y];

                    Material blockBreak = caseMap.explodeCase(cobblestone, source, finalI);
                    boolean continueExplode = false;

                    if (blockBreak.equals(Material.AIR) || (blockBreak.equals(Material.DIRT) && ignoreFirstBreak))
                        continueExplode = true;

                    entry.setValue(continueExplode);
                }
            });
        }
    }

    public boolean hasValidCoordinates(int x, int y) {

        return x < gameManager.getMapManager().getWight() && x > -1 && y < gameManager.getMapManager().getHeight() && y > -1;
    }

    public Material explodeCase(boolean cobblestone, PlayerBomberman source, int indexRadius) {

        Material blockExplode = block;

        if (block.equals(Material.AIR))
            killEntitys(source);
        else
            if (block.equals(Material.DIRT) || (cobblestone && block.equals(Material.COBBLESTONE))) {
                block = Material.AIR;
                spawnPowerup(worldLocation);
            }

        updateInWorld();

        if (block.equals(Material.AIR)) {

            if (source.hasPowerup(PowerupTypes.FIRE)) {
                worldLocation.getBlock().setType(Material.FIRE);
            }

            displayExplosion(indexRadius);
        }

        return blockExplode;
    }

    public void spawnWall() {

        block = Material.DIRT;

        if (powerup != null && powerup.isAlive()) {
            powerup.die();
            powerup = null;
        }

        updateInWorld();
    }

    private void killEntitys(PlayerBomberman source) {

        if (powerup != null && powerup.isAlive()) {
            powerup.die();
            powerup = null;
        }

        if (bomb != null && bomb.isAlive()) {
            bomb.explodeBomb(true);
            bomb = null;
        }

        if (!players.isEmpty()) {

            for (PlayerBomberman player : players) {
                Player p = player.getPlayerIfOnline();
                Player pSource = source.getPlayerIfOnline();

                if (p != null && pSource != null && !player.hasPowerup(PowerupTypes.INVULNERABILITY)) {
                    p.damage(777.77D, pSource);
                }
            }

            players.clear();
        }
    }

    private void updateInWorld() {

        for (int y = 0; y <= 2; y++) {
            worldLocation.clone().add(0, y, 0).getBlock().setType(block, false);
        }
    }

    @SuppressWarnings("deprecation")
    private void displayExplosion(int radius) {

        if (radius <= 3 || RandomUtils.nextInt(1000) >= (radius - 3) * 50) {

            Location location = worldLocation.clone().add(0, -1, 0);

            location.getBlock().setTypeIdAndData(Material.STAINED_CLAY.getId(), (byte) 14, false);
            gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> location.getBlock().setType(Material.STONE, false), RandomUtils.nextInt(30) + 30L);
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

        else if (random <= 700)
            powerupToSpawn = new CadeauPowerup();

        if (powerupToSpawn != null)
            powerup = gameManager.getPowerupManager().spawnPowerup(powerupToSpawn, locationPowerup);
    }

    public boolean isEmpty() {

        return players.isEmpty() && (powerup == null || !powerup.isAlive()) && block.equals(Material.AIR);
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

    public Bomb getBomb() {
        return bomb;
    }

    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }
}
