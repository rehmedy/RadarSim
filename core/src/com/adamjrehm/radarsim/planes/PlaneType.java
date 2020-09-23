package com.adamjrehm.radarsim.planes;

import com.adamjrehm.radarsim.geography.Intersection;
import com.adamjrehm.radarsim.geography.Runway;

public enum PlaneType {

    C172(110, "C172", WeightCategory.CAT_I), AC68(180, "AC68", WeightCategory.CAT_II),
    BE20(210, "BE20", WeightCategory.CAT_III), BE36(130, "BE36", WeightCategory.CAT_I),
    BE40(210, "BE40", WeightCategory.CAT_III), BE58(140, "BE58", WeightCategory.CAT_II_SLOW),
    C208(130, "C208", WeightCategory.CAT_I), C310(140, "C310", WeightCategory.CAT_II),
    C421(170, "C421", WeightCategory.CAT_II), C550(210, "C550", WeightCategory.CAT_III),
    CL60(210, "CL60", WeightCategory.CAT_III), FA20(210, "FA20", WeightCategory.CAT_III),
    GLF3(210, "GLF3", WeightCategory.CAT_III), LJ35(210, "LJ35", WeightCategory.CAT_III),
    M20P(140, "M20P", WeightCategory.CAT_I), P28A(110, "P28A", WeightCategory.CAT_I),
    PA31(170, "PA31", WeightCategory.CAT_II), PA34(160, "PA34", WeightCategory.CAT_II),
    PA46(150, "PA46", WeightCategory.CAT_I), PAY3(160, "PAY3", WeightCategory.CAT_II),
    PC12(170, "PC12", WeightCategory.CAT_I_FAST), SR22(120, "SR22", WeightCategory.CAT_I),

    DH8A(160, "DH8A", WeightCategory.CAT_III), MD11(160, "H/MD11", WeightCategory.CAT_III),
    MD81(160, "MD81", WeightCategory.CAT_III), CRJ1(160, "CRJ1", WeightCategory.CAT_III),
    A306(160, "H/A306", WeightCategory.CAT_III), A320(160, "A320", WeightCategory.CAT_III),
    A342(160, "H/A342", WeightCategory.CAT_III), A343(160, "H/A343", WeightCategory.CAT_III_HEAVY),
    A345(160, "H/A345", WeightCategory.CAT_III), A346(160, "H/A346", WeightCategory.CAT_III),
    B722(160, "B722", WeightCategory.CAT_III),
    B733(160, "B733", WeightCategory.CAT_III), B734(160, "B734", WeightCategory.CAT_III),
    B735(160, "B735", WeightCategory.CAT_III), B736(160, "B736", WeightCategory.CAT_III),
    B743(160, "H/B743", WeightCategory.CAT_III_HEAVY), B744(160, "H/B744", WeightCategory.CAT_III),
    B752(160, "B752", WeightCategory.CAT_III), B753(160, "B753", WeightCategory.CAT_III),
    B762(160, "H/B762", WeightCategory.CAT_III), B763(160, "H/B763", WeightCategory.CAT_III_HEAVY),
    B772(160, "H/B772", WeightCategory.CAT_III), B773(160, "H/B773", WeightCategory.CAT_III_HEAVY);

    private int speed;
    private String id;
    private WeightCategory cat;

    PlaneType(int speed, String id, WeightCategory cat) {
        this.speed = speed;
        this.id = id;
        this.cat = cat;
    }

    public int getSpeed() {
        return speed;
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