package net.marakaner.skinchanger.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateUtils {

    private boolean version;

    public boolean isNewestVersion(){
        return version;
    }

    public void checkForUpdate(final URL url, final String pluginName, final String prefix, final Plugin plugin) throws IOException {

        BufferedReader bufferedReader = null;
        final StringBuilder stringBuilder = new StringBuilder();

        try{

            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            int count;
            final char[] data = new char[5000];
            while ((count = bufferedReader.read(data)) != -1){
                stringBuilder.append(data,0 ,count);
            }

        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(bufferedReader);
        }

        final String string = stringBuilder.toString();

        if(string.contains(plugin.getDescription().getVersion())){
            Bukkit.getConsoleSender().sendMessage(prefix + pluginName + " §7ist up to date§8.");
            version = true;
        } else {
            Bukkit.getConsoleSender().sendMessage(prefix + "§7Ein Update des §a" + pluginName + "§7 Plugins wurde gefunden!");
            Bukkit.getConsoleSender().sendMessage(prefix + "§7Informationen: §6" + stringBuilder.toString());
            Bukkit.getConsoleSender().sendMessage(prefix + "§7Du kannst dir das §aPlugin §7erneut herunterladen und erhältst die neuste Version§8.");
            version = false;
        }

    }

}

