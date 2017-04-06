package game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings {
    private static boolean saferFirstClick, safeReveal;
    private static int currentLevel;
    private static int configNumber;
    
    public static void settingsLoadFailed(){
        saferFirstClick = true;
        safeReveal = true;
        currentLevel = 1;
    }
    
    public static void loadSettingsFromFile() {
        RandomAccessFile raf = null;
        configNumber = 0;
        try {
            raf = new RandomAccessFile("config.cfg", "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            configNumber = Integer.parseInt(raf.readLine());
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            settingsLoadFailed();
        }
        try {
            raf.close();
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        applySettings(configNumber);
    }
    
    public static void applySettings(int configNumber){
        saferFirstClick = (configNumber%2 != 0);
        safeReveal = ((configNumber >> 1)%2 != 0);
        currentLevel = (configNumber >> 2)%2 + (configNumber >> 3)%2*2;
    }
    
    public static void checkSettingsIfChanged(){
        int checkNumber = 0, multiplier = 1;
        checkNumber += (saferFirstClick ? 1 : 0) * multiplier;
        multiplier = multiplier << 1;
        checkNumber += (safeReveal ? 1 : 0) * multiplier;
        multiplier = multiplier << 1;
        checkNumber += currentLevel%2*multiplier + ((currentLevel >> 1)%2)*(multiplier << 1);
        if (checkNumber != configNumber) {
            saveSettings(checkNumber);
        }
    }
    
    public static void saveSettings(int checkNumber){
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile("config.cfg", "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            raf.setLength(0);
            raf.writeBytes(Integer.toString(checkNumber));
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            raf.close();
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
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