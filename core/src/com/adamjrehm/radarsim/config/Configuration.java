package com.adamjrehm.radarsim.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.util.Properties;

public class Configuration {

    private static final Configuration INSTANCE = new Configuration();

    private static String airportName,
            patternEntryOneName,
            patternEntryTwoName,
            patternEntryThreeName,
            patternEntryFourName,
            patternEntryFiveName,
            northSIDName,
            southSIDName;

    private static int patternEntryOneX, patternEntryOneY,
            patternEntryTwoX, patternEntryTwoY,
            patternEntryThreeX, patternEntryThreeY,
            patternEntryFourX, patternEntryFourY,
            patternEntryFiveX, patternEntryFiveY,
            windowWidth,
            windowHeight,
            radarUpdateSpeed,
            planeAccelerationRate,
            planeDecelerationRate;


    private Configuration(){}

    public static Configuration getInstance(){
        return INSTANCE;
    }

    public boolean loadAirportConfiguration() {
        System.out.println("Loading airport configuration...");

        String configFilePath = "./configs/config.properties";

        Properties prop = new Properties();

        if (new File(configFilePath).exists()){
            try {
                FileInputStream inputStream = new FileInputStream(new File(configFilePath));
                prop.load(inputStream);

                airportName = prop.getProperty("AIRPORT_NAME");
                patternEntryOneName = prop.getProperty("PATTERN_ENTRY_ONE_NAME");
                patternEntryTwoName = prop.getProperty("PATTERN_ENTRY_TWO_NAME");
                patternEntryThreeName = prop.getProperty("PATTERN_ENTRY_THREE_NAME");
                patternEntryFourName = prop.getProperty("PATTERN_ENTRY_FOUR_NAME");
                patternEntryFiveName = prop.getProperty("PATTERN_ENTRY_FIVE_NAME");
                northSIDName = prop.getProperty("NORTH_SID_NAME");
                southSIDName = prop.getProperty("SOUTH_SID_NAME");

                patternEntryOneX = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_ONE_X"));
                patternEntryOneY = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_ONE_Y"));
                patternEntryTwoX = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_TWO_X"));
                patternEntryTwoY = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_TWO_Y"));
                patternEntryThreeX = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_THREE_X"));
                patternEntryThreeY = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_THREE_Y"));
                patternEntryFourX = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_FOUR_X"));
                patternEntryFourY = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_FOUR_Y"));
                patternEntryFiveX = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_FIVE_X"));
                patternEntryFiveY = Integer.parseInt(prop.getProperty("PATTERN_ENTRY_FIVE_Y"));

                windowWidth = Integer.parseInt(prop.getProperty("WINDOW_WIDTH"));
                windowHeight = Integer.parseInt(prop.getProperty("WINDOW_HEIGHT"));
                radarUpdateSpeed = Integer.parseInt(prop.getProperty("RADAR_UPDATE_SPEED"));
                planeAccelerationRate = Integer.parseInt(prop.getProperty("ACCELERATION_RATE"));
                planeDecelerationRate = Integer.parseInt(prop.getProperty("DECELERATION_RATE"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            File newFile = new File(configFilePath);
            Gdx.files.internal("configs/config.properties").copyTo(new FileHandle(newFile));
            return false;
        }

    }

    public static String getAirportName() {
        return airportName;
    }

    public static String getPatternEntryOneName() {
        return patternEntryOneName;
    }

    public static String getPatternEntryTwoName() {
        return patternEntryTwoName;
    }

    public static String getPatternEntryThreeName() {
        return patternEntryThreeName;
    }

    public static String getPatternEntryFourName() {
        return patternEntryFourName;
    }

    public static String getPatternEntryFiveName() {
        return patternEntryFiveName;
    }

    public static int getPatternEntryOneX() {
        return patternEntryOneX;
    }

    public static int getPatternEntryOneY() {
        return patternEntryOneY;
    }

    public static int getPatternEntryTwoX() {
        return patternEntryTwoX;
    }

    public static int getPatternEntryTwoY() {
        return patternEntryTwoY;
    }

    public static int getPatternEntryThreeX() {
        return patternEntryThreeX;
    }

    public static int getPatternEntryThreeY() {
        return patternEntryThreeY;
    }

    public static int getPatternEntryFourX() {
        return patternEntryFourX;
    }

    public static int getPatternEntryFourY() {
        return patternEntryFourY;
    }

    public static int getPatternEntryFiveX() {
        return patternEntryFiveX;
    }

    public static int getPatternEntryFiveY() {
        return patternEntryFiveY;
    }

    public static String getNorthSIDName() {
        return northSIDName;
    }

    public static String getSouthSIDName() {
        return southSIDName;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public static int getRadarUpdateSpeed() {
        return radarUpdateSpeed;
    }

    public static int getPlaneAccelerationRate() {
        return planeAccelerationRate;
    }

    public static int getPlaneDecelerationRate() {
        return planeDecelerationRate;
    }
}
