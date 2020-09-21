package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.adamjrehm.radarsim.planes.PlaneController;
import com.adamjrehm.radarsim.scenes.Gameplay;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SimInfo {

    private RadarSim sim;
    private Gameplay gameplay;
    private PlaneController controller;

    private Stage stage;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture buttonUpTexture = new Texture("images/buttonup.png"),
    buttonDownTexture = new Texture("images/buttondown.png");

    private Container<VerticalGroup> container;
    private VerticalGroup simInfoVerticalGroup;

    private BitmapFont font = new BitmapFont();

    private Label operationsLabel, timeElapsedLabel;

    public SimInfo(RadarSim sim, Gameplay gameplay){
        this.sim = sim;
        this.gameplay = gameplay;
        this.controller = gameplay.getPlaneController();

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameInfo.VIRTUAL_WIDTH, GameInfo.VIRTUAL_HEIGHT, camera);
        this.viewport.getCamera().position.set(GameInfo.VIRTUAL_WIDTH / 2, GameInfo.VIRTUAL_HEIGHT / 2, 0);

        this.stage = new Stage(viewport);

        this.buttonDownTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.buttonUpTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        this.simInfoVerticalGroup = populateSimInfoAndControls();

        this.container = new Container<>();
        container.setPosition(2250, 0, Align.bottomRight);
        container.setSize(GameInfo.VIRTUAL_WIDTH / 11f, GameInfo.VIRTUAL_HEIGHT / 4f);
        container.align(Align.bottomRight);
        container.setActor(simInfoVerticalGroup);
        container.setVisible(true);

        stage.addActor(container);
    }

    public void render(){
        operationsLabel.setText("Operations: " + controller.getOperationsCounter());
        timeElapsedLabel.setText("Time Elapsed: " + gameplay.getTimeElapsed());

        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    public void dispose(){
        stage.dispose();
        buttonUpTexture.dispose();
        buttonDownTexture.dispose();
    }

    private VerticalGroup populateSimInfoAndControls(){
        final VerticalGroup t = new VerticalGroup();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.background = new SpriteDrawable(new Sprite(buttonUpTexture));
        labelStyle.fontColor = Color.LIGHT_GRAY;
        labelStyle.font = font;

        operationsLabel = new Label("Operations: " + controller.getOperationsCounter(), labelStyle);
        operationsLabel.setAlignment(Align.center);

        timeElapsedLabel = new Label("Time Elapsed: " + gameplay.getTimeElapsed(), labelStyle);
        timeElapsedLabel.setAlignment(Align.center);

        t.addActor(operationsLabel);
        t.addActor(timeElapsedLabel);

        final TextButton pauseButton = new TextButton("Pause", getButtonStyle(Color.YELLOW));

        final TextButton resumeButton = new TextButton("Resume", getButtonStyle(Color.GREEN));

//        final TextButton restartButton = new TextButton("Restart", getButtonStyle(Color.FIREBRICK));

        pauseButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                controller.pause();
                t.addActor(resumeButton);
                t.removeActor(pauseButton);
            }
        });
        t.addActor(pauseButton);

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.resume();
                t.addActor(pauseButton);
                t.removeActor(resumeButton);
            }
        });

//        restartButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                gameplay = null;
//                sim.setScreen(new Gameplay(sim));
//            }
//        });
//        t.addActor(restartButton);

        return t;

    }

    private TextButton.TextButtonStyle getButtonStyle(Color color){
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new SpriteDrawable(new Sprite(buttonUpTexture));
        buttonStyle.down = new SpriteDrawable(new Sprite(buttonDownTexture));
        buttonStyle.font = font;
        buttonStyle.fontColor = color;

        return buttonStyle;
    }

    public Stage getStage(){
        return this.stage;
    }
}
