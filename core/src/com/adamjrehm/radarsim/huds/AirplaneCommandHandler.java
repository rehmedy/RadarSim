package com.adamjrehm.radarsim.huds;

import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.geography.DeparturePoint;
import com.adamjrehm.radarsim.geography.Pattern;
import com.adamjrehm.radarsim.geography.Runway;
import com.adamjrehm.radarsim.planes.Airplane;
import com.adamjrehm.radarsim.planes.FPLType;
import com.adamjrehm.radarsim.planes.PlaneController;
import com.adamjrehm.radarsim.scenes.Gameplay;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

public class AirplaneCommandHandler extends Table {

    private PlaneController controller;

    private VerticalGroup column1;

    private Container<VerticalGroup> airplaneTableContainer, column2Container, column3Container, newPlaneTableContainer;

    private VerticalGroup airplaneTable, commandTable, vectorTable, runwayTable, departurePathTable, newPlaneTable, taxiwayTable;

    private Container<VerticalGroup> planeInfoTableContainer;
    private VerticalGroup planeInfoTable;
    private Label nextVectorLabel, lastVectorLabel, speedLabel, isAirborneLabel, inboundOrOutboundLabel, touchAndGoCountLabel,
            locationLabel;

    private ButtonGroup<TextButton> airplaneButtonGroup, setValueButtonGroup;

    private final TextButton pausePlaneButton = new TextButton("Pause Plane", Configuration.UI.getButtonStyle(false, false, Color.YELLOW));
    private final TextButton resumePlaneButton = new TextButton("Resume Plane", Configuration.UI.getButtonStyle(false, false, Color.GREEN));
    private final TextButton removePlaneButton = new TextButton("Remove Plane", Configuration.UI.getButtonStyle(false, false, Color.FIREBRICK));
    private TextButton addTouchAndGoButton, cancelLandingClearanceButton, clearForTakeoffButton, clearToLandButton,
            crossButton, exitRunwayButton, extendCrosswindButton, extendCrosswindOneMileButton, extendDownwindButton,
            extendDownwindOneMileButton, extendUpwindButton, extendUpwindOneMileButton, goAroundButton, lineUpButton,
            makeLeft360Button, makeRight360Button, setDeparturePathButton, setNextVectorButton, setRunwayButton,
            turnBaseButton;

    private Airplane selected;

    public AirplaneCommandHandler(Gameplay gameplay) {

        // Initialization
        this.controller = gameplay.getPlaneController();

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
        this.column1 = new VerticalGroup();

        this.airplaneTableContainer = new Container<>();
        airplaneTableContainer.setActor(airplaneTable);

        this.column2Container = new Container<>();
        column2Container.setActor(commandTable);
        column2Container.setVisible(false);

        this.column3Container = new Container<>();
        column3Container.setVisible(false);

        this.newPlaneTableContainer = new Container<>();
        newPlaneTableContainer.setActor(newPlaneTable);

        planeInfoTableContainer = new Container<>();
        planeInfoTableContainer.setActor(planeInfoTable);
        planeInfoTableContainer.setVisible(false);

        column1.addActor(planeInfoTableContainer);
        column1.addActor(airplaneTableContainer);

        defaults().top().fillX();

        add(column1).expand().uniform();
        add(column2Container).expand().uniform();
        add(column3Container).expand().uniform();
        add(newPlaneTableContainer).expand().uniform();

        //setDebug(true);
    }

    public void update(Array<Airplane> planes) {
        // If an aircraft is checked, find it & show commands
        if (airplaneButtonGroup.getAllChecked().size == 1) {
            updatePlaneInfoLabels();
            showButtons();
        }

        // If all aircraft are unchecked, hide the commandTable, clear selection
        else if (airplaneButtonGroup.getAllChecked().size == 0) {
            column2Container.setVisible(false);
            column3Container.setVisible(false);
            planeInfoTableContainer.setVisible(false);
            commandTable.clear();
            clearSelection();
        }
    }

    private void updatePlaneInfoLabels(){
        speedLabel.setText("Speed: " + selected.getSpeed());
        nextVectorLabel.setText("Next: " + (selected.getNext() != null ? selected.getNext().getName() : "null"));
        lastVectorLabel.setText("Last: " + selected.getLast().getName());
        isAirborneLabel.setText("Airborne: " + selected.isAirborne());
        inboundOrOutboundLabel.setText("Type: " + selected.inboundOrOutbound());
        touchAndGoCountLabel.setText("T/G: " + selected.getTouchAndGoCount());
        locationLabel.setText("Location: " + selected.getDistanceFromAirport() + " " + selected.getDirectionFromAirport());
    }

