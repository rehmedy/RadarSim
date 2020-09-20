package com.adamjrehm.radarsim.config;

import com.adamjrehm.radarsim.planes.PlaneType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CallsignManager {

    private static final CallsignManager callsignManager = new CallsignManager();

    private CallsignManager(){ }

    public static CallsignManager getInstance(){
        return callsignManager;
    }

    private static Array<Callsign> GAPlaneCallsigns;
    private static Array<Callsign> commercialPlaneCallsigns;

    public static Array<Callsign> getGAPlaneCallsigns() {
        return GAPlaneCallsigns;
    }

    public static Array<Callsign> getCommercialPlaneCallsigns() {
        return commercialPlaneCallsigns;
    }

    public boolean load(){
        boolean a = loadGeneralAviationAirplanes();
        boolean b = loadCommercialAirplanes();

        return a && b;
    }

    private boolean loadGeneralAviationAirplanes() {
        System.out.println("Loading general aviation airplane list...");

        GAPlaneCallsigns = new Array<>();

        String GAPlaneListPath = "./config/generalaviationplanes.txt";

        if (new File(GAPlaneListPath).exists()){

            try {
                Scanner scanner = new Scanner(new File(GAPlaneListPath));

                while (scanner.hasNextLine()){
                    String[] tokens = scanner.nextLine().split(" ");
                    String callsign = tokens[0];
                    PlaneType type = PlaneType.getPlaneTypeByID(tokens[1]);

                    GAPlaneCallsigns.add(new Callsign(callsign, type));
                }

                scanner.close();

                System.out.println("General aviation airplane list loaded successfully!");
            } catch (FileNotFoundException e) {
                System.err.println("General aviation airplane list FAILED to load!");
                e.printStackTrace();
            }

            return true;
        } else {
            File newFile = new File(GAPlaneListPath);
            Gdx.files.internal("configs/generalaviationplanes.txt").copyTo(new FileHandle(newFile));
            return false;
        }
    }

    private boolean loadCommercialAirplanes(){
        System.out.println("Loading commercial airplane list...");

        commercialPlaneCallsigns = new Array<>();

        String commercialPlaneListPath = "./config/commercialplanes.txt";

        if (new File(commercialPlaneListPath).exists()){

            try {
                Scanner scanner = new Scanner(new File(commercialPlaneListPath));
                scanner.useDelimiter(" ");

                while (scanner.hasNextLine()){
                    String[] tokens = scanner.nextLine().split(" ");
                    String callsign = tokens[0];
                    PlaneType type = PlaneType.getPlaneTypeByID(tokens[1]);

                    commercialPlaneCallsigns.add(new Callsign(callsign, type));
                }

                scanner.close();

                System.out.println("Commercial airplane list loaded successfully!");
            } catch (FileNotFoundException e) {
                System.err.println("Commercial airplane list FAILED to load!");
                e.printStackTrace();
            }

            return true;
        } else {
            File newFile = new File(commercialPlaneListPath);
            Gdx.files.internal("configs/commercialplanes.txt").copyTo(new FileHandle(newFile));
            return false;
        }
    }

    public static class Callsign {
        private String callsign;
        private PlaneType type;

        private Callsign(String callsign, PlaneType type){
            this.callsign = callsign;
            this.type = type;
        }

        public String getCallsign() {
            return callsign;
        }

        public PlaneType getType() {
            return type;
        }

    }
}
