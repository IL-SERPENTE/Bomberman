package net.samagames.bomberman;

import org.bukkit.Color;

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
public enum ArmorValue {

    ORANGE(1, Color.fromRGB(216, 127, 51)),
    LIGHT_BLUE(3, Color.fromRGB(102, 153, 216)),
    YELLOW(4, Color.fromRGB(229, 229, 51)),
    GREEN(5, Color.fromRGB(127, 204, 25)),
    PINK(6, Color.fromRGB(242, 178, 204)),
    PURPLE(10, Color.fromRGB(127, 63, 178)),
    BLUE(11, Color.fromRGB(51, 76, 178)),
    BROWN(12, Color.fromRGB(102, 76, 51)),
    RED(14, Color.fromRGB(153, 51, 51));

    final short helmetDataValue;
    final transient Color armorColor;

    ArmorValue(int helmetDataValue, Color armorColor) {

        this.helmetDataValue = (short) helmetDataValue;
        this.armorColor = armorColor;
    }

    public short getHelmetDataValue() {
        return helmetDataValue;
    }

    public Color getArmorColor() {
        return armorColor;
    }
}
