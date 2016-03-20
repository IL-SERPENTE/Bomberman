package fr.azuxul.bomberman;

/**
 * Enum of game music
 *
 * @author Azuxul
 * @version 1.0
 */
public enum Music {

    WAITING(2259, 18),
    START(2256, 5),
    GAME(2257, 23),
    END(2258, 4),
    DEATH(2260, 5);

    private final int recordId;
    private final int time;

    Music(int recordId, int time) {

        this.recordId = recordId;
        this.time = time;
    }

    public int getRecordId() {
        return recordId;
    }

    public int getTime() {
        return time;
    }
}
