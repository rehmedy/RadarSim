package com.adamjrehm.radarsim.geography;

public class Intersection extends Pattern {

    // 28R Arrivals & Departures
    public static final Intersection R28R_A = new Intersection("R28R/A", 668, 656);
    public static final Intersection R28R_C = new Intersection("R28R/C", 650, 659);
    public static final Intersection R28R_D = new Intersection("R28R/D", 643, 660);
    public static final Intersection R28R_E = new Intersection("R28R/E", 605, 667);
    public static final Intersection R28R_F = new Intersection("R28R/F", 593, 669);
    public static final Intersection R28R_G = new Intersection("R28R/G", 576, 672);

    // 28R/L Arrivals & Departures
    public static final Intersection R28R_HS_NORTH_A = new Intersection("R28R/HS NORTH A", 668.5f, 659);
    public static final Intersection R28R_HS_NORTH_C = new Intersection("R28R/HS NORTH C", 650.5f, 663);
    public static final Intersection R28R_HS_NORTH_D = new Intersection("R28R/HS NORTH D", 643.5f, 664);
    public static final Intersection R28R_HS_NORTH_E = new Intersection("R28R/HS NORTH E", 604, 671);
    public static final Intersection R28R_HS_NORTH_F = new Intersection("R28R/HS NORTH F", 592, 673);
    public static final Intersection R28R_HS_NORTH_G = new Intersection("R28R/HS NORTH G", 576.5f, 676);

    // 28L Arrivals Only
    public static final Intersection R28R_HS_SOUTH_D = new Intersection("R28R/HS SOUTH D", 642.5f, 657);
    public static final Intersection R28R_HS_SOUTH_E = new Intersection("R28R/HS SOUTH E", 606, 663);
    public static final Intersection R28R_HS_SOUTH_F = new Intersection("R28R/HS SOUTH F", 594, 665);
    public static final Intersection R28R_HS_SOUTH_G = new Intersection("R28R/HS SOUTH G", 575.5f, 668);

    // 28L Arrivals & Departures
    public static final Intersection R28L_A = new Intersection("R28L/A", 666, 645);
    public static final Intersection R28L_C = new Intersection("R28L/C", 648, 648);
    public static final Intersection R28L_D = new Intersection("R28L/D", 641, 649);
    public static final Intersection R28L_E = new Intersection("R28L/E", 609, 655);
    public static final Intersection R28L_F = new Intersection("R28L/F", 597, 657);
    public static final Intersection R28L_G = new Intersection("R28L/G", 575, 661);

    // 28L Departures Only
    public static final Intersection R28L_HS_NORTH_A = new Intersection("R28L/HS NORTH A", 666.5f, 648);
    public static final Intersection R28L_HS_NORTH_C = new Intersection("R28L/HS NORTH C", 648.5f, 651);

    // 16/34 Arrivals & Departures
    public static final Intersection R16_H = new Intersection("R16/H", 591, 690.5f);
    public static final Intersection R34_E = new Intersection("R34/E", 621.5f, 607);

    // 16 Departures Only
    public static final Intersection R16_HS_WEST_H = new Intersection("R16/HS WEST H", 587, 689);
    public static final Intersection R16_HS_EAST_H = new Intersection("R16/HS EAST H", 595, 692);

    public Intersection(String name, float x, float y) {
        super(name, x, y);
    }

}
