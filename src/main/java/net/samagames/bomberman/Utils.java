package net.samagames.bomberman;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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
}
