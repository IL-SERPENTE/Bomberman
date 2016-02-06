package fr.azuxul.bomberman.entity;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.map.CaseMap;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
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

        this.owner.setPlacedBombs(this.owner.getPlacedBombs() + 1);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float damage) {

        return false;
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
                this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void explode() {

        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation(new Location(getWorld().getWorld(), locX, locY, locZ));

        this.owner.setPlacedBombs(this.owner.getPlacedBombs() - 1);

        if (owner.getPowerupTypes() == null)
            caseMap.explode(false, owner);
        else if (owner.getPowerupTypes().equals(PowerupTypes.HYPER_BOMB))
            caseMap.explode(true, owner);
        else if (owner.getPowerupTypes().equals(PowerupTypes.SUPER_BOMB)) {

            //TODO: Add super bomb

        } else
            caseMap.explode(false, owner);

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
