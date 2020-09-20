package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.adamjrehm.radarsim.scenes.Gameplay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuButtons {

    private RadarSim radarSim;
    private Stage stage;
    private Viewport gameViewport;

    private ImageButton playBtn, optionsBtn, quitBtn;

    public MainMenuButtons(RadarSim sim){
        this.radarSim = sim;

        gameViewport = new FitViewport(GameInfo.VIRTUAL_WIDTH, GameInfo.VIRTUAL_HEIGHT, new OrthographicCamera());

        stage = new Stage(gameViewport, radarSim.getBatch());

        Gdx.input.setInputProcessor(stage);

        createAndPositionButtons();
        addAllListeners();

        stage.addActor(playBtn);
        stage.addActor(optionsBtn);
        stage.addActor(quitBtn);
    }

    private void createAndPositionButtons(){
        Texture playBtnTexture = new Texture("images/StartBtn.png");
        Texture optionsBtnTexture = new Texture("images/OptionsBtn.png");
        Texture quitBtnTexture = new Texture("images/QuitBtn.png");

        playBtnTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        optionsBtnTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        quitBtnTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        playBtn = new ImageButton(new SpriteDrawable(new Sprite(
                playBtnTexture)));

        optionsBtn = new ImageButton(new SpriteDrawable(new Sprite(
                optionsBtnTexture)));

        quitBtn = new ImageButton(new SpriteDrawable(new Sprite(
                quitBtnTexture)));

        playBtn.setPosition(GameInfo.VIRTUAL_WIDTH / 2f, GameInfo.VIRTUAL_HEIGHT / 2f, Align.center);

        optionsBtn.setPosition(GameInfo.VIRTUAL_WIDTH / 2f, GameInfo.VIRTUAL_HEIGHT / 2f - 120, Align.center);

        quitBtn.setPosition(GameInfo.VIRTUAL_WIDTH / 2f, GameInfo.VIRTUAL_HEIGHT / 2f - 240, Align.center);
    }

    private void addAllListeners(){
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                radarSim.setScreen(new Gameplay(radarSim));
                System.out.println("Starting simulator...");
            }
        });

        optionsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                //TODO: Implement options menu
                System.out.println("Opening options...");
            }
        });

        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                //TODO: Implement quit button
                System.out.println("Quitting game...");
                System.exit(0);
            }
        });
    }

    public Stage getStage(){
        return this.stage;
    }
}
