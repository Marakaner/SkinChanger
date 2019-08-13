package net.marakaner.skinchanger.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.marakaner.skinchanger.utils.GameProfileBuilder;
import net.marakaner.skinchanger.utils.UUIDFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Skin {

    private String value;
    private String signature;

    public Skin(String owner) {
        UUID uniqueId;

        try {
            uniqueId = UUIDFetcher.getUUID(owner);
        } catch (Exception e) {
            return;
        }

        if(uniqueId == null) {
            return;
        }

        try {
            GameProfile gameProfile = GameProfileBuilder.fetch(uniqueId);
            Collection<Property> properties = gameProfile.getProperties().get("textures");

            for(Property all : properties) {
                this.value = all.getValue();
                this.signature = all.getSignature();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Skin(UUID uniqueId) {
        try {
            GameProfile gameProfile = GameProfileBuilder.fetch(uniqueId);
            Collection<Property> properties = gameProfile.getProperties().get("textures");

            for(Property all : properties) {
                this.value = all.getValue();
                this.signature = all.getSignature();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Skin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public Property getProperty() {
        return new Property("textures", this.value, this.signature);
    }

    public Collection<Property> getProperties() {
        Collection<Property> properties = new ArrayList<>();
        properties.add(getProperty());
        return properties;
    }

    public String getSignature() {
        return signature;
    }

    public String getValue() {
        return value;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
