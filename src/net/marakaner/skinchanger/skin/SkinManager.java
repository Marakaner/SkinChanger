package net.marakaner.skinchanger.skin;

import net.marakaner.skinchanger.SkinChanger;
import net.marakaner.skinchanger.sql.SQLManager;
import net.marakaner.skinchanger.utils.MapBuilder;
import net.marakaner.skinchanger.utils.MessageUtil;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SkinManager {

    private SQLManager sqlManager;

    public SkinManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    private Map<UUID, SkinSession> skinSessions = new HashMap<>();

    public void setSkin(Player player, String name) {
        if(skinSessions.containsKey(player.getUniqueId())) {
            skinSessions.remove(player.getUniqueId());
        }

        SkinSession skinSession = new SkinSession(player);
        skinSession.setSkin(name);
        this.skinSessions.put(player.getUniqueId(), skinSession);
    }

    public void saveSkin(UUID player, Skin skin) {
        sqlManager.executeMultiUpdate("DELETE FROM user_skin WHERE UUID = '" + player.toString() + "'", "INSERT INTO user_skin(UUID, skin_value, skin_signature) VALUES ('" + player.toString() + "','" + skin.getValue() + "','" + skin.getSignature() + "')");
    }

    public void setSkin(Player player, Skin skin) {
        if(skinSessions.containsKey(player.getUniqueId())) {
            skinSessions.remove(player.getUniqueId());
        }

        SkinSession skinSession = new SkinSession(player);
        skinSession.setSkin(skin);
        this.skinSessions.put(player.getUniqueId(), skinSession);
    }

    public void resetSkin(Player player) {
        if(hasActiveSkin(player)) {
            sqlManager.executeQuery("SELECT * FROM user_info WHERE UUID = '" + player.getUniqueId().toString() + "'", map -> MinecraftServer.getServer().postToMainThread(() -> {
                skinSessions.get(player.getUniqueId()).setSkin(new Skin(String.valueOf(map.get("skin_value")), String.valueOf(map.get("skin_signature"))));
                this.skinSessions.remove(player.getUniqueId());
            }), "skin_value", "skin_signature");
            sqlManager.executeUpdate("DELETE FROM user_skin WHERE UUID = '" + player.getUniqueId().toString() + "'");
        }
    }

    public void deleteSkin(UUID player) {
        hasSavedSkin(player, hasSkin -> {
            if(hasSkin) {
                sqlManager.executeUpdate("DELETE FROM user_skin WHERE UUID = '" + player.toString() + "'");
            }
        });
    }

    public void unregisterPlayer(UUID uniqueId) {
        if(this.skinSessions.containsKey(uniqueId)) {
            this.skinSessions.remove(uniqueId);
        }
    }

    public void setActiveSkin(Player player) {
        sqlManager.executeQuery("SELECT * FROM user_skin WHERE UUID = '" + player.getUniqueId() + "'", map -> {
            if(map.isEmpty()) {
                return;
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setSkin(player, new Skin(String.valueOf(map.get("skin_value")), String.valueOf(map.get("skin_signature"))));
                        player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_loaded"));
                    }
                }.runTask(SkinChanger.getInstance());
            }
        }, "skin_value", "skin_signature");
    }

    public void hasSavedSkin(UUID uuid, Consumer<Boolean> consumer) {
        sqlManager.executeQuery("SELECT * FROM user_skin WHERE UUID = '" + uuid.toString() + "'", map -> {
            if(map.isEmpty()) {
                consumer.accept(false);
            } else {
                consumer.accept(true);
            }
        }, "skin_value");
    }

    public boolean hasActiveSkin(Player player) {
        return this.skinSessions.containsKey(player.getUniqueId());
    }

}
