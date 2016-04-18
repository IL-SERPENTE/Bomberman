package net.samagames.bomberman.powerup;

import net.samagames.api.SamaGamesAPI;
import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Enum of powerups types
 *
 * @author Azuxul
 * @version 1.0
 */
public enum Powerups {

    SPEED("Speed", "speed", Types.SPEED),
    SLOWNESS("Lenteur", "slowness", Types.SPEED),
    AUTO_PLACE("Auto place", "auto-place", 5, Types.CADEAU),
    RANDOM_FUSE("Random bomb", "random-fuse", Types.BOMB_MODIFIER),
    HYPER_BOMB("Hyper bomb", "hyper-bomb", Types.BOMB_MODIFIER),
    SUPER_BOMB("Super bomb", "super-bomb", Types.BOMB_MODIFIER),
    SELF_INVULNERABILITY("Self protection", "self-invulnerability", Types.BOOSTER),
    EXPLOSION_KILL("Charge nucléaire", "explosion-kill", Types.BOOSTER),
    BOMB_ACTIVATOR("Détonateur", "bomb-activator", Types.BOOSTER),
    DESTRUCTOR("Destructeur", "destructor", 5, Types.CADEAU),
    BOMB_PROTECTION("Seconde vie", "bomb-protection", Types.BOOSTER),
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
    WALL_INVISIBILITY("Transparence" , "wall_invisibility" , Types.CADEAU),
    INVISIBILITY("Invisibilité", "invisibility", 5, Types.CADEAU);

    public static final String JSON_POWERUP_CHANCE = "booster-chance";

    private final String name;
    private final int chance;
    private final int duration;
    private final Types type;

    Powerups(String name, String jsonName, Types types) {

        this(name, jsonName, -1, types);
    }

    Powerups(String name, String jsonName, int duration, Types type) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = duration;
        this.type = type;
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
