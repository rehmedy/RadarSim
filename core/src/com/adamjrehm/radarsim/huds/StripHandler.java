package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.adamjrehm.radarsim.planes.Airplane;
import com.adamjrehm.radarsim.planes.PlaneController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

public class StripHandler {

    private Stage stage;
    private OrthographicCamera camera;
    private Viewport viewport;

    private PlaneController controller;

    private Texture stripTexture, arrivalContainerTexture, departureContainerTexture;

    private Container<VerticalGroup> arrivalTableContainer, departureTableContainer;
    private VerticalGroup arrivalTable, departureTable;


    public StripHandler(PlaneController controller){
        this.controller = controller;

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameInfo.VIRTUAL_WIDTH, GameInfo.VIRTUAL_HEIGHT, camera);
        this.stage = new Stage(viewport);

        stripTexture = new Texture("images/stripbackground.png");
        stripTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        arrivalContainerTexture = new Texture("images/arrivalscontainerbackground.png");
        arrivalContainerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        departureContainerTexture = new Texture("images/departurescontainerbackground.png");
        departureContainerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        this.arrivalTable = new VerticalGroup();
        updateArrivals();

        this.departureTable = new VerticalGroup();
        updateDepartures();

        this.arrivalTableContainer = new Container<>();
        arrivalTableContainer.setSize(arrivalContainerTexture.getWidth(),arrivalContainerTexture.getHeight());
        arrivalTableContainer.setPosition(850, 0);
        arrivalTableContainer.align(Align.bottom);
        arrivalTableContainer.setActor(arrivalTable);
        arrivalTableContainer.setBackground(new SpriteDrawable(new Sprite(arrivalContainerTexture)));

        this.departureTableContainer = new Container<>();
        departureTableContainer.setSize(departureContainerTexture.getWidth(), departureContainerTexture.getHeight());
        departureTableContainer.setPosition(0,0);
        departureTableContainer.align(Align.bottom);
        departureTableContainer.setActor(departureTable);
        departureTableContainer.setBackground(new SpriteDrawable(new Sprite(departureContainerTexture)));

        stage.addActor(arrivalTableContainer);
        stage.addActor(departureTableContainer);

    }

    public void render(){
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    public void dispose(){
        stage.dispose();
        stripTexture.dispose();
    }

    public void update(){
        updateArrivals();
        updateDepartures();
    }

    public void updateArrivals(){
        arrivalTable.clear();
        for (Airplane p : controller.getArrivalList()){
            arrivalTable.addActor(generateStrip(p));
        }
    }

    public void updateDepartures(){
        departureTable.clear();
        for (Airplane p : controller.getDepartureList()){
            departureTable.addActor(generateStrip(p));
        }
    }

    public Stage getStage(){
        return this.stage;
    }

    public Label generateStrip(Airplane p){

        Label.LabelStyle style = new Label.LabelStyle();

        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        style.font = font;
        style.fontColor = Color.BLACK;
        style.background = new SpriteDrawable(new Sprite(stripTexture));

        Label l = new Label(p.getCallsign() + " | " + p.getType().getId() + (p.getFinalDepartureInstruction() != null ? " | " + p.getFinalDepartureInstruction().getName() : ""), style);
        l.setAlignment(Align.center);

        return l;
    }
}
