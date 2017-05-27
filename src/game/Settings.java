package game;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class Settings {
    private static int currentLevel;
    private static boolean saferFirstClick, safeReveal;

    public static void loadFromFile() {
        try {
            String fileData = new String(Files.readAllBytes(Paths.get("settings.json")));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode settings = objectMapper.readTree(fileData);
            currentLevel = settings.get("startingLevel").asInt();
            saferFirstClick = settings.get("saferFirstClick").asBoolean();
            safeReveal = settings.get("safeReveal").asBoolean();
        } catch (IOException e) {
            loadFailed();
        }
    }

    private static void loadFailed() {
        currentLevel = 1;
        saferFirstClick = true;
        safeReveal = true;
    }

    public static void checkIfChanged() {
        try {
            String fileData = new String(Files.readAllBytes(Paths.get("settings.json")));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode settings = objectMapper.readTree(fileData);
            if (currentLevel != settings.get("startingLevel").asInt()
                    || saferFirstClick != settings.get("saferFirstClick").asBoolean()
                    || safeReveal != settings.get("safeReveal").asBoolean()) {
                save();
            }
        } catch (IOException e) {
            save();
        }
    }

    private static void save() {
        JsonFactory factory = new JsonFactory();
        try {
            JsonGenerator generator = factory.createGenerator(new File("settings.json"), JsonEncoding.UTF8);
            generator.writeStartObject();
            generator.writeNumberField("startingLevel", currentLevel);
            generator.writeBooleanField("saferFirstClick", saferFirstClick);
            generator.writeBooleanField("safeReveal", safeReveal);
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
        }
    }

    public static boolean isSaferFirstClick() {
        return saferFirstClick;
    }

    public static void setSaferFirstClick(boolean saferFirstClick) {
        Settings.saferFirstClick = saferFirstClick;
    }

    public static boolean isSafeReveal() {
        return safeReveal;
    }

    public static void setSafeReveal(boolean safeReveal) {
        Settings.safeReveal = safeReveal;
    }

    public static int getCurrentLevel() {
        return currentLevel;
    }

    public static void setCurrentLevel(int currentLevel) {
        Settings.currentLevel = currentLevel;
    }
}