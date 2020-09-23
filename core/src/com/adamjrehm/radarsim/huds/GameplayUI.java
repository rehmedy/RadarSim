package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.scenes.Gameplay;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameplayUI {

    private Stage stage;
    private OrthographicCamera camera;
    private Viewport viewport;

    private StripHandler stripHandler;
    private AirplaneCommandHandler airplaneCommandHandler;
    private SimCommandHandler simCommandHandler;

    private Table rootTable;    // Two columns - Radar side & control side
    private Table controlTable; // Two rows - Airplane commands in top row
                                // Sim commands in bottom row

    public GameplayUI(Gameplay gameplay){

        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);
        this.stage = new Stage(viewport);

        this.stripHandler = new StripHandler(gameplay);
        this.airplaneCommandHandler = new AirplaneCommandHandler(gameplay);
        this.simCommandHandler = new SimCommandHandler(gameplay);

        this.rootTable = new Table();

        this.rootTable.setFillParent(true);

        // Column 1 - left half of screen
        this.rootTable.add(stripHandler).expandX().fillX().uniform().bottom();

        // Column 2, right half of screen
        this.controlTable = new Table();

        this.controlTable.add(airplaneCommandHandler).expand().fill();
        this.controlTable.row();
        this.controlTable.add(simCommandHandler).bottom().right();

        this.rootTable.add(controlTable).expand().fill().uniform();

        // Debug
//        this.controlTable.setDebug(true);
//        this.rootTable.setDebug(true);

        // UI Stage
        stage.addActor(rootTable);
    }

    public void render(){
        simCommandHandler.update();
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height){
        this.stage.getViewport().update(width, height, true);
    }

    public Stage getStage(){
        return this.stage;
    }

    public StripHandler getStripHandler() {
        return stripHandler;
    }

    public AirplaneCommandHandler getAirplaneCommandHandler() {
        return airplaneCommandHandler;
    }

    public SimCommandHandler getSimCommandHandler() {
        return simCommandHandler;
    }

}
