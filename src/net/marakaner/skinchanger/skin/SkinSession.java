package net.marakaner.skinchanger.skin;

import com.mojang.authlib.properties.Property;
import net.marakaner.skinchanger.SkinChanger;
import net.marakaner.skinchanger.utils.ReflectUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SkinSession extends ReflectUtil {

    private UUID uniqueId;
    private Skin skin;

    private Player player;

    public SkinSession(Player player) {
        this.uniqueId = player.getUniqueId();
        this.player = player;
    }

    public void setSkin(String skinOwner) {
        this.skin = new Skin(skinOwner);

        destroyPlayer();

        Bukkit.getScheduler().runTaskLater(SkinChanger.getInstance(), () -> buildPlayer(), 5);
    }

    public void setSkin(Skin skin) {
        this.skin = skin;

        destroyPlayer();

        Bukkit.getScheduler().runTaskLater(SkinChanger.getInstance(), () -> buildPlayer(), 5);
    }

    private void buildPlayer() {

        CraftPlayer craftPlayer = (CraftPlayer) this.player;

        EnumDifficulty enumDifficulty = EnumDifficulty.getById(craftPlayer.getWorld().getDifficulty().getValue());
        WorldType worldType = WorldType.getType(craftPlayer.getWorld().getWorldType().getName());
        WorldSettings.EnumGamemode enumGamemode = WorldSettings.EnumGamemode.getById(craftPlayer.getGameMode().getValue());
        boolean flyEnabled = craftPlayer.getAllowFlight();
        int slot = craftPlayer.getInventory().getHeldItemSlot();

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(0, enumDifficulty, worldType, enumGamemode);
        PacketPlayOutPosition position = new PacketPlayOutPosition(craftPlayer.getLocation().getX(), craftPlayer.getLocation().getY(), craftPlayer.getLocation().getZ(), craftPlayer.getLocation().getYaw(), craftPlayer.getLocation().getPitch(), new HashSet<>());

        sendPacket(respawn, player);
        sendPacket(position, player);

        player.updateInventory();
        player.setAllowFlight(flyEnabled);
        player.getInventory().setHeldItemSlot(slot);

        org.bukkit.Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        for(int x=-10; x<10; x++)
            for(int z=-10; z<10; z++)
                player.getWorld().refreshChunk(chunk.getX() + x, chunk.getZ() + z);

        PacketPlayOutPlayerInfo addTab = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle());
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle());

        for(Player all : Bukkit.getOnlinePlayers()) {
            sendPacket(addTab, all);
            if(all.getUniqueId() != this.player.getUniqueId()) {
                sendPacket(spawn, all);
            }
        }

        showPlayer();

    }

    private void destroyPlayer() {

        hidePlayer();

        CraftPlayer craftPlayer = (CraftPlayer) this.player;

        Collection<Property> properties = this.skin.getProperties();

        craftPlayer.getProfile().getProperties().removeAll("textures");
        craftPlayer.getProfile().getProperties().putAll("textures", properties);

        PacketPlayOutPlayerInfo removeTab = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle());
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(craftPlayer.getEntityId());

        for(Player all : Bukkit.getOnlinePlayers()) {
            sendPacket(removeTab, all);
            if(all.getUniqueId() != this.player.getUniqueId()) {
                sendPacket(destroy, all);
            }
        }
    }

    private void showPlayer() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.showPlayer(player);
        }
    }

    private void hidePlayer() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.hidePlayer(player);
        }
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Player getPlayer() {
        return player;
    }

    public Skin getSkin() {
        return skin;
    }
}
