package fr.azuxul.bomberman.map;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.entity.Powerup;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.BombPowerup;
import fr.azuxul.bomberman.powerup.BoosterPowerup;
import fr.azuxul.bomberman.powerup.RadiusPowerup;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.World;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Class description
 *
 * @author Azuxul
 */
public class CaseMap {

    GameManager gameManager;
    Location worldLocation;
    Powerup powerup;
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

        explodeCase(cobblestone, source);

        for (int i = 1; i <= radius; i++) {
            final int finalI = i;
            faces.entrySet().stream().filter(Map.Entry::getValue).forEach(entry -> {
                BlockFace face = entry.getKey();
                int x = xMap + finalI * face.getModX();
                int y = yMap + finalI * face.getModZ();

                if (x < gameManager.getMapManager().getWight() && x > -1 && y < gameManager.getMapManager().getHeight() && y > -1) {

                    CaseMap caseMap = map[x][y];

                    entry.setValue(!caseMap.explodeCase(cobblestone, source));
                }
            });
        }
    }

    public boolean explodeCase(boolean cobblestone, PlayerBomberman source) {

        boolean explode = false;

        if (block.equals(Material.AIR)) {
            if (powerup != null)
                powerup.die();

            killPlayers(source);
        } else {
            if (block.equals(Material.DIRT) || (cobblestone && block.equals(Material.COBBLESTONE))) {
                block = Material.AIR;
                spawnPowerup(worldLocation);

                explode = true;
            } else if (block.equals(Material.COBBLESTONE) || block.equals(Material.STONE))
                explode = true;
        }

        if (block.equals(Material.AIR))
            displayExplosion();

        updateInWorld();

        return explode;
    }

    private void killPlayers(PlayerBomberman source) {
        if (!players.isEmpty())
            for (PlayerBomberman player : players) {
                player.getPlayerIfOnline().damage(777.77D, source.getPlayerIfOnline());
            }
    }

    public void updateInWorld() {

        for (int y = 0; y <= 2; y++) {
            worldLocation.clone().add(0, y, 0).getBlock().setType(block);
        }
    }

    private void displayExplosion() {

        World world = ((CraftWorld) worldLocation.getWorld()).getHandle();

        //worldLocation.getWorld().createExplosion(worldLocation, 0.5f);
        //ParticleEffect.EXPLOSION_LARGE.display(0.1f, 0.1f, 0.1f, 0, 1, worldLocation.clone().add(0, 1, 0), 30);

        world.addParticle(EnumParticle.EXPLOSION_LARGE, worldLocation.getBlockX() + 0.5, worldLocation.getBlockY(), worldLocation.getBlockZ() + 0.5, world.random.nextFloat(), world.random.nextFloat(), world.random.nextFloat());
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
