package net.marakaner.skinchanger.commands;

import net.marakaner.skinchanger.SkinChanger;
import net.marakaner.skinchanger.utils.MapBuilder;
import net.marakaner.skinchanger.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkinResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("Diese Befehl kann nur von einem Spieler ausgeführt werden!");
            return false;
        }

        Player player = (Player) sender;

        if(player.hasPermission("skinchanger.changeskinother")) {
            if(args.length == 0) {
                if(SkinChanger.getInstance().getSkinManager().hasActiveSkin(player)) {
                    SkinChanger.getInstance().getSkinManager().resetSkin(player);
                    player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_reset", new MapBuilder<String, String>().add("%", "").finish()));
                } else {
                    player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_changed"));
                }
            } else if(args.length == 1) {
                Player target;

                if((target = Bukkit.getPlayer(args[0])) != null) {
                    if(SkinChanger.getInstance().getSkinManager().hasActiveSkin(target)) {
                        SkinChanger.getInstance().getSkinManager().resetSkin(target);
                        target.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_reset"));
                        player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_reset_other", new MapBuilder<String, String>().add("%PLAYER%", target.getName()).finish()));
                    } else {
                        player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_changed_other", new MapBuilder<String, String>().add("%PLAYER%", args[0]).finish()));
                    }
                } else {
                    SkinChanger.getInstance().getSqlManager().executeQuery("SELECT * FROM user_info WHERE username = '" + args[0] + "'", map -> {
                        if(!map.isEmpty()) {
                            UUID targetUniqueId = UUID.fromString(map.get("UUID").toString());
                            SkinChanger.getInstance().getSkinManager().hasSavedSkin(targetUniqueId, hasActive -> {
                                if(hasActive) {
                                    SkinChanger.getInstance().getSkinManager().deleteSkin(targetUniqueId);
                                    player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_reset_other", new MapBuilder<String, String>().add("%PLAYER%", args[0]).finish()));
                                } else {
                                    player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_changed_other", new MapBuilder<String, String>().add("%PLAYER%", args[0]).finish()));
                                }
                            });
                        } else {
                            player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_changed_other", new MapBuilder<String, String>().add("%PLAYER%", args[0]).finish()));
                        }
                    }, "UUID");
                }
            } else {
                player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.no_command"));
            }
        } else if(player.hasPermission("skinchanger.changeskin")) {
            if (args.length == 0) {
                if(SkinChanger.getInstance().getSkinManager().hasActiveSkin(player)) {
                    SkinChanger.getInstance().getSkinManager().resetSkin(player);
                    player.sendMessage(MessageUtil.getMessage(SkinChanger.getInstance().getPrefix() + "Messages.skin_reset"));
                } else {
                    player.sendMessage(SkinChanger.getInstance().getPrefix() + MessageUtil.getMessage("Messages.skin_not_changed"));
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