    private void showButtons(){
        commandTable.clear();

        if (selected.getFPLType() == FPLType.IFR){
            if (selected.getLandings() < 0 && !selected.isAirborne()) {
                commandTable.addActor(clearForTakeoffButton);
                commandTable.addActor(crossButton);
                commandTable.addActor(lineUpButton);
                commandTable.addActor(setDeparturePathButton);
            } else if (selected.getLandings() == 0) {
                commandTable.addActor(exitRunwayButton);
            } else if (selected.getLandings() > 0) {
                commandTable.addActor(cancelLandingClearanceButton);;
                commandTable.addActor(clearToLandButton);
                commandTable.addActor(goAroundButton);
            }
        } else if (selected.getFPLType() == FPLType.VFR){
            if (selected.getLandings() < 0 && !selected.isAirborne()) {
                commandTable.addActor(addTouchAndGoButton);
                commandTable.addActor(clearForTakeoffButton);
                commandTable.addActor(crossButton);
                commandTable.addActor(lineUpButton);
            } else if (selected.getLandings() == 0) {
                commandTable.addActor(crossButton);
                commandTable.addActor(exitRunwayButton);
            } else if (selected.getLandings() > 0) {
                commandTable.addActor(addTouchAndGoButton);
                commandTable.addActor(cancelLandingClearanceButton);
                commandTable.addActor(clearToLandButton);
                commandTable.addActor(extendCrosswindButton);
                commandTable.addActor(extendCrosswindOneMileButton);
                commandTable.addActor(extendDownwindButton);
                commandTable.addActor(extendDownwindOneMileButton);
                commandTable.addActor(extendUpwindButton);
                commandTable.addActor(extendUpwindOneMileButton);
                commandTable.addActor(goAroundButton);
                commandTable.addActor(makeLeft360Button);
                commandTable.addActor(makeRight360Button);
                commandTable.addActor(setNextVectorButton);
                commandTable.addActor(setRunwayButton);
                commandTable.addActor(turnBaseButton);
            }
        }

        if (selected.isPaused()){
            commandTable.addActor(resumePlaneButton);
        } else if (!selected.isPaused()) {
            commandTable.addActor(pausePlaneButton);
        }

        commandTable.addActor(removePlaneButton);
    }

    private VerticalGroup populateNewPlaneButtons() {
        VerticalGroup t = new VerticalGroup();

        TextButton generateRandomVFRInboundButton = new TextButton("New VFR Inbound", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        generateRandomVFRInboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random VFR Inbound Generated");
                controller.generateRandomVFRInbound();
            }
        });
        t.addActor(generateRandomVFRInboundButton);

        TextButton generateRandomIFRInboundButton = new TextButton("New IFR Inbound", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        generateRandomIFRInboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random IFR Inbound Generated");
                controller.generateRandomIFRInbound();
            }
        });
        t.addActor(generateRandomIFRInboundButton);

        TextButton generateRandomGAOutboundButton = new TextButton("New GA Departure", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        generateRandomGAOutboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random GA Departure Generated");
                controller.generateRandomGAOutbound(false);
            }
        });
        t.addActor(generateRandomGAOutboundButton);

        TextButton generateRandomIFROutboundButton = new TextButton("New IFR Departure", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        generateRandomIFROutboundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random IFR Departure Generated");
                controller.generateRandomCommercialOutbound();
            }
        });
        t.addActor(generateRandomIFROutboundButton);

        TextButton generateRandomGAOutboundIntersectionButton = new TextButton("New GA Intersection Departure", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        generateRandomGAOutboundIntersectionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random GA Intersection Departure Generated");
                controller.generateRandomGAOutbound(true);
            }
        });
        t.addActor(generateRandomGAOutboundIntersectionButton);

        TextButton generateRandomVFROutboundR16Button = new TextButton("New R16 VFR Departure", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        generateRandomVFROutboundR16Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random VFR Runway 16 Departure Generated");
                controller.generateRandom16Outbound(false);
            }
        });
        t.addActor(generateRandomVFROutboundR16Button);

        TextButton generateRandomIFROutboundR16Button = new TextButton("New R16 IFR Departure", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        generateRandomIFROutboundR16Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println("New Random IFR Runway 16 Departure Generated");
                controller.generateRandom16Outbound(true);
            }
        });
        t.addActor(generateRandomIFROutboundR16Button);

        TextButton removeLastPlaneButton = new TextButton("Remove Last Plane", Configuration.UI.getButtonStyle(false, false, Color.FIREBRICK));
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

        addTouchAndGoButton = new TextButton("Add Touch & Go", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        addTouchAndGoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (selected.getLandings() < 0) {
                    selected.setLandings(selected.getLandings() - 1);
                    selected.clearDeparturePath();
                    selected.generateDeparturePath();
                    System.out.println(selected.toString() + ": Touch & Go added");
                } else if (selected.getLandings() > 0) {
                    selected.setLandings(selected.getLandings() + 1);
                    System.out.println(selected.toString() + ": Touch & Go added");
                } else
                    System.out.println(selected.toString() + ": Unable to add touch & go");
            }
        });
