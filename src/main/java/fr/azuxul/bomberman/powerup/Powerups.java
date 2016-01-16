package fr.azuxul.bomberman.powerup;

/**
 * Class description
 *
 * @author Azuxul
 */
public enum Powerups {

    HYPER_BOMB(""),
    P1("Un booster");

    private String name;

    Powerups(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
