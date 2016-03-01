package fr.azuxul.bomberman;

import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Util
 *
 * @author Azuxul
 * @version 1.0
 */
public class Util {

    private Util() {
    }

    public static void sendHotBarMessage(Player player, String message) {

        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatBaseComponent, (byte) 2);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }
}
