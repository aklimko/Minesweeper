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

import static game.Level.*;

class Settings {
    private static Level currentLevel;
    private static boolean saferFirstClick, safeReveal;

    static void loadFromFile() {
        try {
            String fileData = new String(Files.readAllBytes(Paths.get("settings.json")));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode settings = objectMapper.readTree(fileData);
            currentLevel = Level.getLevelFromInt(settings.get("startingLevel").asInt());
            saferFirstClick = settings.get("saferFirstClick").asBoolean();
            safeReveal = settings.get("safeReveal").asBoolean();
        } catch (IOException e) {
            loadFailed();
        }
    }

    private static void loadFailed() {
        currentLevel = EASY;
        saferFirstClick = true;
        safeReveal = true;
    }

    static void checkIfChanged() {
        try {
            String fileData = new String(Files.readAllBytes(Paths.get("settings.json")));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode settings = objectMapper.readTree(fileData);
            if (currentLevel.getValue() != settings.get("startingLevel").asInt()
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
            generator.writeNumberField("startingLevel", currentLevel.getValue());
            generator.writeBooleanField("saferFirstClick", saferFirstClick);
            generator.writeBooleanField("safeReveal", safeReveal);
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            // Can't do anything
        }
    }

    static boolean isSaferFirstClick() {
        return saferFirstClick;
    }

    static void setSaferFirstClick(boolean saferFirstClick) {
        Settings.saferFirstClick = saferFirstClick;
    }

    static boolean isSafeReveal() {
        return safeReveal;
    }

    static void setSafeReveal(boolean safeReveal) {
        Settings.safeReveal = safeReveal;
    }

    static Level getCurrentLevel() {
        return currentLevel;
    }

    static void setCurrentLevel(Level currentLevel) {
        Settings.currentLevel = currentLevel;
    }
}