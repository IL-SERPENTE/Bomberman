package net.samagames.bomberman.player;

import net.minecraft.server.v1_9_R2.*;
import net.samagames.api.games.GamePlayer;
import net.samagames.bomberman.*;
import net.samagames.bomberman.entity.Bomb;
import net.samagames.bomberman.map.CaseMap;
import net.samagames.bomberman.powerup.Powerups;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

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
public class PlayerBomberman extends GamePlayer {

    private final GameManager gameManager;
    private final List<Bomb> aliveBombs;
    private final Set<Powerups> persistentPowerups;
    private Powerups bombModifier;
    private Powerups powerups;
    private ObjectiveSign objectiveSign;
    private Location respawnLocation;
    private ArmorValue armorValue;
    private int bombNumber;
    private int radius;
    private int placedBombs;
    private int totalPlacedBombs;
    private int kills;
    private int recordPlayTime;
    private int powerupDuration;
    private int maxHealth;
    private int health;
    private float speed;
    private boolean playMusic;

    public PlayerBomberman(Player player) {
        super(player);
        gameManager = Bomberman.getGameManager();
        aliveBombs = new ArrayList<>();
        persistentPowerups = new HashSet<>();
        powerups = null;
        objectiveSign = null;
        bombNumber = 1;
        powerupDuration = 0;
        radius = 2;
        speed = 0.2f;
        kills = 0;
        recordPlayTime = -2;
        playMusic = false;
    }

    public void replaceBlock(Material originalBlock, Material newBlock, int duration) {

        Player player = getPlayerIfOnline();
        Map<Vector, Material> toChange = new HashMap<>();

        for (int x = 0; x < gameManager.getMapManager().getWight(); x++) {
            for (int z = 0; z < gameManager.getMapManager().getHeight(); z++) {
                CaseMap caseMap = gameManager.getMapManager().getMap()[x][z];
                if (caseMap.getBlock() == originalBlock) {
                    toChange.put(caseMap.getWorldLocation().toVector(), newBlock);
                    toChange.put(caseMap.getWorldLocation().clone().add(0, 1, 0).toVector(), newBlock);
                    toChange.put(caseMap.getWorldLocation().clone().add(0, 2, 0).toVector(), newBlock);
                }

            }
        }

        Utils.changeBlocks(toChange, player);
        toChange.replaceAll((v, m) -> originalBlock);
        Bukkit.getScheduler().runTaskLater(gameManager.getPlugin(), () -> Utils.changeBlocks(toChange, player), duration);
    }

    public ArmorValue getArmorValue() {
        return armorValue;
    }

    public void setArmorValue(ArmorValue armorValue) {
        this.armorValue = armorValue;
    }

    public Powerups getBombModifier() {
        return bombModifier;
    }

    public void setBombModifier(Powerups bombModifier) {
        this.bombModifier = bombModifier;
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }

    public Set<Powerups> getPersistentPowerups() {
        return persistentPowerups;
    }

    public boolean hasPowerup(Powerups powerup) {

        return powerup.equals(powerups) || persistentPowerups.contains(powerup) || powerup.equals(bombModifier);
    }

    public List<Bomb> getAliveBombs() {
        return aliveBombs;
    }

