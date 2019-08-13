package net.marakaner.skinchanger.utils;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder <K,V> {

    private Map<K, V> map;

    public MapBuilder() {
        this.map = new HashMap<>();
    }

    public MapBuilder add(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public MapBuilder remove(K key) {
        this.map.remove(key);
        return this;
    }

    public Map finish() {
        return map;
    }

}
