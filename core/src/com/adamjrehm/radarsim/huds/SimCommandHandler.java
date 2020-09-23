package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.planes.PlaneController;
import com.adamjrehm.radarsim.scenes.Gameplay;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;

public class SimCommandHandler extends Table {

    private Gameplay gameplay;
    private PlaneController controller;

    private Container<VerticalGroup> container;
    private VerticalGroup simInfoVerticalGroup;

    private Label operationsLabel, timeElapsedLabel, runningTimeLabel;

    public SimCommandHandler(Gameplay gameplay) {
        this.gameplay = gameplay;
        this.controller = gameplay.getPlaneController();

        this.simInfoVerticalGroup = populateSimInfoAndControls();

        this.container = new Container<>();
        container.setActor(simInfoVerticalGroup);

        add(container);
    }

    public void update() {
        operationsLabel.setText("Operations: " + controller.getOperationsCounter());
        timeElapsedLabel.setText("Total Time Elapsed: " + gameplay.getTotalTimeElapsed());
        runningTimeLabel.setText("Running Time: " + gameplay.getRunningTime());
    }

    private VerticalGroup populateSimInfoAndControls() {
        final VerticalGroup t = new VerticalGroup();

        Label.LabelStyle labelStyle = new Label.LabelStyle();

        labelStyle.background = new SpriteDrawable(Configuration.UI.getButtonUpSprite());
        float currentWidth = labelStyle.background.getMinWidth();
        float currentHeight = labelStyle.background.getMinHeight();
        labelStyle.background.setMinWidth(currentWidth * Configuration.UI.getScale());
        labelStyle.background.setMinHeight(currentHeight * Configuration.UI.getScale());

        labelStyle.fontColor = Color.LIGHT_GRAY;
        labelStyle.font = Configuration.UI.getFont();

        operationsLabel = new Label("Operations: " + controller.getOperationsCounter(), labelStyle);
        operationsLabel.setAlignment(Align.center);

        runningTimeLabel = new Label("Running Time: " + gameplay.getRunningTime(), labelStyle);
        runningTimeLabel.setAlignment(Align.center);

        timeElapsedLabel = new Label("Total Time Elapsed: " + gameplay.getTotalTimeElapsed(), labelStyle);
        timeElapsedLabel.setAlignment(Align.center);

        t.addActor(operationsLabel);
        t.addActor(runningTimeLabel);
        t.addActor(timeElapsedLabel);

        final TextButton pauseButton = new TextButton("Pause", Configuration.UI.getButtonStyle(false, false, Color.YELLOW));
        final TextButton resumeButton = new TextButton("Resume", Configuration.UI.getButtonStyle(false, false, Color.GREEN));

        pauseButton.addListener(new ChangeListener() {
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

        //TODO: Implement restart simulator button

        return t;

    }


}
