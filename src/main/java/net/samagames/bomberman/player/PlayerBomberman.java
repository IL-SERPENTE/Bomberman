package net.samagames.bomberman.player;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import net.samagames.api.games.GamePlayer;
import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.Music;
import net.samagames.bomberman.map.CaseMap;
import net.samagames.bomberman.powerup.PowerupTypes;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Player for Bomberman plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerBomberman extends GamePlayer {

    private final GameManager gameManager;
    private PowerupTypes powerupTypes;
    private ObjectiveSign objectiveSign;
    private int bombNumber;
    private int radius;
    private int placedBombs;
    private int kills;
    private int recordPlayTime;
    private int autoPlaceTime;
    private float speed;
    private boolean playMusic;

    public PlayerBomberman(Player player) {
        super(player);
        gameManager = Bomberman.getGameManager();
        powerupTypes = null;
        objectiveSign = null;
        bombNumber = 1;
        autoPlaceTime = 0;
        radius = 2;
        speed = 0.2f;
        kills = 0;
        recordPlayTime = -2;
        playMusic = false;
    }

    public void update() {

        if (autoPlaceTime > 0 && --autoPlaceTime <= 0) {

            powerupTypes = null;
        }
    }

    public boolean isPlayMusic() {
        return playMusic;
    }

    public void setPlayMusic(boolean playMusic) {
        this.playMusic = playMusic;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {

        if (speed > 0.4) {
            this.speed = 0.4f;
        } else if (speed < 0.1) {
            this.speed = 0.1f;
        } else
            this.speed = speed;

        getPlayerIfOnline().setWalkSpeed(this.speed);
    }

    public int getPlacedBombs() {
        return placedBombs;
    }

    public void setPlacedBombs(int placedBombs) {
        this.placedBombs = placedBombs;
    }

    public PowerupTypes getPowerupTypes() {
        return powerupTypes;
    }

    public void setPowerup(PowerupTypes powerupTypes) {

        this.powerupTypes = powerupTypes;

        if (powerupTypes.equals(PowerupTypes.AUTO_PLACE)) {
            autoPlaceTime = 5;
        } else {
            autoPlaceTime = 0;
        }
    }

    public int getBombNumber() {
        return bombNumber;
    }

    public void setBombNumber(int bombNumber) {
        this.bombNumber = bombNumber;
        updateInventory();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        updateInventory();
    }

    public ObjectiveSign getObjectiveSign() {
        return objectiveSign;
    }

    public void setObjectiveSign(ObjectiveSign objectiveSign) {
        this.objectiveSign = objectiveSign;
    }

    public CaseMap getCaseMap() {
        Location loc = getPlayerIfOnline().getLocation();
        return gameManager.getMapManager().getCaseAtWorldLocation(loc.getBlockX() , loc.getBlockZ());
    }

    public void updateInventory() {

        int itemBombNb = bombNumber > 64 ? 64 : bombNumber;
        ItemStack itemBombNumber = new ItemStack(Material.DIAMOND_BLOCK, itemBombNb);
        ItemMeta itemMeta = itemBombNumber.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "Nombre de bombes : " + ChatColor.GOLD + bombNumber);
        itemBombNumber.setItemMeta(itemMeta);

        int itemRadiusNb = radius > 64 ? 64 : radius;
        ItemStack itemRadiusNbumber = new ItemStack(Material.EMERALD_BLOCK, itemRadiusNb);
        itemMeta = itemRadiusNbumber.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "Puissance : " + ChatColor.GOLD + itemRadiusNb);
        itemRadiusNbumber.setItemMeta(itemMeta);

        Player player = getPlayerIfOnline();
        Inventory inventory = player.getInventory();

        inventory.setItem(8, itemRadiusNbumber);
        inventory.setItem(7, itemBombNumber);
    }

    public void explode(int radius) {

        Location baseLocation = getPlayerIfOnline().getLocation();

        baseLocation.getWorld().playSound(baseLocation, Sound.EXPLODE, 10.0f, 20.0f);

        int minX = radius * -1;

        for (int x = minX; x <= radius; x++) {

            int minZ = Math.abs(x) - radius;
            int maxZ = Math.abs(minZ);

            for (int z = minZ; z <= maxZ; z++) {
                CaseMap explodeCase = gameManager.getMapManager().getCaseAtWorldLocation(baseLocation.getBlockX() + x , baseLocation.getBlockZ() + z);

                if (explodeCase != null) {
                    explodeCase.explodeCase(true, this, 0);
                }
            }
        }
    }

    public void stopWaitingRecord(Location spawn) {

        CraftPlayer craftPlayer = (CraftPlayer) getPlayerIfOnline();
        PacketPlayOutWorldEvent packetPlayOutWorldEvent = new PacketPlayOutWorldEvent(1005, new BlockPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()), 0, false);

        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutWorldEvent);
    }

    public void playMusic(Music music, Location location) {

        if (playMusic) {
            CraftPlayer craftPlayer = (CraftPlayer) getPlayerIfOnline();
            PacketPlayOutWorldEvent packetPlayOutWorldEvent = new PacketPlayOutWorldEvent(1005, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), music.getRecordId(), false);

            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutWorldEvent);
        }
    }

    public void startMusic() {

        Player player = getPlayerIfOnline();

        player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + ChatColor.GREEN + " La musique est désormais activée !");

        ItemStack record = new ItemStack(Material.RECORD_4);
        ItemMeta itemMeta = record.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Désactiver la musique !");
        record.setItemMeta(itemMeta);

        player.getInventory().setItem(8, record);

        setPlayMusic(true);
        setRecordPlayTime(-2);
    }

    public void stopMusic() {

        Player player = getPlayerIfOnline();

        player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + ChatColor.RED + " La musique est désormais desactivée !");

        ItemStack record = new ItemStack(Material.GREEN_RECORD);
        ItemMeta itemMeta = record.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Activer la musique !");
        record.setItemMeta(itemMeta);

        player.getInventory().setItem(8, record);

        setPlayMusic(false);
        stopWaitingRecord(gameManager.getSpawn());
    }

    public int getRecordPlayTime() {
        return recordPlayTime;
    }

    public void setRecordPlayTime(int recordPlayTime) {
        this.recordPlayTime = recordPlayTime;
    }

    public int getFuseTicks() {
        return getPowerupTypes() != null && getPowerupTypes().equals(PowerupTypes.RANDOM_FUSE) ? (RandomUtils.nextInt(4) + 1) * 20 : 50;
    }
}
