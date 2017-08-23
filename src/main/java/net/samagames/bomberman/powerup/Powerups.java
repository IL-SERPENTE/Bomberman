package net.samagames.bomberman.powerup;

import net.samagames.api.SamaGamesAPI;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of Bomberman.
 *
 * Bomberman is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bomberman is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bomberman.  If not, see <http://www.gnu.org/licenses/>.
 */
public enum Powerups {

    SPEED("Speed", "speed", Types.SPEED),
    SLOWNESS("Lenteur", "slowness", Types.SPEED),
    AUTO_PLACE("Auto place", "auto-place", 5, Types.CADEAU),
    RANDOM_FUSE("Random bomb", "random-fuse", Types.BOMB_MODIFIER),
    HYPER_BOMB("Hyper bomb", "hyper-bomb", Types.BOMB_MODIFIER),
    SUPER_BOMB("Super bomb", "super-bomb", Types.BOMB_MODIFIER),
    SELF_INVULNERABILITY("Self protection", "self-invulnerability", Material.LAPIS_ORE, ""),
    EXPLOSION_KILL("Charge nucléaire", "explosion-kill", Material.IRON_ORE, ""),
    BOMB_ACTIVATOR("Détonateur", "bomb-activator", Material.GOLD_ORE, ""),
    DESTRUCTOR("Destructeur", "destructor", 5, Types.CADEAU),
    BOMB_PROTECTION("Seconde vie", "bomb-protection", Material.DIAMOND_ORE, ""),
    BLINDNESS("Jet d\'encre", "blindness", Types.CADEAU),
    SWAP("Swap", "swap", Types.CADEAU),
    NAUSEA("Nausée", "nausea", Types.CADEAU),
    WALL_BUILDER("Constructeur", "wall-builder", 8, Types.CADEAU),
    FIRE("Incendie", "fire", Types.BOMB_MODIFIER),
    FIREWORKS("Festivité", "fireworks", Types.CADEAU),
    INVULNERABILITY("Invincibilité", "invulnerability", 13, Types.CADEAU),
    FREEZER("Freezer", "freezer", 4, Types.CADEAU),
    CAT("Chat piégé", "cat", Types.CADEAU),
    ENDERMITE_SPAWN("Infestation", "endermite-spawn", Types.CADEAU),
    //WALL_INVISIBILITY("Transparence", "wall_invisibility", 20, Types.CADEAU),
    INVISIBILITY("Invisibilité", "invisibility", 5, Types.CADEAU),
    MULTIPLE_BOMB("Bombe à fragmentation", "multiple-bomb", Types.BOMB_MODIFIER);

    public static final String JSON_POWERUP_CHANCE = "booster-chance";

    private final String name;
    private final int chance;
    private final int duration;
    private final Types type;
    private final transient ItemStack icon;

    Powerups(String name, String jsonName, Types types) {

        this(name, jsonName, -1, types);
    }

    Powerups(String name, String jsonName, Material material, String description) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = -1;
        this.type = Types.BOOSTER;

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.GREEN + name);

        List<String> lore = new ArrayList<>();

        for (String line : description.split("#")) {
            lore.add(line.replaceAll("&", "§"));
        }

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        this.icon = itemStack;
    }

    Powerups(String name, String jsonName, int duration, Types type) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = duration;
        this.type = type;
        this.icon = null;
    }

    @Nonnull
    public static Powerups getRandomPowerupType(Types type) {

        Powerups[] values = values();
        int chanceTotal = 0;

        List<Powerups> powerups = new ArrayList<>();

        for (Powerups powerupTypes : values) {
            if (powerupTypes.getType() == type) {
                chanceTotal += powerupTypes.chance;
                powerups.add(powerupTypes);
            }
        }

        int random = RandomUtils.nextInt(chanceTotal * 10);
        int index = 0;

        for (Powerups powerupTypes : powerups) {

            random = random - powerupTypes.chance * 10;

            if (random < 0)
                break;
            else
                index++;
        }

        return powerups.get(index);
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Types getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public int getChance() {
        return chance;
    }

}
