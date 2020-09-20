package com.adamjrehm.radarsim.helpers;

import com.adamjrehm.radarsim.config.Configuration;

public class GameInfo {

    //Screen settings
    public static final String TITLE = "Tower Radar Simulator";
    public static final int VIRTUAL_WIDTH = 2500;
    public static final int VIRTUAL_HEIGHT = 1250;
    public static final int ASPECT_RATIO = 2/1;
    public static final double SCALE_WIDTH = (double)Configuration.getWindowWidth() / VIRTUAL_WIDTH;
    public static final double SCALE_HEIGHT = (double)Configuration.getWindowHeight() / VIRTUAL_HEIGHT;

    //Asset settings
    public static final String SIM_BACKGROUND_IMAGE_PATH = "images/Sim BG.png";
    public static final String GROUND_IMAGE_PATH = "images/groundHD.png";
    public static final String RADAR_IMAGE_PATH = "images/DBritezoom.png";
    public static final String TARGET_IMAGE_PATH = "images/target.png";

    public static final double PIXELS_PER_MILE_RADAR = 56.1;
}
