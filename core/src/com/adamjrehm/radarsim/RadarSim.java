package com.adamjrehm.radarsim;

import com.adamjrehm.radarsim.config.CallsignManager;
import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.scenes.MainMenu;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.File;

public class RadarSim extends Game {
    private SpriteBatch batch;

    //private SpeechRecognizer speechRec;


    @Override
    public void create() {
        if (!loadConfiguration()) {
            System.out.println("Configurations generated, please setup your preferences.");
            System.exit(0);
        }

        Gdx.graphics.setWindowedMode(Configuration.getWindowWidth(), Configuration.getWindowHeight());
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        batch = new SpriteBatch();
        setScreen(new MainMenu(this));

		//TODO: Implement speech recognition for single person mode
		//speechRec = new SpeechRecognizer();
    }

    @Override
    public void render() {
        super.render();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    private boolean loadConfiguration(){
        boolean a = Configuration.getInstance().init();
        boolean b = CallsignManager.getInstance().load();

        return a && b;
    }
}


/**
 * Credit to Awesome Tuts on Udemy for the structure & many features of this program
 * https://www.udemy.com/course/the-complete-libgdx-game-course/
 */