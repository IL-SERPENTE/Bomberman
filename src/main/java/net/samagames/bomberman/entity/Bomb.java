package net.samagames.bomberman.entity;

import net.minecraft.server.v1_8_R3.*;
import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.map.CaseMap;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.PowerupTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Bomb entity
 *
 * @author Azuxul
 * @version 1.0
 */
public class Bomb extends EntityTNTPrimed {

    private static GameManager gameManager;
    private final int radius;
    private final PlayerBomberman owner;

    public Bomb(World world, double x, double y, double z, int fuseTicks, int radius, Player owner) {
        super(world, x, y, z, ((CraftPlayer) owner).getHandle());

        gameManager = Bomberman.getGameManager();
        this.fuseTicks = fuseTicks;
        this.radius = radius;
        this.owner = gameManager.getPlayer(owner.getUniqueId());
        this.motX = 0;
        this.motY = 0;
        this.motZ = 0;
        this.velocityChanged = true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float damage) {

        Entity damager = damagesource.i();

        if (damager instanceof EntityPlayer) {

            PlayerBomberman playerBomberman = gameManager.getPlayer(damager.getUniqueID());

            if (playerBomberman != null && owner.getPowerupTypes() != null) {
                if (owner.equals(playerBomberman) && owner.getPowerupTypes().equals(PowerupTypes.BOMB_ACTIVATOR)) {

                    explode();
                } else if (owner.getPowerupTypes().equals(PowerupTypes.DESTRUCTOR)) {

                    die(false);
                }
            }
        }
        return false;
    }

    @Override
    public void t_() {

        if (this.fuseTicks-- <= 0 && isAlive()) {

            this.die();
        } else {
            this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        }
    }

    public void explode() {

        die(true);
    }

    @Override
    public void die() {
        die(true);
    }

    public void die(boolean explosionDie) {

        CraftWorld craftWorld = getWorld().getWorld();
        Location baseLocation = new Location(craftWorld, locX, locY, locZ);
        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation(baseLocation.getBlockX(), baseLocation.getBlockZ());

        super.die();

        if (explosionDie) {
            PowerupTypes powerup = owner.getPowerupTypes();

            // powerup can be null
            caseMap.explode(PowerupTypes.HYPER_BOMB.equals(powerup), PowerupTypes.SUPER_BOMB.equals(powerup), owner);

            craftWorld.playSound(baseLocation, Sound.EXPLODE, 10.0f, 20.0f);
        } else
            caseMap.setBomb(null);

        this.owner.setPlacedBombs(this.owner.getPlacedBombs() - 1);
        this.owner.updateInventory();

        baseLocation.add(0, 1, 0).getBlock().setType(Material.AIR, false);
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
