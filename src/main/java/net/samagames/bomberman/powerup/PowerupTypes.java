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
public enum PowerupTypes {

    SPEED("Speed", "speed"),
    SLOWNESS("Lenteur", "slowness"),
    AUTO_PLACE("Auto place", "auto-place", 5),
    RANDOM_FUSE("Random bomb", "random-fuse"),
    HYPER_BOMB("Hyper bomb", "hyper-bomb"),
    SUPER_BOMB("Super bomb", "super-bomb"),
    SELF_INVULNERABILITY("Self protection", "self-invulnerability"),
    EXPLOSION_KILL("Charge nucléaire", "explosion-kill"),
    BOMB_ACTIVATOR("Détonateur", "bomb-activator"),
    DESTRUCTOR("Destructeur", "destructor", 5),
    BOMB_PROTECTION("Seconde vie", "bomb-protection");

    public static final String JSON_POWERUP_CHANCE = "booster-chance";

    private final String name;
    private final int chance;
    private final int duration;
    private final boolean special;

    PowerupTypes(String name, String jsonName) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = -1;
        this.special = false;
    }

    PowerupTypes(String name, String jsonName, boolean special) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = -1;
        this.special = special;
    }

    PowerupTypes(String name, String jsonName, int duration) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = duration;
        this.special = false;
    }

    PowerupTypes(String name, String jsonName, int duration, boolean special) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = duration;
        this.special = special;
    }

    @Nonnull
    public static PowerupTypes getRandomPowerupType(boolean special) {

        PowerupTypes[] values = values();
        int chanceTotal = 0;

        List<PowerupTypes> powerups = new ArrayList<>();

        for (PowerupTypes powerupTypes : values) {
            if (powerupTypes.isSpecial() == special) {
                chanceTotal += powerupTypes.chance;
                powerups.add(powerupTypes);
            }
        }

        int random = RandomUtils.nextInt(chanceTotal);
        int index = 0;

        for (PowerupTypes powerupTypes : powerups) {

            random = random - powerupTypes.chance;

            if (random <= 0)
                break;
            else
                index++;
        }

        return values[index];
    }

    public boolean isSpecial() {
        return special;
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
