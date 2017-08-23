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
public enum NBTTags {

    NO_GRAVITY("NoGravity"),
    MARKER("Marker"),
    INVULNERABLE("Invulnerable"),
    INVISIBLE("Invisible"),
    DISABLED_SLOTS("DisabledSlots"),
    CUSTOM_NAME_VISIBLE("CustomNameVisible"),
    CUSTOM_NAME("CustomName"),
    SMALL("Small"),
    AGE("Age"),
    PICKUP_DELAY("PickupDelay"),
    CAN_PLACE_ON("CanPlaceOn");

    private final String name;

    NBTTags(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
