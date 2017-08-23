package net.samagames.bomberman;

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