    private void updateHealth() {

        Player player = getPlayerIfOnline();

        if (player != null) {

            if (maxHealth > 0 && !isSpectator()) {

                int displayHealth = health * 2;

                player.setMaxHealth(maxHealth * 2.0d);

                if (displayHealth != (int) player.getHealth())
                    player.setHealth(displayHealth < 0 ? 0 : displayHealth);
            } else {
                player.resetMaxHealth();
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {

        if (health > maxHealth)
            this.health = maxHealth;
        else
            this.health = health;

        updateHealth();
    }

    public void addTotalBomb(int bombToAdd) {
        this.totalPlacedBombs += bombToAdd;
    }

    public int getTotalPlacedBombs() {
        return totalPlacedBombs;
    }

    public void removeArmor() {

        Player player = getPlayerIfOnline();

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setBoots(null);
    }

    public void setArmor() {

        Player player = getPlayerIfOnline();

        // Possible helmet data value : 1, 3, 4, 5, 6, 10, 11, 12, 14

        ItemStack helmet = new ItemStack(Material.CARPET, 1, getArmorValue().getHelmetDataValue());

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        Utils.setLeatherArmorColor(chestplate, getArmorValue().getArmorColor());

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        Utils.setLeatherArmorColor(boots, Color.fromRGB(242, 127, 165));

        if ("Azuxul".equals(player.getName())) {
            helmet.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
            chestplate.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
            boots.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
        }

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setBoots(boots);
    }

    public void update() {

        if (hasPowerup(Powerups.INVISIBILITY))
            gameManager.getServer().getScheduler().runTask(gameManager.getPlugin(), () -> getPlayerIfOnline().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, powerupDuration * 20, 1), true));

        if (powerups != null && powerups.getDuration() > 0 && --powerupDuration <= 0) {

            if (hasPowerup(Powerups.WALL_BUILDER)) {
                setPlacedBombs(0);
            } else if (hasPowerup(Powerups.INVISIBILITY)) {
                setArmor();
            }

            powerups = null;
            updateInventory();
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

    public Powerups getPowerups() {
        return powerups;
    }

    public void setPowerup(Powerups powerups) {

        this.powerups = powerups;
        this.powerupDuration = powerups.getDuration();
        this.updateInventory();
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
        return gameManager.getMapManager().getCaseAtWorldLocation(loc.getBlockX(), loc.getBlockZ());
    }

    public void updateInventory() {

        // Generate item for display bombs number
        int itemBombNb = bombNumber > 64 ? 64 : bombNumber;
        ItemStack itemBombNumber = new ItemStack(Material.DIAMOND_BLOCK, itemBombNb);
        ItemMeta itemMeta = itemBombNumber.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "Nombre de bombes : " + ChatColor.GOLD + bombNumber);
        itemBombNumber.setItemMeta(itemMeta);

        // Generate item for display explosion radius
        int itemRadiusNb = radius > 64 ? 64 : radius;
        ItemStack itemRadiusNbumber = new ItemStack(Material.EMERALD_BLOCK, itemRadiusNb);
        itemMeta = itemRadiusNbumber.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET.toString() + ChatColor.GREEN + "Puissance : " + ChatColor.GOLD + itemRadiusNb);
        itemRadiusNbumber.setItemMeta(itemMeta);

        // Generate main item
        boolean bomb = hasPowerup(Powerups.WALL_BUILDER);

        ItemStack mainItem = new ItemStack(bomb ? Material.BRICK : Material.CARPET, 1, (short) (bomb ? 0 : 8));
        itemMeta = mainItem.getItemMeta();
        NBTTagCompound nbtTagCompound;
        net.minecraft.server.v1_9_R2.ItemStack mainItemNMS;

        itemMeta.setDisplayName(bomb ? ChatColor.GOLD + "Constructeur" : ChatColor.GOLD + "Bombe");
        mainItem.setItemMeta(itemMeta);
        mainItem.setAmount(getBombNumber() - getPlacedBombs());

        mainItemNMS = CraftItemStack.asNMSCopy(mainItem);
        nbtTagCompound = mainItemNMS.getTag() != null ? mainItemNMS.getTag() : new NBTTagCompound();

        NBTTagList nbtTagList = new NBTTagList();

        nbtTagList.add(new NBTTagString("minecraft:stone"));
        nbtTagList.add(new NBTTagString("minecraft:stained_hardened_clay"));

        nbtTagCompound.set(NBTTags.CAN_PLACE_ON.getName(), nbtTagList);

        mainItemNMS.setTag(nbtTagCompound);
        mainItem = CraftItemStack.asBukkitCopy(mainItemNMS);

        updateInventoryBoosterStatus();

        Player player = getPlayerIfOnline();

        if (player != null) {
            Inventory inventory = player.getInventory();

            inventory.setItem(8, itemRadiusNbumber);
            inventory.setItem(7, itemBombNumber);
            inventory.setItem(0, mainItem);
        }

    }

    public void updateInventoryBoosterStatus() {

        Inventory inventory = getPlayerIfOnline().getInventory();
        Iterator<Powerups> powerupsIterator = persistentPowerups.iterator();

        for (int i = 0; i <= 3; i++) {

            inventory.setItem(2 + i, null);
        }

        for (int i = 0; i <= persistentPowerups.size() - 1; i++) {

            inventory.setItem(2 + i, powerupsIterator.next().getIcon());
        }
    }

    public void swap() {

        Player player = getPlayerIfOnline();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());
        Collection<PlayerBomberman> playerBombermanCollection = gameManager.getInGamePlayers().values();

        List<PlayerBomberman> swappablePlayers = playerBombermanCollection.stream().filter(playerFilter -> !playerFilter.equals(playerBomberman) && playerFilter.getPlayerIfOnline() != null).collect(Collectors.toList());

        if (swappablePlayers.isEmpty()) {
            player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.RED + "Il n'y pas de joueur avec qui swap !");
        } else {

            PlayerBomberman playerSwapBomberman = swappablePlayers.get(RandomUtils.nextInt(swappablePlayers.size())); // Get random player

            Player playerSwap = playerSwapBomberman.getPlayerIfOnline();

            Location locationOfPlayerSwap = playerSwap.getLocation();
            Location locationOfPlayer = player.getLocation();

            player.teleport(locationOfPlayerSwap);
            playerSwap.teleport(locationOfPlayer);

            playerSwap.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + "Vous avez été swap avec un autre joueur");
            player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + "Vous avez été swap avec un autre joueur");
        }
    }

    public boolean die() {

        final int newHealth = getHealth() - 1;
        Player player = getPlayerIfOnline();

        if (player == null) {
            return false;
        }

        if (powerups != null && hasPowerup(Powerups.EXPLOSION_KILL)) {
            gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> explode(3), 1L);
        }

        if (newHealth > 0) {

            setHealth(newHealth);
            player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.RED + ChatColor.BOLD + "Il vous reste " + newHealth + " vie(s) !");

        } else if (hasPowerup(Powerups.BOMB_PROTECTION)) {

            setHealth(getHealth());
            player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.RED + "Vous avez perdu votre booster " + ChatColor.GOLD + Powerups.BOMB_PROTECTION.getName() + ChatColor.RED + " !");
            getPersistentPowerups().remove(Powerups.BOMB_PROTECTION);
            updateInventoryBoosterStatus();

        } else {

            setHealth(0);
            player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.RED + "Vous etes mort: vous n'avez plus de vies !");
            setSpectator();
            playMusic(Music.DEATH, player.getLocation());
            setRespawnLocation(player.getLocation());

            if (gameManager.getConnectedPlayers() <= 1)
                gameManager.endGame();
        }

        return true;
    }

    public void explode(int radius) {

        Location baseLocation = getPlayerIfOnline().getLocation();

        baseLocation.getWorld().playSound(baseLocation, Sound.ENTITY_GENERIC_EXPLODE, 10.0f, 20.0f);

        int minX = radius * -1;

        for (int x = minX; x <= radius; x++) {

            int minZ = Math.abs(x) - radius;
            int maxZ = Math.abs(minZ);

            for (int z = minZ; z <= maxZ; z++) {
                CaseMap explodeCase = gameManager.getMapManager().getCaseAtWorldLocation(baseLocation.getBlockX() + x, baseLocation.getBlockZ() + z);

                if (explodeCase != null) {
                    explodeCase.explodeCase(true, this, 0);
                }
            }
        }
    }

    public void stopWaitingRecord(Location spawn) {

        CraftPlayer craftPlayer = (CraftPlayer) getPlayerIfOnline();
        PacketPlayOutWorldEvent packetPlayOutWorldEvent = new PacketPlayOutWorldEvent(1010, new BlockPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()), 0, false);

        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutWorldEvent);
    }

    public void playMusic(Music music, Location location) {

        if (playMusic) {
            CraftPlayer craftPlayer = (CraftPlayer) getPlayerIfOnline();
            PacketPlayOutWorldEvent packetPlayOutWorldEvent = new PacketPlayOutWorldEvent(1010, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), music.getRecordId(), false);

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
        return hasPowerup(Powerups.RANDOM_FUSE) ? (RandomUtils.nextInt(4) + 1) * 20 : 50;
    }
}
