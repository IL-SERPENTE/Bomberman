package fr.azuxul.bomberman.powerup;

/**
 * Enum of powerups types
 *
 * @author Azuxul
 * @version 1.0
 */
public enum PowerupTypes {

    SPEED("Speed"),
    SLOWNESS("Lenteur"),
    HYPER_BOMB("Hyper bomb"),
    SUPER_BOMB("Super bomb");

    private final String name;

    PowerupTypes(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
