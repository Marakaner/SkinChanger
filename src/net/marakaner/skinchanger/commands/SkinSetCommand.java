package net.marakaner.skinchanger.commands;

import net.marakaner.skinchanger.SkinChanger;
import net.marakaner.skinchanger.skin.Skin;
import net.marakaner.skinchanger.utils.MapBuilder;
import net.marakaner.skinchanger.utils.MessageUtil;
import net.marakaner.skinchanger.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkinSetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(player.hasPermission("skinchanger.changeskinother")) {
            if (args.length == 0) {
                player.sendMessage(SkinChanger.getInstance().getPrefix() + "§3Hilfe zum Wechseln des Skins");
                player.sendMessage(SkinChanger.getInstance().getPrefix() + "§4/skin [Name] §8- §3Wechsel deinen Skin permanent zum dem Skin von dem Spieler§8.");
            } else if (args.length == 2) {

                Player target = Bukkit.getPlayer(args[0]);

                if(target != null) {

                    UUID uniqueId = UUIDFetcher.getUUID(args[1]);

                    if(uniqueId == null) {
                        player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_available", new MapBuilder<String, String>().add("%NAME%", args[0]).finish()));
                        return false;
                    }

                    Skin skin = new Skin(uniqueId);
                    SkinChanger.getInstance().getSkinManager().setSkin(target, skin);
                    SkinChanger.getInstance().getSkinManager().saveSkin(target.getUniqueId(), skin);
                    target.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_set", new MapBuilder<String, String>().add("%NAME%", args[1]).finish()));
                    player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_set_other", new MapBuilder<String, String>().add("%NAME%", args[1]).add("%PLAYER%", target.getName()).finish()));

                } else {

                    SkinChanger.getInstance().getSqlManager().executeQuery("SELECT * FROM user_info WHERE username = '" + args[0] + "'", map -> {
                        if (!map.isEmpty()) {
                            UUID targetUniqueId = UUID.fromString(map.get("UUID").toString());

                            UUID uniqueId = UUIDFetcher.getUUID(args[1]);

                            if(uniqueId == null) {
                                player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_available", new MapBuilder<String, String>().add("%NAME%", args[0]).finish()));
                                return;
                            }

                            Skin skin = new Skin(uniqueId);
                            SkinChanger.getInstance().getSkinManager().saveSkin(targetUniqueId, skin);
                            player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_set_other", new MapBuilder<String, String>().add("%NAME%", args[1]).add("%PLAYER%", args[0]).finish()));

                        } else {
                            player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_changed_other", new MapBuilder<String, String>().add("%PLAYER%", args[0]).finish()));
                        }
                    }, "UUID");
                }

            } else {
                player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.no_command"));
            }
        } else {

            player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.no_permission", new MapBuilder<String, String>().add("%COMMAND%", command.getName()).finish()));
        }


        return true;
    }
}
