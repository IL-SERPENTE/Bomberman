package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
public class BombModifierPowerup implements Powerup {

    private final Powerups type;
    private final GameManager gameManager;

    public BombModifierPowerup() {

        this.type = Powerups.getRandomPowerupType(Types.BOMB_MODIFIER);
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        playerBomberman.setBombModifier(type);
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Type de bombe";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BEDROCK);
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }
}
