package net.marakaner.skinchanger.listener;

import net.marakaner.skinchanger.SkinChanger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        SkinChanger.getInstance().getUserManager().register(event.getPlayer());
        SkinChanger.getInstance().getSkinManager().setActiveSkin(event.getPlayer());
    }

}