//        t.addActor(addTouchAndGoButton);

        cancelLandingClearanceButton = new TextButton("Cancel Landing Clearance", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        cancelLandingClearanceButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.cancelLandingClearance();
            }
        });
//        t.addActor(cancelLandingClearanceButton);

        clearForTakeoffButton = new TextButton("Clear for Takeoff", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        clearForTakeoffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.clearForTakeoff();
                System.out.println(selected.toString() + ": Cleared for takeoff...");
            }
        });
//        t.addActor(clearForTakeoffButton);

        clearToLandButton = new TextButton("Clear to Land", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        clearToLandButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.clearToLand();
            }
        });
//        t.addActor(clearToLandButton);

        // Create buttons & add listeners
        crossButton = new TextButton("Cross", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        crossButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                System.out.println(selected.toString() + ": Crossing runway...");
                selected.cross();
            }
        });
//        t.addActor(crossButton);

        exitRunwayButton = new TextButton("Exit Runway", Configuration.UI.getButtonStyle(true, false, Color.WHITE));
        exitRunwayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton) actor;
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
//        t.addActor(exitRunwayButton);

        extendCrosswindButton = new TextButton("Extend Crosswind", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        extendCrosswindButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendCrosswind(10);
            }
        });
//        t.addActor(extendCrosswindButton);

        extendCrosswindOneMileButton = new TextButton("Extend Crosswind 1 Mile", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        extendCrosswindOneMileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendCrosswind(1);
            }
        });
//        t.addActor(extendCrosswindOneMileButton);

        extendDownwindButton = new TextButton("Extend Downwind", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        extendDownwindButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendDownwind(10);
            }
        });
//        t.addActor(extendDownwindButton);

        extendDownwindOneMileButton = new TextButton("Extend Downwind 1 Mile", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        extendDownwindOneMileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendDownwind(1);
            }
        });
//        t.addActor(extendDownwindOneMileButton);

        extendUpwindButton = new TextButton("Extend Upwind", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        extendUpwindButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendUpwind(10);
            }
        });
//        t.addActor(extendUpwindButton);

        extendUpwindOneMileButton = new TextButton("Extend Upwind 1 Mile", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        extendUpwindOneMileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.extendUpwind(1);
            }
        });
//        t.addActor(extendUpwindOneMileButton);

        goAroundButton = new TextButton("Go Around", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        goAroundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (selected.getFPLType() == FPLType.IFR)
                    selected.goAroundIFR();
                else
                    selected.goAroundVFR();
            }
        });
//        t.addActor(goAroundButton);

        lineUpButton = new TextButton("Line Up & Wait", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        lineUpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.lineUp();
                System.out.println(selected.toString() + ": Lining up and waiting...");
            }
        });
//        t.addActor(lineUpButton);


        makeLeft360Button = new TextButton("Make Left 360", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        makeLeft360Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.makeLeft360();
            }
        });
//        t.addActor(makeLeft360Button);

        makeRight360Button = new TextButton("Make Right 360", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        makeRight360Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.makeRight360();
            }
        });
//        t.addActor(makeRight360Button);

        setDeparturePathButton = new TextButton("Set Departure Path", Configuration.UI.getButtonStyle(true, false, Color.WHITE));
        setDeparturePathButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton) actor;
                if (button.isChecked()) {
                    column3Container.setActor(departurePathTable);
                    column3Container.setVisible(true);
                } else {
                    column3Container.removeActor(departurePathTable);
                    column3Container.setVisible(false);
                }
            }
        });
        setValueButtonGroup.add(setDeparturePathButton);
//        t.addActor(setDeparturePathButton);

        setNextVectorButton = new TextButton("Set Next Vector", Configuration.UI.getButtonStyle(true, false, Color.WHITE));
        setNextVectorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton) actor;
                if (button.isChecked()) {
                    column3Container.setActor(vectorTable);
                    column3Container.setVisible(true);
                } else {
                    column3Container.removeActor(vectorTable);
                    column3Container.setVisible(false);
                }
            }
        });
        setValueButtonGroup.add(setNextVectorButton);
