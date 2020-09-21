package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.geography.DeparturePoint;
import com.adamjrehm.radarsim.geography.Pattern;
import com.adamjrehm.radarsim.geography.Runway;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.adamjrehm.radarsim.planes.Airplane;
import com.adamjrehm.radarsim.planes.FPLType;
import com.adamjrehm.radarsim.planes.PlaneController;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AirplaneCommandHandler {

    private RadarSim radarSim;
    private PlaneController controller;

    private Stage stage;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture buttonUpTexture = new Texture("images/buttonup.png"),
                    buttonDownTexture = new Texture("images/buttondown.png"),
                    buttonCheckedTexture = new Texture("images/buttonchecked.png"),
                    buttonUpSlimTexture = new Texture("images/buttonupslim.png"),
                    buttonDownSlimTexture = new Texture("images/buttondownslim.png");

    private BitmapFont font = new BitmapFont();

    private Container<VerticalGroup> airplaneTableContainer, column2Container, column3Container, newPlaneTableContainer;

    private VerticalGroup airplaneTable, commandTable, vectorTable, runwayTable, departurePathTable, newPlaneTable, taxiwayTable;

    private Container<VerticalGroup> planeInfoTableContainer;
    private VerticalGroup planeInfoTable;
    private Label nextVectorLabel, lastVectorLabel, speedLabel, isAirborneLabel, inboundOrOutboundLabel, touchAndGoCountLabel,
                    locationLabel, directionFromAirportLabel;

    private ButtonGroup<TextButton> airplaneButtonGroup, setValueButtonGroup;

    private Airplane selected;

    private boolean changedSelection;


    public AirplaneCommandHandler(RadarSim sim, PlaneController controller){

        // Initialization
        this.radarSim = sim;
        this.controller = controller;

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameInfo.VIRTUAL_WIDTH, GameInfo.VIRTUAL_HEIGHT, camera);
        this.viewport.getCamera().position.set(GameInfo.VIRTUAL_WIDTH / 2f, GameInfo.VIRTUAL_HEIGHT / 2f, 0);

        stage = new Stage(viewport);

        //Gdx.input.setInputProcessor(stage);

        // Button Groups
        this.airplaneButtonGroup = new ButtonGroup<>();
        airplaneButtonGroup.setMinCheckCount(0);
        airplaneButtonGroup.setMaxCheckCount(1);
        airplaneButtonGroup.setUncheckLast(true);

        this.setValueButtonGroup = new ButtonGroup<>();
        setValueButtonGroup.setMinCheckCount(0);
        setValueButtonGroup.setMaxCheckCount(1);
        setValueButtonGroup.setUncheckLast(true);

        // Tables
        this.airplaneTable = new VerticalGroup();
        this.newPlaneTable = populateNewPlaneButtons();
        this.commandTable = populateCommands();
        this.vectorTable = populateVectors();
        this.runwayTable = populateRunways();
        this.departurePathTable = populateDeparturePaths();
        this.planeInfoTable = populatePlaneInfo();
        this.taxiwayTable = populateTaxiways();


        // Table Containers
        float containerWidth = GameInfo.VIRTUAL_WIDTH / 11f;
        float containerHeight = GameInfo.VIRTUAL_HEIGHT;
        this.airplaneTableContainer = new Container<>();
        airplaneTableContainer.setSize(containerWidth, containerHeight / 2);
        airplaneTableContainer.setPosition(1250,450);
        airplaneTableContainer.setActor(airplaneTable);
        airplaneTableContainer.align(Align.topLeft);

        this.column2Container = new Container<>();
        column2Container.setSize(containerWidth, containerHeight);
        column2Container.setPosition(1250 + airplaneTableContainer.getWidth(), 0);
        column2Container.setActor(commandTable);
        column2Container.align(Align.topLeft);
        column2Container.setVisible(false);

        this.column3Container = new Container<>();
        column3Container.setSize(containerWidth, containerHeight);
        column3Container.setPosition(1250 + airplaneTableContainer.getWidth() + column2Container.getWidth(), 0);
        column3Container.align(Align.topLeft);
        column3Container.setVisible(false);

        this.newPlaneTableContainer = new Container<>();
        newPlaneTableContainer.setSize(containerWidth, containerHeight);
        newPlaneTableContainer.setPosition(2250,0);
        newPlaneTableContainer.setActor(newPlaneTable);
        newPlaneTableContainer.align(Align.topRight);

        planeInfoTableContainer = new Container<>();
        planeInfoTableContainer.setSize(containerWidth, 200);
        planeInfoTableContainer.setPosition(1235, 1040);
        planeInfoTableContainer.setActor(planeInfoTable);
        planeInfoTableContainer.align(Align.top);
        planeInfoTableContainer.setVisible(false);

        // Add everything to stage
        stage.addActor(airplaneTableContainer);
        stage.addActor(column2Container);
        stage.addActor(column3Container);
        stage.addActor(newPlaneTableContainer);
        stage.addActor(planeInfoTableContainer);
    }

    public void render(){
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    public void update(Array<Airplane> planes){
        // Get selected aircraft if new click is detected
//      if (changedSelection) {

        // If an aircraft is checked, find it & show commands
        if (airplaneButtonGroup.getAllChecked().size == 1) {
            for (Airplane p : planes) {
                if (airplaneButtonGroup.getChecked() == p.getTextButton()) {
                    this.selected = p;
                    changedSelection = false;
                }
            }
            column2Container.setVisible(true);

            planeInfoTableContainer.setVisible(true);

            speedLabel.setText("Speed: " + selected.getSpeed());
            nextVectorLabel.setText("Next: " + (selected.getNext() != null ? selected.getNext().getName() : "null"));
            lastVectorLabel.setText("Last: " + selected.getLast().getName());
            isAirborneLabel.setText("Airborne: " + selected.isAirborne());
            inboundOrOutboundLabel.setText("Type: " + selected.inboundOrOutbound());
            touchAndGoCountLabel.setText("T/G: " + selected.getTouchAndGoCount());
            locationLabel.setText("Location: " + selected.getDistanceFromAirport() + " " + selected.getDirectionFromAirport());
        }

        // If all aircraft are unchecked, hide the commandTable, clear selection
        else if (airplaneButtonGroup.getAllChecked().size == 0){
            column2Container.setVisible(false);
            column3Container.setVisible(false);
            planeInfoTableContainer.setVisible(false);
            clearSelection();
        }
        render();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    public void dispose(){
        stage.dispose();
        buttonCheckedTexture.dispose();
        buttonDownSlimTexture.dispose();
        buttonDownTexture.dispose();
        buttonUpSlimTexture.dispose();
        buttonUpTexture.dispose();
        font.dispose();
    }


    private VerticalGroup populateNewPlaneButtons() {
        VerticalGroup t = new VerticalGroup();

        TextButton generateRandomVFRInboundButton = new TextButton("New VFR Inbound", getDefaultStyle(false, Color.WHITE));
        generateRandomVFRInboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random VFR Inbound Generated");
                controller.generateRandomVFRInbound();
            }
        });
        t.addActor(generateRandomVFRInboundButton);

        TextButton generateRandomIFRInboundButton = new TextButton("New IFR Inbound", getDefaultStyle(false, Color.WHITE));
        generateRandomIFRInboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random IFR Inbound Generated");
                controller.generateRandomIFRInbound();
            }
        });
        t.addActor(generateRandomIFRInboundButton);

        TextButton generateRandomGAOutboundButton = new TextButton("New GA Departure", getDefaultStyle(false, Color.WHITE));
        generateRandomGAOutboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random GA Departure Generated");
                controller.generateRandomGAOutbound(false);
            }
        });
        t.addActor(generateRandomGAOutboundButton);

        TextButton generateRandomIFROutboundButton = new TextButton("New IFR Departure", getDefaultStyle(false, Color.WHITE));
        generateRandomIFROutboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random IFR Departure Generated");
                controller.generateRandomCommercialOutbound();
            }
        });
        t.addActor(generateRandomIFROutboundButton);

        TextButton generateRandomGAOutboundIntersectionButton = new TextButton("New GA Intersection Departure", getDefaultStyle(false, Color.WHITE));
        generateRandomGAOutboundIntersectionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random GA Intersection Departure Generated");
                controller.generateRandomGAOutbound(true);
            }
        });
        t.addActor(generateRandomGAOutboundIntersectionButton);

        TextButton generateRandomVFROutboundR16Button = new TextButton("New R16 VFR Departure", getDefaultStyle(false, Color.WHITE));
        generateRandomVFROutboundR16Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random VFR Runway 16 Departure Generated");
                controller.generateRandom16Outbound(false);
            }
        });
        t.addActor(generateRandomVFROutboundR16Button);

        TextButton generateRandomIFROutboundR16Button = new TextButton("New R16 IFR Departure", getDefaultStyle(false, Color.WHITE));
        generateRandomIFROutboundR16Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random IFR Runway 16 Departure Generated");
                controller.generateRandom16Outbound(true);
            }
        });
        t.addActor(generateRandomIFROutboundR16Button);

        TextButton removeLastPlaneButton = new TextButton("Remove Last Plane", getDefaultStyle(false, Color.FIREBRICK));
        removeLastPlaneButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("Removing last plane...");
                if (controller.removeLastPlane())
                    System.out.println("Removed last plane successfully.");
                else
                    System.out.println("No planes found!");
            }
        });
        t.addActor(removeLastPlaneButton);

        return t;
    }

    private VerticalGroup populateCommands() {

        // Create table
        final VerticalGroup t = new VerticalGroup();

        TextButton addTouchAndGoButton = new TextButton("Add Touch & Go", getDefaultStyle(false, Color.WHITE));
        addTouchAndGoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (selected.getLandings() < 0) {
                    selected.setLandings(selected.getLandings() - 1);
                    selected.clearDeparturePath();
                    selected.generateDeparturePath();
                    System.out.println(selected.toString() + ": Touch & Go added");
                }
                else if (selected.getLandings() > 0) {
                    selected.setLandings(selected.getLandings() + 1);
                    System.out.println(selected.toString() + ": Touch & Go added");
                }
                else
                    System.out.println(selected.toString() + ": Unable to add touch & go");
            }
        });
        t.addActor(addTouchAndGoButton);

        TextButton cancelLandingClearanceButton = new TextButton("Cancel Landing Clearance", getDefaultStyle(false, Color.WHITE));
        cancelLandingClearanceButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.cancelLandingClearance();
            }
        });
        t.addActor(cancelLandingClearanceButton);

        TextButton clearForTakeoffButton = new TextButton("Clear for Takeoff", getDefaultStyle(false, Color.WHITE));
        clearForTakeoffButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.clearForTakeoff();
                System.out.println(selected.toString() + ": Cleared for takeoff...");
            }
        });
        t.addActor(clearForTakeoffButton);

        TextButton clearToLandButton = new TextButton("Clear to Land", getDefaultStyle(false, Color.WHITE));
        clearToLandButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.clearToLand();
            }
        });
        t.addActor(clearToLandButton);

        // Create buttons & add listeners
        TextButton crossButton = new TextButton("Cross", getDefaultStyle(false, Color.WHITE));
        crossButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println(selected.toString() + ": Crossing runway...");
                selected.cross();
            }
        });
        t.addActor(crossButton);

        TextButton exitRunwayButton = new TextButton("Exit Runway", getDefaultStyle(true, Color.WHITE));
        exitRunwayButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton)actor;
                if (button.isChecked()) {
                    column3Container.setActor(taxiwayTable);
                    column3Container.setVisible(true);
                } else {
                    column3Container.removeActor(taxiwayTable);
                    column3Container.setVisible(false);
                }
            }
        });
        setValueButtonGroup.add(exitRunwayButton);
        t.addActor(exitRunwayButton);

        TextButton extendCrosswindButton = new TextButton("Extend Crosswind", getDefaultStyle(false, Color.WHITE));
        extendCrosswindButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendCrosswind(10);
            }
        });
        t.addActor(extendCrosswindButton);

        TextButton extendCrosswindOneMileButton = new TextButton("Extend Crosswind 1 Mile", getDefaultStyle(false, Color.WHITE));
        extendCrosswindOneMileButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendCrosswind(1);
            }
        });
        t.addActor(extendCrosswindOneMileButton);

        TextButton extendDownwindButton = new TextButton("Extend Downwind", getDefaultStyle(false, Color.WHITE));
        extendDownwindButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendDownwind(10);
            }
        });
        t.addActor(extendDownwindButton);

        TextButton extendDownwindOneMileButton = new TextButton("Extend Downwind 1 Mile", getDefaultStyle(false, Color.WHITE));
        extendDownwindOneMileButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendDownwind(1);
            }
        });
        t.addActor(extendDownwindOneMileButton);

        TextButton extendUpwindButton = new TextButton("Extend Upwind", getDefaultStyle(false, Color.WHITE));
        extendUpwindButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendUpwind(10);
            }
        });
        t.addActor(extendUpwindButton);

        TextButton extendUpwindOneMileButton = new TextButton("Extend Upwind 1 Mile", getDefaultStyle(false, Color.WHITE));
        extendUpwindOneMileButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendUpwind(1);
            }
        });
        t.addActor(extendUpwindOneMileButton);

        TextButton goAroundButton = new TextButton("Go Around", getDefaultStyle(false, Color.WHITE));
        goAroundButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (selected.getFPLType() == FPLType.IFR)
                    selected.goAroundIFR();
                else
                    selected.goAroundVFR();
            }
        });
        t.addActor(goAroundButton);

        TextButton lineUpButton = new TextButton("Line Up & Wait", getDefaultStyle(false, Color.WHITE));
        lineUpButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.lineUp();
                System.out.println(selected.toString() + ": Lining up and waiting...");
            }
        });
        t.addActor(lineUpButton);


        TextButton makeLeft360Button = new TextButton("Make Left 360", getDefaultStyle(false, Color.WHITE));
        makeLeft360Button.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.makeLeft360();
            }
        });
        t.addActor(makeLeft360Button);

        TextButton makeRight360Button = new TextButton("Make Right 360", getDefaultStyle(false, Color.WHITE));
        makeRight360Button.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.makeRight360();
            }
        });
        t.addActor(makeRight360Button);

        TextButton setDeparturePathButton = new TextButton("Set Departure Path", getDefaultStyle(true, Color.WHITE));
        setDeparturePathButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton)actor;
                if (button.isChecked()) {
                    column3Container.setActor(departurePathTable);
                    column3Container.setVisible(true);
                }
                else{
                    column3Container.removeActor(departurePathTable);
                    column3Container.setVisible(false);
                }
            }
        });
        setValueButtonGroup.add(setDeparturePathButton);
        t.addActor(setDeparturePathButton);

        TextButton setNextVectorButton = new TextButton("Set Next Vector", getDefaultStyle(true, Color.WHITE));
        setNextVectorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton)actor;
                if (button.isChecked()) {
                    column3Container.setActor(vectorTable);
                    column3Container.setVisible(true);
                }
                else{
                    column3Container.removeActor(vectorTable);
                    column3Container.setVisible(false);
                }
            }
        });
        setValueButtonGroup.add(setNextVectorButton);
        t.addActor(setNextVectorButton);

        TextButton setRunwayButton = new TextButton("Set Runway", getDefaultStyle(true, Color.WHITE));
        setRunwayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton)actor;
                if (button.isChecked()) {
                    column3Container.setActor(runwayTable);
                    column3Container.setVisible(true);
                } else {
                    column3Container.removeActor(runwayTable);
                    column3Container.setVisible(false);
                }
            }
        });
        setValueButtonGroup.add(setRunwayButton);
        t.addActor(setRunwayButton);

        TextButton turnBaseButton = new TextButton("Turn Base", getDefaultStyle(false, Color.WHITE));
        turnBaseButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.turnBase();
            }
        });
        t.addActor(turnBaseButton);

        final TextButton pausePlaneButton = new TextButton("Pause Plane", getDefaultStyle(false, Color.YELLOW));
        final TextButton resumePlaneButton = new TextButton("Resume Plane", getDefaultStyle(false, Color.GREEN));
        final TextButton removePlaneButton = new TextButton("Remove Plane", getDefaultStyle(false, Color.FIREBRICK));

        pausePlaneButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selected.pause();
                t.removeActor(pausePlaneButton);
                t.addActorBefore(removePlaneButton, resumePlaneButton);

                System.out.println(selected.getCallsign() + ": Paused.");
            }
        });
        resumePlaneButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selected.resume();
                t.removeActor(resumePlaneButton);
                t.addActorBefore(removePlaneButton, pausePlaneButton);

                System.out.println(selected.getCallsign() + ": Resumed.");
            }
        });
        removePlaneButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                controller.removePlane(selected);
            }
        });

        t.addActor(pausePlaneButton);
        t.addActor(removePlaneButton);
