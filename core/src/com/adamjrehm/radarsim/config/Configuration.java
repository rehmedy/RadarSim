package com.adamjrehm.radarsim.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.*;

public class Configuration {

    private static final Configuration INSTANCE = new Configuration();

    private final String configFilePath = "./configs/config.properties";

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
            uiScale,
            radarUpdateSpeed,
            planeAccelerationRate,
            planeDecelerationRate;


    private Configuration() {
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    public boolean init() {
        boolean a = loadAirportConfiguration();
        boolean b = UI.initAssets();

        return a && b;
    }

    public boolean loadAirportConfiguration() {
        System.out.println("Loading airport configuration...");

        if (new File(configFilePath).exists()) {
            try {
                PropertiesConfiguration prop = new PropertiesConfiguration(new File(configFilePath));

                updateOldConfigs(prop);

                airportName = prop.getString("AIRPORT_NAME");
                patternEntryOneName = prop.getString("PATTERN_ENTRY_ONE_NAME");
                patternEntryTwoName = prop.getString("PATTERN_ENTRY_TWO_NAME");
                patternEntryThreeName = prop.getString("PATTERN_ENTRY_THREE_NAME");
                patternEntryFourName = prop.getString("PATTERN_ENTRY_FOUR_NAME");
                patternEntryFiveName = prop.getString("PATTERN_ENTRY_FIVE_NAME");
                northSIDName = prop.getString("NORTH_SID_NAME");
                southSIDName = prop.getString("SOUTH_SID_NAME");

                patternEntryOneX = (prop.getInt("PATTERN_ENTRY_ONE_X"));
                patternEntryOneY = (prop.getInt("PATTERN_ENTRY_ONE_Y"));
                patternEntryTwoX = (prop.getInt("PATTERN_ENTRY_TWO_X"));
                patternEntryTwoY = (prop.getInt("PATTERN_ENTRY_TWO_Y"));
                patternEntryThreeX = (prop.getInt("PATTERN_ENTRY_THREE_X"));
                patternEntryThreeY = (prop.getInt("PATTERN_ENTRY_THREE_Y"));
                patternEntryFourX = (prop.getInt("PATTERN_ENTRY_FOUR_X"));
                patternEntryFourY = (prop.getInt("PATTERN_ENTRY_FOUR_Y"));
                patternEntryFiveX = (prop.getInt("PATTERN_ENTRY_FIVE_X"));
                patternEntryFiveY = (prop.getInt("PATTERN_ENTRY_FIVE_Y"));

                windowWidth = (prop.getInt("WINDOW_WIDTH"));
                windowHeight = (prop.getInt("WINDOW_HEIGHT"));
                radarUpdateSpeed = (prop.getInt("RADAR_UPDATE_SPEED"));
                planeAccelerationRate = (prop.getInt("ACCELERATION_RATE"));
                planeDecelerationRate = (prop.getInt("DECELERATION_RATE"));

                uiScale = (prop.getInt("UI_SCALE"));
            } catch (NullPointerException | ConfigurationException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            File newFile = new File(configFilePath);
            Gdx.files.internal("configs/config.properties").copyTo(new FileHandle(newFile));
            return false;
        }

    }

    private void updateOldConfigs(PropertiesConfiguration prop) throws ConfigurationException {
        boolean updated = false;

        // Check if any new properties don't exist, write if not
        if (prop.getProperty("UI_SCALE") == null){
            prop.addProperty("UI_SCALE", "2");
            updated = true;
        }

        // Store values & close output stream
        if (updated){
            prop.save();
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

    public static int getUiScale() {
        return uiScale;
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

    public static class UI {
        private static BitmapFont font;
        private static Sprite buttonUp;
        private static Sprite buttonDown;
        private static Sprite buttonChecked;
        private static Sprite buttonUpSlim;
        private static Sprite buttonDownSlim;
        private static Sprite strip;
        private static Sprite arrivalContainer;
        private static Sprite departureContainer;

        private static boolean initAssets() {
            initFont();

            Texture buttonUpTexture = new Texture("images/buttonup.png"),
                    buttonDownTexture = new Texture("images/buttondown.png"),
                    buttonCheckedTexture = new Texture("images/buttonchecked.png"),
                    buttonUpSlimTexture = new Texture("images/buttonupslim.png"),
                    buttonDownSlimTexture = new Texture("images/buttondownslim.png"),
                    stripTexture = new Texture("images/stripbackground.png"),
                    arrivalContainerTexture = new Texture("images/arrivalscontainerbackground.png"),
                    departureContainerTexture = new Texture("images/departurescontainerbackground.png");

            buttonUpTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            buttonDownTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            buttonCheckedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            buttonUpSlimTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            buttonDownSlimTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            stripTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            arrivalContainerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            departureContainerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            buttonUp = new Sprite(buttonUpTexture);
            buttonDown = new Sprite(buttonDownTexture);
            buttonChecked = new Sprite(buttonCheckedTexture);
            buttonUpSlim = new Sprite(buttonUpSlimTexture);
            buttonDownSlim = new Sprite(buttonDownSlimTexture);
            strip = new Sprite(stripTexture);
            arrivalContainer = new Sprite(arrivalContainerTexture);
            departureContainer = new Sprite(departureContainerTexture);


            return true;
        }

        private static void initFont() {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = (9 + (getUiScale() * 2));

            font = generator.generateFont(parameter);
            font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            generator.dispose();
        }

        public static void dispose() {
            font.dispose();
            buttonChecked.getTexture().dispose();
            buttonDown.getTexture().dispose();
            buttonDownSlim.getTexture().dispose();
            buttonUp.getTexture().dispose();
            buttonUpSlim.getTexture().dispose();
            strip.getTexture().dispose();
            arrivalContainer.getTexture().dispose();
            departureContainer.getTexture().dispose();
        }

        public static TextButton.TextButtonStyle getButtonStyle(boolean toggleable, boolean slim, Color color) {
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

            if (slim) {
                buttonStyle.up = new SpriteDrawable(buttonUpSlim);
                buttonStyle.down = new SpriteDrawable(buttonDownSlim);

            } else {
                buttonStyle.up = new SpriteDrawable(buttonUp);
                buttonStyle.down = new SpriteDrawable(buttonDown);
                if (toggleable)
                    buttonStyle.checked = new SpriteDrawable(buttonChecked);

            }
            scaleButtonStyle(buttonStyle);
            buttonStyle.font = font;
            buttonStyle.fontColor = color;

            return buttonStyle;
        }

        private static void scaleButtonStyle(TextButton.TextButtonStyle buttonStyle) {
            float currentWidth = buttonStyle.up.getMinWidth();
            float currentHeight = buttonStyle.up.getMinHeight();
            buttonStyle.up.setMinWidth(currentWidth * Configuration.UI.getScale());
            buttonStyle.up.setMinHeight(currentHeight * Configuration.UI.getScale());
            buttonStyle.down.setMinWidth(currentWidth * Configuration.UI.getScale());
            buttonStyle.down.setMinHeight(currentHeight * Configuration.UI.getScale());

            if (buttonStyle.checked != null) {
                buttonStyle.checked.setMinWidth(currentWidth * Configuration.UI.getScale());
                buttonStyle.checked.setMinHeight(currentHeight * Configuration.UI.getScale());
            }
        }

        public static float getScale() {
            return ((9 + (getUiScale() * 2)) / 16f);
        }

        public static BitmapFont getFont() {
            return font;
        }

        public static Sprite getButtonUpSprite() {
            return buttonUp;
        }

        public static Sprite getButtonDownSprite() {
            return buttonDown;
        }

        public static Sprite getButtonCheckedSprite() {
            return buttonChecked;
        }

        public static Sprite getButtonUpSlimSprite() {
            return buttonUpSlim;
        }

        public static Sprite getButtonDownSlimSprite() {
            return buttonDownSlim;
        }

        public static Sprite getStripSprite() {
            return strip;
        }

        public static Sprite getArrivalContainerSprite() {
            return arrivalContainer;
        }

        public static Sprite getDepartureContainerSprite() {
            return departureContainer;
        }
    }
}
