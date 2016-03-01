package fr.azuxul.bomberman.entity;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.map.CaseMap;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.EntityTNTPrimed;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
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
    private int fuseTicks;

    public Bomb(World world, double x, double y, double z, int fuseTicks, int radius, Player owner) {
        super(world, x, y, z, ((CraftPlayer) owner).getHandle());

        gameManager = Bomberman.getGameManager();
        this.fuseTicks = fuseTicks;
        this.radius = radius;
        this.owner = gameManager.getPlayer(owner.getUniqueId());
        this.motX = 0;
        this.motY = 0.15;
        this.motZ = 0;
        this.velocityChanged = true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float damage) {

        return false;
    }

    /**
     * Tick
     */
    @Override
    public void m() {
        if (this.world.spigotConfig.currentPrimedTnt++ <= this.world.spigotConfig.maxTntTicksPerTick) {

            if (this.fuseTicks-- <= 0 && isAlive()) {
                if (!this.world.isClientSide) {
                    this.explode();
                }

                this.die();
            } else {
                this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void explode() {

        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation(new Location(getWorld().getWorld(), locX, locY, locZ));

        this.owner.setPlacedBombs(this.owner.getPlacedBombs() - 1);
        die();

        if (owner.getPowerupTypes() == null)
            caseMap.explode(false, false, owner);
        else if (owner.getPowerupTypes().equals(PowerupTypes.HYPER_BOMB))
            caseMap.explode(true, false, owner);
        else if (owner.getPowerupTypes().equals(PowerupTypes.SUPER_BOMB))
            caseMap.explode(false, true, owner);
        else
            caseMap.explode(false, false, owner);
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