//        t.addActor(setNextVectorButton);

        setRunwayButton = new TextButton("Set Runway", Configuration.UI.getButtonStyle(true, false, Color.WHITE));
        setRunwayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                TextButton button = (TextButton) actor;
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
//        t.addActor(setRunwayButton);

        turnBaseButton = new TextButton("Turn Base", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        turnBaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.turnBase();
            }
        });
//        t.addActor(turnBaseButton);

//        final TextButton pausePlaneButton = new TextButton("Pause Plane", Configuration.UI.getButtonStyle(false, false, Color.YELLOW));
//        final TextButton resumePlaneButton = new TextButton("Resume Plane", Configuration.UI.getButtonStyle(false, false, Color.GREEN));
//        final TextButton removePlaneButton = new TextButton("Remove Plane", Configuration.UI.getButtonStyle(false, false, Color.FIREBRICK));

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
        removePlaneButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                controller.removePlane(selected);
            }
        });

//        t.addActor(pausePlaneButton);
//        t.addActor(removePlaneButton);

        return t;
    }

    private VerticalGroup populateVectors() {
        VerticalGroup t = new VerticalGroup();

        for (final Pattern p : Pattern.values()) {
            TextButton vectorButton = new TextButton(p.getName(), Configuration.UI.getButtonStyle(false, true, Color.WHITE));
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

    private VerticalGroup populateRunways() {
        VerticalGroup t = new VerticalGroup();

        TextButton R28LButton = new TextButton("Runway 28L", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        R28LButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.setRwy(Runway.R28L);
            }
        });
        t.addActor(R28LButton);

        TextButton R28RButton = new TextButton("Runway 28R", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        R28RButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.setRwy(Runway.R28R);
            }
        });
        t.addActor(R28RButton);

        return t;
    }

    private VerticalGroup populateDeparturePaths() {
        VerticalGroup t = new VerticalGroup();

        for (final DeparturePoint p : DeparturePoint.values()) {
            TextButton departurePointButton = new TextButton(p.getName(), Configuration.UI.getButtonStyle(false, false, Color.WHITE));
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

    private VerticalGroup populateTaxiways() {
        VerticalGroup t = new VerticalGroup();

        TextButton taxiwayDeltaButton = new TextButton("Taxiway D", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        taxiwayDeltaButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('D');
            }
        });
        t.addActor(taxiwayDeltaButton);

        TextButton taxiwayEchoButton = new TextButton("Taxiway E", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        taxiwayEchoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('E');
            }
        });
        t.addActor(taxiwayEchoButton);

        TextButton taxiwayFoxtrotButton = new TextButton("Taxiway F", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        taxiwayFoxtrotButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('F');
            }
        });
        t.addActor(taxiwayFoxtrotButton);

        TextButton taxiwayGolfButton = new TextButton("Taxiway G", Configuration.UI.getButtonStyle(false, false, Color.WHITE));
        taxiwayGolfButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected.exitRunway('G');
            }
        });
        t.addActor(taxiwayGolfButton);

        return t;
    }

    private VerticalGroup populatePlaneInfo() {
        VerticalGroup t = new VerticalGroup();

        Label.LabelStyle style = new Label.LabelStyle();

        style.font = Configuration.UI.getFont();
        style.fontColor = Color.LIGHT_GRAY;
        style.background = new SpriteDrawable(Configuration.UI.getButtonUpSlimSprite());
        float currentWidth = style.background.getMinWidth();
        float currentHeight = style.background.getMinHeight();
        style.background.setMinWidth(currentWidth * Configuration.UI.getScale());
        style.background.setMinHeight(currentHeight * Configuration.UI.getScale());

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

    public void addAirplane(final Airplane p) {
        TextButton button = new TextButton(p.toString(), Configuration.UI.getButtonStyle(true, false, Color.WHITE));
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                selected = p;
                column2Container.setVisible(true);
                planeInfoTableContainer.setVisible(true);
                setValueButtonGroup.uncheckAll();

                showButtons();
            }
        });
        p.setTextButton(button);
        airplaneButtonGroup.add(button);

        airplaneTable.addActor(button);
    }

    public void removeAirplane(Airplane p) {
        airplaneButtonGroup.remove(p.getTextButton());
        airplaneTable.removeActor(p.getTextButton());
    }

    public void select(Airplane p) {
        this.selected = p;
        this.airplaneButtonGroup.setChecked(p.toString());
    }

    public void deselectAll() {
        this.selected = null;
        this.airplaneButtonGroup.uncheckAll();
    }

    public void clearSelection() {
        this.selected = null;
    }
}
