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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        this.motY = 0.15;
        this.motZ = 0;
        this.velocityChanged = true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float damage) {

        return false;
    }

    @Override
    public void t_() {
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

        CraftWorld craftWorld = getWorld().getWorld();
        craftWorld.playSound(new Location(craftWorld, locX, locY, locZ), Sound.EXPLODE, 10.0f, 20.0f);

        Player ownerPlayer = owner.getPlayerIfOnline();
        if (ownerPlayer != null) {

            ItemStack bomb = new ItemStack(Material.CARPET, 1, (short) 8);
            ItemMeta itemMeta = bomb.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Bomb");
            bomb.setItemMeta(itemMeta);
            bomb.setAmount(owner.getBombNumber() - owner.getPlacedBombs());

            ownerPlayer.getInventory().setItem(0, bomb);
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
