package net.samagames.bomberman.entity;

import net.minecraft.server.v1_9_R1.*;
import net.samagames.api.games.Status;
import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.NBTTags;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Powerup entity
 *
 * @author Azuxul
 * @version 1.0
 */
public class Powerup extends EntityArmorStand {

    private final net.samagames.tools.powerups.Powerup powerupType;
    private final EntityItem item;
    private final EntityArmorStand armorStand;
    private int dispawnTicks;

    public Powerup(World world, double x, double y, double z, net.samagames.tools.powerups.Powerup powerupType) {

        super(world, x, y, z);

        NBTTagCompound nbtTagCompoundPowerup = new NBTTagCompound();

        c(nbtTagCompoundPowerup); // Init nbtTagCompound

        nbtTagCompoundPowerup.setBoolean(NBTTags.NO_GRAVITY.getName(), true); // Set NoGravity
        nbtTagCompoundPowerup.setBoolean(NBTTags.MARKER.getName(), true); // Set Marker to true
        nbtTagCompoundPowerup.setBoolean(NBTTags.INVULNERABLE.getName(), true); // Set Invulnerable
        nbtTagCompoundPowerup.setBoolean(NBTTags.INVISIBLE.getName(), true); // Set Invisible
        nbtTagCompoundPowerup.setInt(NBTTags.DISABLED_SLOTS.getName(), 31); // Set DisabledSlots
        nbtTagCompoundPowerup.setBoolean(NBTTags.CUSTOM_NAME_VISIBLE.getName(), true); // Set CustomNameVisible
        nbtTagCompoundPowerup.setString(NBTTags.CUSTOM_NAME.getName(), powerupType.getName()); // Set CustomName
        nbtTagCompoundPowerup.setBoolean(NBTTags.SMALL.getName(), true); // Set Small

        f(nbtTagCompoundPowerup); // Set nbtTagCompound

        item = new EntityItem(world, x, y, z, CraftItemStack.asNMSCopy(powerupType.getIcon()));
        initItem(item);

        setLocation(x, y + 0.2, z, 0, 0);
        item.setLocation(x, y, z, 0, 0);

        armorStand = createArmorStand();

        this.powerupType = powerupType;
    }

    /**
     * Initialize item entity
     *
     * @param item used item
     */
    private static void initItem(EntityItem item) {

        NBTTagCompound nbtTagCompoundItem = new NBTTagCompound();

        item.b(nbtTagCompoundItem); // Init nbtTagCompound

        nbtTagCompoundItem.setBoolean(NBTTags.INVULNERABLE.getName(), true); // Set Invulnerable
        nbtTagCompoundItem.setBoolean(NBTTags.NO_GRAVITY.getName(), true); // Set NoGravity
        nbtTagCompoundItem.setInt(NBTTags.AGE.getName(), -32768); // Set Age
        nbtTagCompoundItem.setInt(NBTTags.PICKUP_DELAY.getName(), 32767); // Set CustomName

        item.a(nbtTagCompoundItem); // Set nbtTagCompound

    }

    private static boolean isValidPlayer(Player player) {

        return !player.isSneaking() && !player.getGameMode().equals(GameMode.SPECTATOR);
    }

    /**
     * Spawn powerup
     */
    public void spawn() {

        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        world.addEntity(armorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        world.addEntity(item, CreatureSpawnEvent.SpawnReason.CUSTOM);

        armorStand.getBukkitEntity().setPassenger(item.getBukkitEntity());
    }

    private EntityArmorStand createArmorStand() {

        EntityArmorStand entityArmorStand = new EntityArmorStand(world, locX, locY + 0.3, locZ);

        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        entityArmorStand.c(nbtTagCompound); // Init nbtTagCompound

        nbtTagCompound.setBoolean(NBTTags.NO_GRAVITY.getName(), true); // Set NoGravity
        nbtTagCompound.setBoolean(NBTTags.MARKER.getName(), true); // Set Marker to true
        nbtTagCompound.setBoolean(NBTTags.INVULNERABLE.getName(), true); // Set Invulnerable
        nbtTagCompound.setBoolean(NBTTags.INVISIBLE.getName(), true); // Set Invisible
        nbtTagCompound.setInt(NBTTags.DISABLED_SLOTS.getName(), 31); // Set DisabledSlots
        nbtTagCompound.setBoolean(NBTTags.SMALL.getName(), true); // Set Small

        entityArmorStand.f(nbtTagCompound); // Set nbtTagCompound

        return entityArmorStand;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public void m() {

        if (++dispawnTicks >= 600) // 30 seconds
            this.die();
    }

    /**
     * Detect collides with entity human
     *
     * @param entityHuman collided human
     */
    @Override
    public void d(EntityHuman entityHuman) {

        Player player = (Player) entityHuman.getBukkitEntity();
        Location playerLocation = player.getLocation();
        GameManager gameManager = Bomberman.getGameManager();
        Status status = gameManager.getStatus();
        double distanceSquaredAtPowerup = this.getBukkitEntity().getLocation().distanceSquared(playerLocation); // Calculate distance

        // If IN_GAME, player game mode is not to spectator, powerup is alive and distance at powerup is <= 1.5
        if (status.equals(Status.IN_GAME) && isValidPlayer(player) && this.isAlive() && distanceSquaredAtPowerup <= 1.44) {
            for (int i = 0; i <= 20; i++)
                player.playSound(playerLocation, Sound.BLOCK_NOTE_BASEDRUM, 20.0f, 1.8f);
            powerupType.onPickup(player);
            die();
        }

    }

    /**
     * Kill booster
     */
    @Override
    public void die() {

        super.die();
        item.die();
        armorStand.die();

        Bomberman.getGameManager().getPowerupManager().getPowerups().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Powerup))
            return false;

        Powerup powerup = (Powerup) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(powerupType, powerup.powerupType)
                .append(item, powerup.item)
                .append(armorStand, powerup.armorStand)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(powerupType)
                .append(item)
                .append(armorStand)
                .toHashCode();
    }
}
