package net.samagames.bomberman.entity;

import net.minecraft.server.v1_8_R3.*;
import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.map.CaseMap;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.PowerupTypes;
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
        this.motY = 0;
        this.motZ = 0;
        this.velocityChanged = true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float damage) {

        Entity damager = damagesource.i();

        if (damager instanceof EntityPlayer) {

            PlayerBomberman playerBomberman = gameManager.getPlayer(damager.getUniqueID());

            if (owner.equals(playerBomberman) && owner.getPowerupTypes() != null && owner.getPowerupTypes().equals(PowerupTypes.BOMB_ACTIVATOR)) {

                explode();
            }
        }
        return false;
    }

    @Override
    public void t_() {

        if (this.fuseTicks-- <= 0 && isAlive()) {
            if (!this.world.isClientSide) {
                this.explode();
            }

            this.die();
        } else {
            this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        }
    }

    public void explode() {

        CraftWorld craftWorld = getWorld().getWorld();
        Location baseLocation = new Location(craftWorld, locX, locY, locZ);
        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation((int) Math.floor(locX), (int) Math.floor(locZ));

        this.owner.setPlacedBombs(this.owner.getPlacedBombs() - 1);
        die();


        PowerupTypes powerup = owner.getPowerupTypes();
        // powerup can be null
        caseMap.explode(PowerupTypes.HYPER_BOMB.equals(powerup), PowerupTypes.SUPER_BOMB.equals(powerup), owner);

        craftWorld.playSound(baseLocation, Sound.EXPLODE, 10.0f, 20.0f);

        Player ownerPlayer = owner.getPlayerIfOnline();
        if (ownerPlayer != null) {

            ItemStack bomb = new ItemStack(Material.CARPET, 1, (short) 8);
            ItemMeta itemMeta = bomb.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Bombe");
            bomb.setItemMeta(itemMeta);
            bomb.setAmount(owner.getBombNumber() - owner.getPlacedBombs());

            ownerPlayer.getInventory().setItem(0, bomb);
        }

        baseLocation.add(0, 1, 0).getBlock().setType(Material.AIR , false);
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
