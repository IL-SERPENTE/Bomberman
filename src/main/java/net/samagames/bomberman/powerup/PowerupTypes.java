package net.samagames.bomberman.powerup;

import net.samagames.api.SamaGamesAPI;
import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.Nonnull;

/**
 * Enum of powerups types
 *
 * @author Azuxul
 * @version 1.0
 */
public enum PowerupTypes {

    SPEED("Speed", "speed"),
    SLOWNESS("Lenteur", "slowness"),
    AUTO_PLACE("Auto place", "auto-place"),
    RANDOM_FUSE("Random bomb", "random-fuse"),
    HYPER_BOMB("Hyper bomb", "hyper-bomb"),
    SUPER_BOMB("Super bomb", "super-bomb"),
    SELF_INVULNERABILITY("Self protection", "self-invulnerability"),
    EXPLOSION_KILL("Charge nucléaire", "explosion-kill"),
    BOMB_ACTIVATOR("Détonateur", "bomb-activator");

    private final String name;
    private final int chance;

    PowerupTypes(String name, String jsonName) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("booster-chance").getAsJsonObject().get(jsonName).getAsInt();
    }

    @Nonnull
    public static PowerupTypes getRandomPowerupType() {

        PowerupTypes[] values = values();
        int chanceTotal = 0;

        for (PowerupTypes powerupTypes : values)
            chanceTotal += powerupTypes.chance;

        int random = RandomUtils.nextInt(chanceTotal);
        int index = 0;

        for (PowerupTypes powerupTypes : values) {
            random = random - powerupTypes.chance;

            if (random <= 0)
                break;
            else
                index++;
        }

        return values[index];
    }

    public String getName() {
        return name;
    }

    public int getChance() {
        return chance;
    }

}
