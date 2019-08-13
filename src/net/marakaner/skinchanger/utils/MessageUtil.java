package net.marakaner.skinchanger.utils;

import java.util.Map;

public class MessageUtil {

    private static Map<String, String> messages;

    public static void registerMessages(Map<String, String> messages) {
        MessageUtil.messages = messages;
    }

    public static String getMessage(String id, Map<String, String> replacements) {
        String message = MessageUtil.messages.get(id);
        for(String all : replacements.keySet()) {
            message = message.replaceAll(all, replacements.get(all));
        }
        return message;
    }

    public static String getMessage(String id) {
        return messages.get(id);
    }

}
