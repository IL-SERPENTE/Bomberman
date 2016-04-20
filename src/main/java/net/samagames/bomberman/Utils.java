package net.samagames.bomberman;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Chunk;
import net.minecraft.server.v1_9_R1.PacketPlayOutMultiBlockChange;
import net.samagames.tools.ParticleEffect;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utils class
 *
 * @author Azuxul
 * @version 1.0
 */
public class Utils {

    private Utils() {
    }

    public static void setLeatherArmorColor(ItemStack armor, Color color) {

        ItemMeta itemMeta = armor.getItemMeta();

        if (itemMeta instanceof LeatherArmorMeta) {

            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
            leatherArmorMeta.setColor(color);
            armor.setItemMeta(leatherArmorMeta);
        }
    }

    public static void spawnRandomFirework(Location location) {

        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect.Type type = FireworkEffect.Type.values()[RandomUtils.nextInt(FireworkEffect.Type.values().length)];

        FireworkEffect effect = FireworkEffect.builder().with(type).trail(RandomUtils.nextBoolean()).flicker(RandomUtils.nextBoolean()).withColor(getRandomColor(), getRandomColor()).withFade(getRandomColor(), getRandomColor()).build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(RandomUtils.nextInt(3));

        firework.setFireworkMeta(fireworkMeta);
    }

    public static Color getRandomColor() {

        return Color.fromBGR(RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255));
    }

    public static void spanwCat(Player player, GameManager gameManager) {

        Ocelot cat = player.getWorld().spawn(player.getLocation(), Ocelot.class);
        cat.setCatType(Ocelot.Type.RED_CAT);
        cat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50000, 4, true, true), true);
        cat.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 50000, 255, true, true), true);
        cat.setTarget(player);

        final BukkitTask task = gameManager.getServer().getScheduler().runTaskTimer(gameManager.getPlugin(), () -> {
            if (cat.getCatType().equals(Ocelot.Type.RED_CAT)) {
                cat.setCatType(Ocelot.Type.SIAMESE_CAT);
            } else {
                cat.setCatType(Ocelot.Type.RED_CAT);
            }

            cat.getLocation().getWorld().playSound(cat.getLocation(), Sound.ENTITY_TNT_PRIMED, 10.0f, 20.0f);
            ParticleEffect.FLAME.display(1, 1, 1, 1, 10, cat.getLocation(), 30);
        }, 0L, 7L);

        gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> {
            gameManager.getMapManager().getCaseAtWorldLocation(cat.getLocation().getBlockX(), cat.getLocation().getBlockZ()).explode(true, false, gameManager.getPlayer(player.getUniqueId()), false);
            gameManager.getServer().getScheduler().cancelTask(task.getTaskId());
            cat.remove();
        }, 180);
    }

    public static void changeBlocks(Map<Vector, Material> toChange, Player player) {
        Map<Chunk, List<Short>> chunks = new HashMap<>();
        for (Map.Entry<Vector, Material> e : toChange.entrySet()) {
            Chunk chunk = getChunk(chunks.keySet(), e.getKey().getBlockX() >> 4, e.getKey().getBlockZ() >> 4);
            if (chunk == null) {
                chunk = new Chunk(((CraftWorld) player.getWorld()).getHandle(), e.getKey().getBlockX() << 4, e.getKey().getBlockZ() << 4);
                chunks.put(chunk, new ArrayList<>());
            }
            chunk.a(new BlockPosition(e.getKey().getBlockX(), e.getKey().getBlockY(), e.getKey().getBlockZ()), CraftMagicNumbers.getBlock(e.getValue()).getBlockData());
            chunks.get(chunk).add(getRelativeChunkLocation(e.getKey()));
        }
        for (Map.Entry<Chunk, List<Short>> e : chunks.entrySet()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMultiBlockChange(e.getValue().size(), toShort(e.getValue()), e.getKey()));
        }
    }

    private static Chunk getChunk(Iterable<Chunk> chunks, int x, int z) {
        for (Chunk chunk : chunks) {
            if (chunk.locX == x && chunk.locZ == z)
                return chunk;
        }
        return null;
    }

    private static short[] toShort(List<Short> shorts) {
        short[] array = new short[shorts.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = shorts.get(i);
        return array;
    }

    public static short getRelativeChunkLocation(Vector vector) {
        return (short) ((vector.getBlockX() << 12 & 0xF) & (vector.getBlockZ() << 8 & 0xF) & (vector.getBlockY() & 0xFF));
    }

}
