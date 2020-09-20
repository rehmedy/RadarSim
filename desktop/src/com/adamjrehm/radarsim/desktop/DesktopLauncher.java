package com.adamjrehm.radarsim.desktop;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(GameInfo.TITLE);
        config.setWindowedMode(1600, 800);
        config.setWindowPosition(100,100);

        new Lwjgl3Application(new RadarSim(), config);
    }
}