/*
        TextButton = new TextButton("Extend Upwind", getStyle(false));
        .addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {

            }
        });
        t.addActor();

        TextButton = new TextButton("", getDefaultStyle(false));
        .addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {

            }
        });
        t.addActor();

        TextButton = new TextButton("", getStyle(false));
        .addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {

            }
        });
        t.addActor();

 */

        return t;
    }

    private VerticalGroup populateVectors(){
        VerticalGroup t = new VerticalGroup();

        for (final Pattern p: Pattern.values()){
            TextButton vectorButton = new TextButton(p.getName(), getSlimStyle(false));
            vectorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    selected.setNextVector(p);
                }
            });
            t.addActor(vectorButton);
        }

        return t;
    }

    private VerticalGroup populateRunways(){
        VerticalGroup t = new VerticalGroup();

        TextButton R28LButton = new TextButton("Runway 28L", getDefaultStyle(false, Color.WHITE));
        R28LButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.setRwy(Runway.R28L);
            }
        });
        t.addActor(R28LButton);

        TextButton R28RButton = new TextButton("Runway 28R", getDefaultStyle(false, Color.WHITE));
        R28RButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.setRwy(Runway.R28R);
            }
        });
        t.addActor(R28RButton);

        return t;
    }

    private VerticalGroup populateDeparturePaths(){
        VerticalGroup t = new VerticalGroup();

        for (final DeparturePoint p : DeparturePoint.values()){
            TextButton departurePointButton = new TextButton(p.getName(), getDefaultStyle(false, Color.WHITE));
            departurePointButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    selected.setDeparturePath(p);
                }
            });
            t.addActor(departurePointButton);
        }

        return t;
    }

    private VerticalGroup populateTaxiways(){
        VerticalGroup t = new VerticalGroup();

        TextButton taxiwayDeltaButton = new TextButton("Taxiway D", getDefaultStyle(false, Color.WHITE));
        taxiwayDeltaButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('D');
            }
        });
        t.addActor(taxiwayDeltaButton);

        TextButton taxiwayEchoButton = new TextButton("Taxiway E", getDefaultStyle(false, Color.WHITE));
        taxiwayEchoButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('E');
            }
        });
        t.addActor(taxiwayEchoButton);

        TextButton taxiwayFoxtrotButton = new TextButton("Taxiway F", getDefaultStyle(false, Color.WHITE));
        taxiwayFoxtrotButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('F');
            }
        });
        t.addActor(taxiwayFoxtrotButton);

        TextButton taxiwayGolfButton = new TextButton("Taxiway G", getDefaultStyle(false, Color.WHITE));
        taxiwayGolfButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('G');
            }
        });
        t.addActor(taxiwayGolfButton);

        return t;
    }

    private VerticalGroup populatePlaneInfo(){
        VerticalGroup t = new VerticalGroup();

        Label.LabelStyle style = new Label.LabelStyle();

        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        style.font = font;
        style.fontColor = Color.LIGHT_GRAY;
        style.background = new SpriteDrawable(new Sprite(buttonUpSlimTexture));

        locationLabel = new Label("Location: ", style);
        isAirborneLabel = new Label("Airborne: ", style);
        nextVectorLabel = new Label("Next: ", style);
        lastVectorLabel = new Label("Last: ", style);
        inboundOrOutboundLabel = new Label("Type: ", style);
        touchAndGoCountLabel = new Label("T/G: ", style);
        speedLabel = new Label("Speed: ", style);


        t.addActor(locationLabel);
        t.addActor(isAirborneLabel);
        t.addActor(nextVectorLabel);
        t.addActor(lastVectorLabel);
        t.addActor(inboundOrOutboundLabel);
        t.addActor(touchAndGoCountLabel);
        t.addActor(speedLabel);

        return t;
    }

    public void addAirplane(Airplane p){
        TextButton button = new TextButton(p.toString(), getDefaultStyle(true, Color.WHITE));
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                changedSelection = true;
            }
        });
        p.setTextButton(button);
        airplaneButtonGroup.add(button);

        airplaneTable.addActor(button);
    }

    public void removeAirplane(Airplane p){
        airplaneButtonGroup.remove(p.getTextButton());
        airplaneTable.removeActor(p.getTextButton());
    }

    public void select(Airplane p){
        this.selected = p;
        this.airplaneButtonGroup.setChecked(p.toString());
    }

    public void deselectAll(){
        this.selected = null;
        this.airplaneButtonGroup.uncheckAll();
    }

    public void clearSelection(){
        this.selected = null;
    }

    public Stage getStage(){
        return this.stage;
    }

    public void setPlaneController(PlaneController c){
        this.controller = c;
    }

    public TextButton.TextButtonStyle getDefaultStyle(boolean toggleable, Color color){
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = color;
        style.up = new SpriteDrawable(new Sprite(buttonUpTexture));
        style.down = new SpriteDrawable(new Sprite(buttonDownTexture));

        if (toggleable)
            style.checked = new SpriteDrawable(new Sprite(buttonCheckedTexture));

        return style;
    }

    private TextButton.TextButtonStyle getSlimStyle(boolean toggleable){
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.up = new SpriteDrawable(new Sprite(buttonUpSlimTexture));
        style.down = new SpriteDrawable(new Sprite(buttonDownSlimTexture));

        return style;
    }
}
