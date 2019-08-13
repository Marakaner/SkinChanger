package net.marakaner.skinchanger;

import net.marakaner.skinchanger.commands.SkinCommand;
import net.marakaner.skinchanger.commands.SkinResetCommand;
import net.marakaner.skinchanger.commands.SkinSetCommand;
import net.marakaner.skinchanger.listener.JoinListener;
import net.marakaner.skinchanger.listener.QuitListener;
import net.marakaner.skinchanger.skin.SkinManager;
import net.marakaner.skinchanger.sql.SQLManager;
import net.marakaner.skinchanger.user.UserManager;
import net.marakaner.skinchanger.utils.MessageUtil;
import net.marakaner.skinchanger.utils.UpdateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SkinChanger extends JavaPlugin {

    private static SkinChanger instance;

    private SkinManager skinManager;
    private SQLManager sqlManager;
    private UserManager userManager;

    private String prefix;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getConsoleSender().sendMessage("§3SkinChanger by §4Marakaner §3is §aenabled.");

        loadConfig();

        if(getConfig().getBoolean("MySQL.Enabled")) {
            userManager = new UserManager(this.sqlManager);
            skinManager = new SkinManager(this.sqlManager);

            registerListener();

            registerCommands();

            registerMessages();
        }

    }

    @Override
    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage("§3SkinChanger by §4Marakaner §3is §cdisabled");

    }

    private void registerMessages() {
        Map<String, String> messages = new HashMap();

        FileConfiguration cfg = getConfig();

        try {
            messages.put("Messages.skin_set", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_set")));
            messages.put("Messages.skin_reset", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_reset")));
            messages.put("Messages.skin_set_other", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_set_other")));
            messages.put("Messages.skin_reset_other", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_reset_other")));
            messages.put("Messages.skin_not_available", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_not_available")));
            messages.put("Messages.skin_loaded", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_loaded")));
            messages.put("Messages.no_permission", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.no_permission")));
            messages.put("Messages.skin_not_changed", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_not_changed")));
            messages.put("Messages.no_command", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.no_command")));
            messages.put("Messages.skin_not_changed_other", ChatColor.translateAlternateColorCodes('&', cfg.getString("Messages.skin_not_changed_other")));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cEine Nachricht konnte nicht geladen werde. Lösche die 'config.yml' und starte den Server neu um diesen Fehler zu beheben!");
        }

        MessageUtil.registerMessages(messages);
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("skinreset").setExecutor(new SkinResetCommand());
        Bukkit.getPluginCommand("skin").setExecutor(new SkinCommand());
        Bukkit.getPluginCommand("skinset").setExecutor(new SkinSetCommand());
    }

    private void loadConfig() {

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        FileConfiguration cfg = getConfig();

        prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("Prefix")) + " ";

        if (cfg.getBoolean("MySQL.Enabled")) {
            sqlManager = new SQLManager(cfg.getString("MySQL.Host"),
                    cfg.getString("MySQL.Port"),
                    cfg.getString("MySQL.Database"),
                    cfg.getString("MySQL.User"),
                    cfg.getString("MySQL.Password"));
            sqlManager.connect();

            sqlManager.executeUpdate("CREATE TABLE IF NOT EXISTS user_skin(UUID VARCHAR(36), skin_value VARCHAR(1000), skin_signature VARCHAR(1000))");
            sqlManager.executeUpdate("CREATE TABLE IF NOT EXISTS user_info(UUID VARCHAR(36), username VARCHAR(100), skin_value VARCHAR(1000), skin_signature VARCHAR(1000))");
        } else {
            Bukkit.getConsoleSender().sendMessage("§4Du musst die MySQL aktivieren um das Plugin SkinChanger zu benutzen.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static SkinChanger getInstance() {
        return instance;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public String getPrefix() {
        return prefix;
    }
}
