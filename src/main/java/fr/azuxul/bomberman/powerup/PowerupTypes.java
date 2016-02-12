package fr.azuxul.bomberman.powerup;

import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.Nonnull;

/**
 * Enum of powerups types
 *
 * @author Azuxul
 * @version 1.0
 */
public enum PowerupTypes {

    SPEED("Speed", 6),
    SLOWNESS("Lenteur", 2),
    AUTO_PLACE("Auto palce", 1),
    RANDOM_FUSE("Mauavis contact", 5),
    HYPER_BOMB("Hyper bomb", 6),
    SUPER_BOMB("Super bomb", 4);

    private final String name;
    private final int chance;

    PowerupTypes(String name, int chance) {

        this.name = name;
        this.chance = chance;
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
