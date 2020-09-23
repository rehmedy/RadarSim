package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.planes.Airplane;
import com.adamjrehm.radarsim.planes.PlaneController;
import com.adamjrehm.radarsim.scenes.Gameplay;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;

public class StripHandler extends Table {

    private PlaneController controller;

    private Texture stripTexture, arrivalContainerTexture, departureContainerTexture;

    private Container<VerticalGroup> arrivalTableContainer, departureTableContainer;
    private VerticalGroup arrivalTable, departureTable;


    public StripHandler(Gameplay gameplay){
        this.controller = gameplay.getPlaneController();

//        setDebug(true);

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
        arrivalTableContainer.bottom();
        arrivalTableContainer.setActor(arrivalTable);
        arrivalTableContainer.setBackground(new SpriteDrawable(Configuration.UI.getArrivalContainerSprite()));
        float currentWidth = arrivalTableContainer.getBackground().getMinWidth();
        float currentHeight = arrivalTableContainer.getBackground().getMinHeight();
        arrivalTableContainer.getBackground().setMinWidth(currentWidth * Configuration.UI.getScale());
        arrivalTableContainer.getBackground().setMinHeight(currentHeight * Configuration.UI.getScale());

        this.departureTableContainer = new Container<>();
        departureTableContainer.bottom();
        departureTableContainer.setActor(departureTable);
        departureTableContainer.setBackground(new SpriteDrawable(new Sprite(departureContainerTexture)));
        departureTableContainer.getBackground().setMinWidth(currentWidth * Configuration.UI.getScale());
        departureTableContainer.getBackground().setMinHeight(currentHeight * Configuration.UI.getScale());

        defaults().expandX().uniform();

        add(arrivalTableContainer).left();
        add(departureTableContainer).right();

    }

    public void dispose(){
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

    public Label generateStrip(Airplane p){

        Label.LabelStyle style = new Label.LabelStyle();

        style.font = Configuration.UI.getFont();
        style.fontColor = Color.BLACK;

        style.background = new SpriteDrawable(Configuration.UI.getStripSprite());
        float currentWidth = style.background.getMinWidth();
        float currentHeight = style.background.getMinHeight();
        style.background.setMinWidth(currentWidth * Configuration.UI.getScale());
        style.background.setMinHeight(currentHeight * Configuration.UI.getScale());

        Label l = new Label(p.getCallsign() + " | " + p.getType().getId() + (p.getFinalDepartureInstruction() != null ? " | " + p.getFinalDepartureInstruction().getName() : ""), style);
        l.setAlignment(Align.center);

        return l;
    }
}
