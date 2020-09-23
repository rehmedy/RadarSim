package com.adamjrehm.radarsim.geography;

import com.adamjrehm.radarsim.config.Configuration;

public class DeparturePoint extends Pattern {
    public static final DeparturePoint R28R_DEPARTURE_END = new DeparturePoint("R28R Departure End", 576, 672);
    public static final DeparturePoint R28R_SID_TURN_START = new DeparturePoint("R28R SID Turn Start", 490, 687);
    public static final DeparturePoint R28R_L_NORTH1 = new DeparturePoint("R28R/L " + Configuration.getNorthSIDName() + " SID", 490, 1120);
    public static final DeparturePoint R28R_L_SOUTH1 = new DeparturePoint("R28R/L " + Configuration.getSouthSIDName() + " SID", 490, 240);
    public static final DeparturePoint R28R_310 = new DeparturePoint("Heading 310", 179, 1000);
    public static final DeparturePoint R28R_295 = new DeparturePoint("Heading 295", 114, 893);
    public static final DeparturePoint R28R_RH = new DeparturePoint("Heading 280", 72, 761);
    public static final DeparturePoint R28R_265 = new DeparturePoint("Heading 265", 64, 614);
    public static final DeparturePoint R28R_250 = new DeparturePoint("Heading 250", 89, 487);

    public static final DeparturePoint R28L_DEPARTURE_END = new DeparturePoint("R28L Departure End", 575, 660.5f);
    public static final DeparturePoint R28L_SID_TURN_START = new DeparturePoint("R28L SID Turn Start", 488, 675.5f);

    public static final DeparturePoint R16_DEPARTURE_END = new DeparturePoint("R16 Departure End", 621.5f, 607);
    public static final DeparturePoint R16_RH = new DeparturePoint("Heading 160", 795.5f, 128);
    public static final DeparturePoint R16_SOUTH1 = new DeparturePoint("R16 " + Configuration.getSouthSIDName() + " SID", 621, 100);

    public DeparturePoint(String name, float x, float y) {
        super(name, x, y);
    }

    public static DeparturePoint[] values() {
        return new DeparturePoint[]{R28R_L_SOUTH1, R28R_L_NORTH1, R28R_250, R28R_265, R28R_RH, R28R_295, R28R_310,
                R16_SOUTH1, R16_RH};
    }
}
