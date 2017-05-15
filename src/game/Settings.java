package game;

import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Settings {
    private static int currentLevel;
    private static boolean saferFirstClick, safeReveal;

    private static void loadFailed(){
        currentLevel = 1;
        saferFirstClick = true;
        safeReveal = true;
    }

    public static void loadFromFile(){
        JSONParser parser = new JSONParser();

        try {
            JSONObject object = (JSONObject) parser.parse(new FileReader("settings.json"));
            currentLevel = ((Long) object.get("startingLevel")).intValue();
            saferFirstClick = (boolean) object.get("saferFirstClick");
            safeReveal = (boolean) object.get("safeReveal");
        } catch (IOException | ParseException e) {
            loadFailed();
        }
    }

    public static void checkIfChanged(){
        JSONParser parser = new JSONParser();

        try {
            JSONObject object = (JSONObject) parser.parse(new FileReader("settings.json"));
            if(currentLevel != ((Long)object.get("startingLevel")).intValue()
                    || saferFirstClick != (boolean) object.get("saferFirstClick")
                    || safeReveal != (boolean) object.get("safeReveal")){
                save();
            }
        } catch (IOException | ParseException e) {
            save();
        }
    }

    private static void save(){
        JSONObject object = new JSONObject();
        object.put("startingLevel", currentLevel);
        object.put("saferFirstClick", saferFirstClick);
        object.put("safeReveal", safeReveal);

        try(FileWriter config = new FileWriter("settings.json")){
            config.write(object.toJSONString());
        } catch (IOException e) {
            //do nothing
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