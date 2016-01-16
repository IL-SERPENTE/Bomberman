package fr.azuxul.bomberman.entity;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.Powerups;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Class description
 *
 * @author Azuxul
 */
public class Bomb extends EntityTNTPrimed {

    private int radius;
    private PlayerBomberman owner;
    private GameManager gameManager;

    public Bomb(World world, double x, double y, double z, EntityLiving source) {
        super(world, x, y, z, source);
    }

    public Bomb(World world, double x, double y, double z, int fuseTicks, int radius, Player owner) {
        super(world, x, y, z, ((CraftPlayer) owner).getHandle());

        this.gameManager = Bomberman.getGameManager();
        this.fuseTicks = fuseTicks;
        this.radius = radius;
        this.owner = gameManager.getPlayer(owner.getUniqueId());
    }

    private static boolean explodeBlock(Location location, boolean breakCobblestone) {

        org.bukkit.World world = location.getWorld();
        boolean blockBreak = false;

        for (int i = 0; i <= 2; i++) {

            Block block = world.getBlockAt(location.add(0, 1, 0));

            if (block.getType().equals(Material.DIRT) || (breakCobblestone && block.getType().equals(Material.COBBLESTONE))) {

                block.setType(Material.AIR);
                blockBreak = true;
            } else
                break;
        }
        world.createExplosion(location.add(0, -1, 0), 0);

        return blockBreak;
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

        for (int i = 1; i <= radius; i++) {

            if (owner.getPowerups() == null) {

                explodeBlock(location.clone().add(i, -1, 0), false);
                explodeBlock(location.clone().add((double) i * -1, -1, 0), false);
                explodeBlock(location.clone().add(0, -1, i), false);
                explodeBlock(location.clone().add(0, -1, (double) i * -1), false);

            } else if (owner.getPowerups().equals(Powerups.HYPER_BOMB)) {

                explodeBlock(location.clone().add(i, -1, 0), true);
                explodeBlock(location.clone().add((double) i * -1, -1, 0), true);
                explodeBlock(location.clone().add(0, -1, i), true);
                explodeBlock(location.clone().add(0, -1, (double) i * -1), true);

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
