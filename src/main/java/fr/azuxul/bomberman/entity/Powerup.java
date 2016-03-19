package fr.azuxul.bomberman.entity;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.NBTTags;
import net.minecraft.server.v1_8_R3.*;
import net.samagames.api.games.Status;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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
    public void t_() {
        super.t_();

        if (++dispawnTicks >= 600)
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
        double distanceSquaredAtCoin = this.getBukkitEntity().getLocation().distanceSquared(playerLocation); // Calculate distance

        // If IN_GAME, player game mode is not to spectator, coin is alive and distance at coins is <= 1.5
        if (status.equals(Status.IN_GAME) && isValidPlayer(player) && this.isAlive() && distanceSquaredAtCoin <= 1.44) {
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
