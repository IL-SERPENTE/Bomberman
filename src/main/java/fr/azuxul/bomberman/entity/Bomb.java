package fr.azuxul.bomberman.entity;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.World;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class description
 *
 * @author Azuxul
 */
public class Bomb extends EntityTNTPrimed {

    private static GameManager gameManager;
    private int radius;
    private PlayerBomberman owner;

    public Bomb(World world, double x, double y, double z, EntityLiving source) {
        super(world, x, y, z, source);
        gameManager = Bomberman.getGameManager();
    }

    public Bomb(World world, double x, double y, double z, int fuseTicks, int radius, Player owner) {
        super(world, x, y, z, ((CraftPlayer) owner).getHandle());

        gameManager = Bomberman.getGameManager();
        this.fuseTicks = fuseTicks;
        this.radius = radius;
        this.owner = gameManager.getPlayer(owner.getUniqueId());
    }

    private boolean explodeBlock(Location location, boolean breakCobblestone) {

        org.bukkit.World world = location.getWorld();
        boolean blockBreak = false;
        boolean placePowerup = false;

        for (int i = 0; i <= 2; i++) {

            Block block = world.getBlockAt(location.add(0, 1, 0));

            if (block.getType().equals(Material.DIRT) || (breakCobblestone && block.getType().equals(Material.COBBLESTONE))) {

                block.setType(Material.AIR);
                blockBreak = true;
                placePowerup = true;
            } else {
                blockBreak = block.getType().equals(Material.COBBLESTONE);
                break;
            }
        }

        if (placePowerup) {

            if (owner.getBombNumber() <= 2)
                gameManager.getPowerupManager().spawnBombPowerup(location.add(0, -2.3, 0));

            else {

                int random = RandomUtils.nextInt(1000);

                if (random <= 220)
                    gameManager.getPowerupManager().spawnBoosterPowerup(location.add(0, -2.3, 0));

                else if (random <= 500)
                    gameManager.getPowerupManager().spawnRadiusPowerup(location.add(0, -2.3, 0));

                else if (random <= 650)
                    gameManager.getPowerupManager().spawnBombPowerup(location.add(0, -2.3, 0));
            }

        }

        world.createExplosion(location.add(0, 1, 0), 0);

        return blockBreak;
    }

    private void explodeBlocks(Map<BlockFace, Boolean> faces, Location location, int radius, boolean breakCoobestone) {

        faces.entrySet().stream().filter(Map.Entry::getValue).forEach(entry -> {

            BlockFace blockFace = entry.getKey();
            double x = (double) radius * blockFace.getModX();
            double z = (double) radius * blockFace.getModZ();

            entry.setValue(!explodeBlock(location.clone().add(x, -1, z), breakCoobestone));
        });

    }

    @Override
    public void t_() {
        if (this.world.spigotConfig.currentPrimedTnt++ <= this.world.spigotConfig.maxTntTicksPerTick) {

            if (this.fuseTicks-- <= 0) {
                if (!this.world.isClientSide) {
                    this.explode();
                }

                this.die();
            } else {
                this.W();
                this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
            }

        }
    }

    private void explode() {

        Location location = new Location(getWorld().getWorld(), locX, locY, locZ);
        EnumMap<BlockFace, Boolean> faces = new EnumMap<>(BlockFace.class);

        faces.put(BlockFace.NORTH, true);
        faces.put(BlockFace.EAST, true);
        faces.put(BlockFace.SOUTH, true);
        faces.put(BlockFace.WEST, true);

        for (int i = 1; i <= radius; i++) {

            if (owner.getPowerupTypes() == null) {
                explodeBlocks(faces, location.clone(), i, false);
            } else if (owner.getPowerupTypes().equals(PowerupTypes.HYPER_BOMB)) {

                explodeBlocks(faces, location.clone(), i, true);

            } else if (owner.getPowerupTypes().equals(PowerupTypes.SUPER_BOMB)) {

                for (BlockFace face : faces.keySet()) {

                    double x = (double) i * face.getModX();
                    double z = (double) i * face.getModZ();

                    explodeBlock(location.clone().add(x, -1, z), false);
                }
            } else {
                explodeBlocks(faces, location.clone(), i, false);
            }
        }
    }

    @Override
    public boolean equals(Object comapeObject) {

        if (this == comapeObject)
            return true;
        if (!(comapeObject instanceof Bomb))
            return false;
        if (!super.equals(comapeObject))
            return false;

        Bomb bomb = (Bomb) comapeObject;
        return radius == bomb.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), radius);
    }
}
