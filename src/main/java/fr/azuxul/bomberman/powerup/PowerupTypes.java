package fr.azuxul.bomberman.powerup;

/**
 * Enum of powerups types
 *
 * @author Azuxul
 * @version 1.0
 */
public enum PowerupTypes {

    SPEED("Speed"),
    HYPER_BOMB("Hyper bomb"),
    SUPER_BOMB("Super bomb");

    private String name;

    PowerupTypes(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
