package net.samagames.bomberman.powerup;

import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.Utils;
import net.samagames.bomberman.map.CaseMap;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.tools.powerups.Powerup;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Powerup cadeau
 *
 * @author Azuxul
 * @version 1.0
 */
public class CadeauPowerup implements Powerup {

    private final Powerups type;
    private final GameManager gameManager;

    public CadeauPowerup() {
        this.type = Powerups.getRandomPowerupType(Types.CADEAU);
        this.gameManager = Bomberman.getGameManager();
    }

    @Override
    public void onPickup(Player player) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (type.equals(Powerups.CAT)){
            Ocelot cat = player.getWorld().spawn(player.getLocation() , Ocelot.class);
            cat.setCatType(Ocelot.Type.RED_CAT);
            Bukkit.getScheduler().runTaskLater(gameManager.getPlugin() , () -> {
                gameManager.getMapManager().getCaseAtWorldLocation(cat.getLocation().getBlockX() , cat.getLocation().getBlockZ()).explode(true , false , playerBomberman);
                cat.remove();
            } , Powerups.CAT.getDuration());
        }else if(type.equals(Powerups.WALL_INVISIBILITY)){
            Map<Vector , Material> toChange = new HashMap<>();

            for(int x = 0 ; x < gameManager.getMapManager().getWight() ; x++){
                for(int z = 0 ; z < gameManager.getMapManager().getHeight() ; z++){
                    CaseMap caseMap = gameManager.getMapManager().getMap()[x][z];
                    if(caseMap.getBlock() == Material.DIRT){
                        toChange.put(caseMap.getWorldLocation().clone().add(0 , 1 , 0).toVector() , Material.AIR);
                        toChange.put(caseMap.getWorldLocation().clone().add(0 , 2 , 0).toVector() , Material.AIR);
                    }

                }
            }

            Utils.changeBlocks(toChange , player);
            toChange.replaceAll((v , m ) -> Material.DIRT);
            Bukkit.getScheduler().runTaskLater(gameManager.getPlugin() , () -> Utils.changeBlocks(toChange , player) , Powerups.WALL_INVISIBILITY.getDuration());
        }
        else if (type.equals(Powerups.ENDERMITE_SPAWN)) {
            for (int i = 0; i <= 3; i++) {
                player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDERMITE);
            }
        } else if (type.equals(Powerups.BLINDNESS) || type.equals(Powerups.NAUSEA)) {

            final PotionEffect effect;

            if (type.equals(Powerups.BLINDNESS)) {

                effect = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);
                gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GRAY + " vient de lancer de l'encre !");
            } else if (type.equals(Powerups.NAUSEA)) {

                effect = new PotionEffect(PotionEffectType.CONFUSION, 60, 1);
                gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GOLD + player.getName() + ChatColor.DARK_GREEN + " a déclancher de la nausée !");
            } else {
                effect = null;
            }

            gameManager.getInGamePlayers().values().forEach(playerBomberman1 -> {

                Player p = playerBomberman1.getPlayerIfOnline();

                if (p != null && !p.equals(player))
                    p.addPotionEffect(effect, true);
            });

        } else if(type.equals(Powerups.SWAP)) {
            playerBomberman.swap();
        } else if (type.equals(Powerups.FIREWORKS)) {

            for (int i = 2; i >= 0; i--) {

                Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();

                FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.values()[RandomUtils.nextInt(FireworkEffect.Type.values().length)]).trail(true).flicker(true).withColor(Color.ORANGE, Color.RED).withFade(Color.BLUE, Color.GREEN).build();

                fireworkMeta.addEffect(effect);
                fireworkMeta.setPower(i);

                firework.setFireworkMeta(fireworkMeta);
            }

        } else
            playerBomberman.setPowerup(type);

        gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + player.getName() + ChatColor.GOLD + " vient de recuperer un cadeau");
    }

    @Override
    public String getName() {
        return ChatColor.DARK_PURPLE + "Cadeau";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BEACON);
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
