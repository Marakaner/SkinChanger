package net.marakaner.skinchanger.utils;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {

    public static Field modifiers = getField( Field.class, "modifiers" );

    public static Class< ? > getNMSClass(String name ) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
        try {
            return Class.forName( "net.minecraft.server." + version + "." + name );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Packet packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void setField( Object change, String name, Object to) throws Exception {
        Field field = change.getClass().getDeclaredField( name );
        field.setAccessible( true );
        field.set( change, to );
        field.setAccessible( false );
    }

    public static Field getField( Class< ? > clazz, String name ) {
        try {
            Field field = clazz.getDeclaredField( name );
            field.setAccessible( true );
            if( Modifier.isFinal( field.getModifiers() ) ) {
                modifiers.set( field, field.getModifiers() & ~Modifier.FINAL );
            }
            return field;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
    }

}
