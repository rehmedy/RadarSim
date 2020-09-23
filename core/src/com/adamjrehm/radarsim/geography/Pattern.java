package com.adamjrehm.radarsim.geography;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.adamjrehm.radarsim.geography.PatternDrawable.*;

public class Pattern {

    public static final Pattern ILS = new Pattern("ILS Approach", 1236, 555);
    public static final Pattern SEVEN_EAST = new Pattern("Seven East", 1115, 519);
    public static final Pattern EIGHT_EAST = new Pattern("Eight East", 1125, 640);
    public static final Pattern SEVEN_WEST = new Pattern("Seven West", 175, 730);
    public static final Pattern R28R_CROSSWIND_START = new Pattern("R28R Crosswind Start", 598, 668);
    public static final Pattern CROSSWIND_START = new Pattern("R28L Crosswind Start", 597, 657);
    public static final Pattern MIDFIELD_DOWNWIND = new Pattern("Midfield Downwind", 590, 606);
    public static final Pattern BASE_START = new Pattern("Base Start", 718, 583);
    public static final Pattern BASE_2MI = new Pattern("Two Mile Base Start", 773, 573);
    public static final Pattern R28L_ONE_MILE_FINAL = new Pattern("R28L One Mile Final", 725, 634);
    public static final Pattern R28R_ONE_MILE_FINAL = new Pattern("R28R One Mile Final", 726, 645.5f);
    public static final Pattern R28L_TWO_MILE_FINAL = new Pattern("R28L Two Mile Final", 781, 624);
    public static final Pattern R28R_TWO_MILE_FINAL = new Pattern("R28R Two Mile Final", 782, 636);
    public static final Pattern R28L_FIVE_MILE_FINAL = new Pattern("R28L Five Mile Final", 950, 594);
    public static final Pattern R28R_FIVE_MILE_FINAL = new Pattern("R28R Five Mile Final", 952.5f, 605.5f);
    public static final Pattern TOUCHDOWN_28L = new Pattern("R28L Touchdown Point", 659, 646);
    public static final Pattern TOUCHDOWN_28R = new Pattern("R28R Touchdown Point", 661, 657);
    public static final Pattern SOUTH_2MI = new Pattern("Two Miles South", 612, 489);
    public static final Pattern R10L_FIVE_MILE_FINAL = new Pattern("R10L Five Mile Final", 292, 722);

    public static Pattern[] values() {
        Array<Pattern> values = new Array<Pattern>(new Pattern[]{
                ILS, SEVEN_WEST, SEVEN_EAST, EIGHT_EAST, PATTERN_ENTRY_ONE, PATTERN_ENTRY_TWO, PATTERN_ENTRY_THREE, PATTERN_ENTRY_FOUR, PATTERN_ENTRY_FIVE, R28R_CROSSWIND_START,
                CROSSWIND_START, MIDFIELD_DOWNWIND, BASE_2MI, BASE_START, R28L_ONE_MILE_FINAL, R28L_TWO_MILE_FINAL, R28L_FIVE_MILE_FINAL,
                R28R_ONE_MILE_FINAL, R28R_TWO_MILE_FINAL, R28R_FIVE_MILE_FINAL, TOUCHDOWN_28L, TOUCHDOWN_28R, SOUTH_2MI, R10L_FIVE_MILE_FINAL
        });

        return values.toArray();
    }

    private String name;
    private Vector2 vector;

    public Pattern(String name, float x, float y) {
        this.name = name;
        this.vector = new Vector2(x, y);
    }

    public Pattern(Vector2 v) {
        this.vector = v;
    }

    public Pattern(String name, Vector2 v) {
        this.name = name;
        this.vector = v;
    }

    public Vector2 getVector2() {
        return vector;
    }

    public String getName() {
        return name;
    }

    public float getX() {
        return vector.x;
    }

    public float getY() {
        return vector.y;
    }

    @Override
    public boolean equals(Object o) {
        Pattern p = (Pattern) o;
        return p.getVector2().epsilonEquals(getVector2());
    }

    @Override
    public String toString() {
        return "Pattern " + name + ": " + getX() + "/" + getY();
    }

}
