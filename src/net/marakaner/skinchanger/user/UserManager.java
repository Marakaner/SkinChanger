package net.marakaner.skinchanger.user;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.marakaner.skinchanger.sql.SQLManager;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class UserManager {

    private SQLManager sqlManager;

    public UserManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public void isRegistered(UUID uuid, Consumer<Boolean> consumer) {
        this.sqlManager.executeQuery("SELECT * FROM user_info WHERE UUID = '" + uuid.toString() + "'", map -> {
            if(map.isEmpty()) {
                consumer.accept(false);
            } else {
                consumer.accept(true);
            }
        }, "UUID");
    }

    public void register(Player player) {
        isRegistered(player.getUniqueId(), registered -> {

            GameProfile profile = ((CraftPlayer) player).getProfile();
            Property properties = profile.getProperties().get("textures").iterator().next();

            if(!registered) {
                sqlManager.executeUpdate("INSERT INTO user_info (UUID, username, skin_value, skin_signature) VALUES ("
                + "'" + player.getUniqueId().toString() + "', "
                + "'" + player.getName() + "', "
                + "'" + properties.getValue() + "', "
                + "'" + properties.getSignature() + "')");
            } else {
                sqlManager.executeUpdate("UPDATE user_info SET "
                + "skin_value='" + properties.getValue() + "', "
                + "skin_signature='" + properties.getSignature() + "', "
                + "username='" + player.getName() + "'"
                + "WHERE UUID='" + player.getUniqueId().toString() + "'");
            }
        });
    }

}
