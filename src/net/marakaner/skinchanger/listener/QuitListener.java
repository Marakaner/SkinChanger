package net.marakaner.skinchanger.listener;

import net.marakaner.skinchanger.SkinChanger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SkinChanger.getInstance().getSkinManager().unregisterPlayer(event.getPlayer().getUniqueId());
    }

}
