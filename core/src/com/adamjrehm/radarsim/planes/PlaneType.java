package com.adamjrehm.radarsim.planes;

import com.adamjrehm.radarsim.geography.Intersection;
import com.adamjrehm.radarsim.geography.Runway;

public enum PlaneType {

    C172(110, 60, 110, 60, 300,"C172", WeightCategory.CAT_I),
    AC68(180, 100, 180, 100, 455, "AC68", WeightCategory.CAT_II),
    BE20(210, 105,230, 115, 570, "BE20", WeightCategory.CAT_III),
    BE36(130, 75, 150, 75, 350, "BE36", WeightCategory.CAT_I),
    BE40(210, 110, 250, 130, 1200, "BE40", WeightCategory.CAT_III),
    BE58(140, 95, 175, 100, 700, "BE58", WeightCategory.CAT_II_SLOW),
    C208(130, 85, 160, 85, 500, "C208", WeightCategory.CAT_I),
    C310(140, 95, 175, 95, 505, "C310", WeightCategory.CAT_II),
    C421(170, 95, 200, 100, 600, "C421", WeightCategory.CAT_II),
    C550(210, 110, 240, 115, 1000, "C550", WeightCategory.CAT_III),
    CL60(210, 125, 250, 145, 1720, "CL60", WeightCategory.CAT_III),
    FA20(210, 110, 250, 120, 1600, "FA20", WeightCategory.CAT_III),
    GLF3(210, 130, 250, 145, 1800, "GLF3", WeightCategory.CAT_III),
    LJ35(210, 130, 250, 140, 1500, "LJ35", WeightCategory.CAT_III),
    M20P(140, 70, 150, 70, 450, "M20P", WeightCategory.CAT_I),
    P28A(110, 65, 110, 65, 300, "P28A", WeightCategory.CAT_I),
    PA31(170, 100, 210, 90, 400, "PA31", WeightCategory.CAT_II),
    PA34(160, 85, 160, 80, 300, "PA34", WeightCategory.CAT_II),
    PA46(150, 85, 200, 80, 450, "PA46", WeightCategory.CAT_I),
    PAY3(160, 110, 240, 105, 700, "PAY3", WeightCategory.CAT_II),
    PC12(170, 85, 210, 110, 600, "PC12", WeightCategory.CAT_I_FAST),
    SR22(120, 80, 140, 70, 450, "SR22", WeightCategory.CAT_I),

    DH8A(160, 105, 180, 140, 900, "DH8A", WeightCategory.CAT_III),
    MD11(160, 150, 250, 175, 3100, "H/MD11", WeightCategory.CAT_III),
    MD81(160, 130, 250, 140, 2400, "MD81", WeightCategory.CAT_III),
    CRJ1(160, 140, 250, 135, 1600, "CRJ1", WeightCategory.CAT_III),
    A306(160, 135, 250, 160, 2240, "H/A306", WeightCategory.CAT_III),
    A320(160, 138, 250, 145, 2190, "A320", WeightCategory.CAT_III),
    A342(160, 150, 250, 145, 2765, "H/A342", WeightCategory.CAT_III),
    A343(160, 150, 250, 145, 2765, "H/A343", WeightCategory.CAT_III_HEAVY),
    A345(160, 150, 250, 145, 2765, "H/A345", WeightCategory.CAT_III),
    A346(160, 150, 250, 145, 2765, "H/A346", WeightCategory.CAT_III),
    B722(160, 125, 250, 145, 3000, "B722", WeightCategory.CAT_III),
    B733(160, 135, 250, 140, 1600, "B733", WeightCategory.CAT_III),
    B734(160, 140, 250, 150, 2000, "B734", WeightCategory.CAT_III),
    B735(160, 130, 250, 145, 1500, "B735", WeightCategory.CAT_III),
    B736(160, 125, 250, 145, 1900, "B736", WeightCategory.CAT_III),
    B743(160, 140, 250, 180, 3300, "H/B743", WeightCategory.CAT_III_HEAVY),
    B744(160, 160, 250, 185, 3300, "H/B744", WeightCategory.CAT_III),
    B752(160, 135, 250, 145, 1900, "B752", WeightCategory.CAT_III),
    B753(160, 145, 250, 145, 2600, "B753", WeightCategory.CAT_III),
    B762(160, 135, 250, 160, 2700, "H/B762", WeightCategory.CAT_III),
    B763(160, 145, 250, 160, 2900, "H/B763", WeightCategory.CAT_III_HEAVY),
    B772(160, 140, 250, 170, 2900, "H/B772", WeightCategory.CAT_III),
    B773(160, 150, 250, 170, 3000, "H/B773", WeightCategory.CAT_III_HEAVY);

    private int arrivalSpeed;
    private int touchdownSpeed;
    private int departureSpeed;
    private int rotationSpeed;
    private int departureDistanceInMeters;
    private String id;
    private WeightCategory cat;

    PlaneType(int arrivalSpeed, int touchdownSpeed, int departureSpeed, int rotationSpeed, int departureDistanceInMeters, String id, WeightCategory cat){
        this.departureSpeed = departureSpeed;
        this.arrivalSpeed = arrivalSpeed;
        this.touchdownSpeed = touchdownSpeed;
        this.rotationSpeed = rotationSpeed;
        this.departureDistanceInMeters = departureDistanceInMeters;
        this.id = id;
        this.cat = cat;
    }

    public int getArrivalSpeed() {
        return arrivalSpeed;
    }

    public int getTouchdownSpeed() {
        return touchdownSpeed;
    }

    public int getDepartureSpeed() {
        return departureSpeed;
    }

    public int getRotationSpeed() {
        return rotationSpeed;
    }

    public int getDepartureDistanceInMeters() {
        return departureDistanceInMeters;
    }


    public String getId() {
        return id;
    }

    public WeightCategory getWeightCategory() {
        return cat;
    }

    public Intersection[] getDefaultLandingPath(Runway rwy) {
        return cat.getDefaultLandingPath(rwy);
    }

    public static PlaneType getPlaneTypeByID(String id) {
        for (PlaneType p : PlaneType.values()) {
            if (p.id.contains(id))
                return p;
        }
        return null;
    }
}