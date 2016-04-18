package net.samagames.bomberman;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Chunk;
import net.minecraft.server.v1_9_R1.PacketPlayOutMultiBlockChange;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
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

    public static void changeBlocks(Map<Vector, Material> toChange , Player player){
        Map<Chunk , List<Short>> chunks = new HashMap<>();
        for(Map.Entry<Vector, Material> e : toChange.entrySet()){
            Chunk chunk = getChunk(chunks.keySet() , e.getKey().getBlockX()>>4 , e.getKey().getBlockZ()>>4);
            if(chunk == null){
                chunk = new Chunk(((CraftWorld) player.getWorld()).getHandle() , e.getKey().getBlockX()<<4 , e.getKey().getBlockZ()<<4);
                chunks.put(chunk , new ArrayList<>());
            }
            chunk.a(new BlockPosition(e.getKey().getBlockX() , e.getKey().getBlockY() , e.getKey().getBlockZ()) , CraftMagicNumbers.getBlock(e.getValue()).getBlockData());
            chunks.get(chunk).add(getRelativeChunkLocation(e.getKey()));
        }
        for(Map.Entry<Chunk , List<Short>> e : chunks.entrySet()){
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutMultiBlockChange(e.getValue().size() , toShort(e.getValue()) , e.getKey()));
        }
    }

    private static Chunk getChunk(Iterable<Chunk> chunks , int x , int z){
        for (Chunk chunk : chunks){
            if(chunk.locX == x && chunk.locZ == z)
                return chunk;
        }
        return null;
    }

    private static short[] toShort(List<Short> shorts){
        short[] array = new short[shorts.size()];
        for (int i = 0 ; i < array.length ; i++)
            array[i] = shorts.get(i);
        return array;
    }

    public static short getRelativeChunkLocation(Vector vector){
        return (short) ((vector.getBlockX()<<12&0xF) & (vector.getBlockZ()<<8&0xF) & (vector.getBlockY()&0xFF));
    }

}
