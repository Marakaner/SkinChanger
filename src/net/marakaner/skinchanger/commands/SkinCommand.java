package net.marakaner.skinchanger.commands;

import net.marakaner.skinchanger.SkinChanger;
import net.marakaner.skinchanger.skin.Skin;
import net.marakaner.skinchanger.utils.MapBuilder;
import net.marakaner.skinchanger.utils.MessageUtil;
import net.marakaner.skinchanger.utils.UUIDFetcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(player.hasPermission("skinchanger.changeskin")) {
            if (args.length == 0) {
                player.sendMessage(SkinChanger.getInstance().getPrefix() + "§3Hilfe zum Wechseln des Skins");
                player.sendMessage(SkinChanger.getInstance().getPrefix() + "§4/skin [Name] §8- §3Wechsel deinen Skin permanent zum dem Skin von dem Spieler§8.");
            } else if (args.length == 1) {

                UUID uniqueId = UUIDFetcher.getUUID(args[0]);

                if(uniqueId == null) {
                    player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_available", new MapBuilder<String, String>().add("%NAME%", args[0]).finish()));
                    return false;
                }

                Skin skin = new Skin(uniqueId);
                SkinChanger.getInstance().getSkinManager().setSkin(player, skin);
                SkinChanger.getInstance().getSkinManager().saveSkin(player.getUniqueId(), skin);
                player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_set", new MapBuilder<String, String>().add("%NAME%", args[0]).finish()));

            } else {
                player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.no_command"));
            }
        } else {
            player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.no_permission", new MapBuilder<String, String>().add("%COMMAND%", command.getName()).finish()));
        }

        return true;
    }

}
