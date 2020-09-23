package com.adamjrehm.radarsim.planes;

import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.geography.*;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

public class Airplane extends Sprite {
    private Sprite line;
    private String callsign;
    private PlaneType type;
    private BitmapFont font;
    private Pattern last, next;
    private FPLType fpl;
    private Runway rwy;
    private int landings;
    private float speed;
    private float lastUpdateTime = 0;
    private float timeAlive = 0;
    private float tempTimer = -1;
    private float extendDownwind = 0, extendUpwind = 0, extendCrosswind = 0;
    private float extendDownwindCache = 0, extendUpwindCache = 0, extendCrosswindCache = 0;
    private boolean landed, taxiing, clearedForTakeoff = false, clearedToLand = false, goingAround = false, outOfJurisdiction;
    private boolean paused;
    private Vector2 pos, dir, vel;
    private Array<Pattern> alternatePattern;
    private Array<Pattern> departurePath;
    private Array<Intersection> landingPath;
    private Pattern finalDepartureInstruction;
    private TextButton textButton;

    /**
     * Inbound airplane constructor takes 4 parameters
     *
     * @param callsign   Aircraft's callsign or commercial identifier (eg. N172PT or DAL1128)
     * @param type       Type of aircraft defined by static PlaneType enum
     * @param startPoint Pattern object where the aircraft should spawn in on the scope
     * @param landings   How many landings the aircraft should intend to complete (>=1 if inbound, <0 if outbound)
     */
    protected Airplane(String callsign, PlaneType type, Pattern startPoint, int landings) {
        super(new Texture(GameInfo.TARGET_IMAGE_PATH));
        getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.callsign = callsign;
        this.type = type;
        this.speed = (float) type.getSpeed();
        this.last = startPoint;
        this.landings = landings;
        line = new Sprite(new Texture("images/greenbox.png"));
        line.setScale(1, 13);
        line.setRotation(45);

        initFont();

        if (startPoint == Pattern.ILS) {
            this.fpl = FPLType.IFR;
            this.rwy = Runway.R28R;
        } else {
            this.fpl = FPLType.VFR;
            this.rwy = Runway.R28L;
        }

        pos = new Vector2(last.getX(), last.getY());
        setPosition(pos.x - getWidth() / 2, pos.y - getHeight() / 2);  //Divide getWidth/Height by 2 to set anchor point to center of target image
    }

    /**
     * Outbound airplane constructor
     * Randomly picks a SID if IFR, otherwise, defaults runway heading for VFR
     *
     * @param callsign   Aircraft's callsign or commercial identifier (eg. N172PT or DAL1128)
     * @param type       Type of aircraft defined by static PlaneType enum
     * @param fpl        Flightplan type defined by static FPLType object
     * @param startPoint Intersection where airplane should spawn in on the scope
     */
    protected Airplane(String callsign, PlaneType type, FPLType fpl, Intersection startPoint) {
        super(new Texture(GameInfo.TARGET_IMAGE_PATH));
        getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.callsign = callsign;
        this.type = type;
        this.fpl = fpl;
        this.last = startPoint;
        this.landings = -1;         // -1 for departure
        this.taxiing = false;       // Aircraft spawn stopped
        this.speed = 0;             // ^^
        line = new Sprite(new Texture("images/greenbox.png"));
        line.setScale(1, 13);
        line.setRotation(45);
        initFont();

        if (startPoint == Intersection.R16_HS_WEST_H || startPoint == Intersection.R16_HS_EAST_H)
            this.rwy = Runway.R16;
        else
            this.rwy = Runway.R28R;     // All other aircraft will default to Runway 28R


        departurePath = new Array<>();
        generateDeparturePath();

        this.finalDepartureInstruction = departurePath.get(departurePath.size - 1);

        pos = new Vector2(last.getX(), last.getY());
        setPosition(pos.x - getWidth() / 2, pos.y - getHeight() / 2);  //Divide getWidth/Height by 2 to set anchor point to center of target image
    }


    /**
     * Touch & Go outbound airplane constructor
     * Creates a touch & go aircraft starting on the ground, then returning to land
     *
     * @param callsign
     * @param type
     * @param startPoint
     * @param touchAndGoes
     */
    protected Airplane(String callsign, PlaneType type, Intersection startPoint, int touchAndGoes) {
        super(new Texture(GameInfo.TARGET_IMAGE_PATH));
        getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.callsign = callsign;
        this.type = type;
        this.fpl = FPLType.VFR;
        this.last = startPoint;
        this.landings = -1 - touchAndGoes;          // Negative for departure, changed to positive after takeoff
        this.taxiing = false;                       // Aircraft spawn stopped
        this.speed = 0;                             // ^^
        line = new Sprite(new Texture("images/greenbox.png"));
        line.setScale(1, 13);
        line.setRotation(45);
        initFont();

        if (startPoint == Intersection.R16_HS_WEST_H || startPoint == Intersection.R16_HS_EAST_H)
            this.rwy = Runway.R16;
        else
            this.rwy = Runway.R28R;     // All other aircraft will default to Runway 28R


        departurePath = new Array<>();
        generateDeparturePath();

        pos = new Vector2(last.getX(), last.getY());
        setPosition(pos.x - getWidth() / 2, pos.y - getHeight() / 2);  //Divide getWidth/Height by 2 to set anchor point to center of target image
    }

    /**
     * Calculates & updates airplane every render cycle
     */
    public void update() {
        if (!paused) {
            // If we have not touched down on the runway for our final landing
            if (landings > 0) {     //If plane has not touched down for their final landing

                // If we do not have a "next" vector to fly to,
                // Check if we have touched down, deduct a landing, return to default pattern
                // Get the next default vector
                if (next == null) {
                    if (last == Pattern.TOUCHDOWN_28L || last == Pattern.TOUCHDOWN_28R) {
                        landings--;
                        extendUpwind = 0;
                        extendCrosswind = 0;
                        extendDownwind = 0;
                        extendUpwindCache = 0;
                        extendCrosswindCache = 0;
                        extendDownwindCache = 0;
                        alternatePattern = null;
                        if (!clearedToLand)
                            landings++;
                        clearedToLand = false;
                    }

                    System.out.println(callsign + ": Getting next vector...");
                    getNextVector();
                    System.out.println(callsign + ": Next Vector: " + next);
                    System.out.println(callsign + ": Current Alternate Pattern: " + alternatePattern);

                    // If the alternate pattern array is populated, use it
                    if (alternatePattern != null && alternatePattern.size > 0)
                        next = alternatePattern.removeIndex(0);

                    // Subtract destination vector from current position to find direction to that vector
                    // Normalize the result to be 1 unit of movement in that direction
                    dir = next.getVector2().cpy().sub(pos).nor();
                }

                // Reduce speed if cleared to land
                if (clearedToLand && speed > (.75f * type.getSpeed())) {
                    speed += Configuration.getPlaneDecelerationRate() * Gdx.graphics.getDeltaTime();
                }

                // If we're not cleared to land and under max speed, speed up
                else if (!clearedToLand && speed < type.getSpeed())
                    speed += Configuration.getPlaneAccelerationRate() * Gdx.graphics.getDeltaTime();

                // Scale direction vector to our speed * time
                // Update our position & store the time it took
                vel = dir.cpy().scl((float) pxMovedPerSecond() * Gdx.graphics.getDeltaTime());
                pos.set(pos.x + vel.x, pos.y + vel.y);
                lastUpdateTime += Gdx.graphics.getDeltaTime();

                // Update the sprite position on the batch one time per X second(s) to mimic a radar scope
                if (lastUpdateTime >= Configuration.getRadarUpdateSpeed()) {
//                System.out.println("Position: " + this.pos.x + " / " + this.pos.y);
//                System.out.println("Bounding Rectangle: " + this.getBoundingRectangle().x + " / " + this.getBoundingRectangle().y);
//                System.out.println("Center: " + (this.getBoundingRectangle().x + (this.getBoundingRectangle().width / 2)) + " / " + (this.getBoundingRectangle().y + (this.getBoundingRectangle().height / 2)));
//                System.out.println("Rectangle Width/Height: " + this.getBoundingRectangle().width + " / " + this.getBoundingRectangle().height);
//                System.out.println("Sprite Width/Height: " + this.getWidth() + " / " + this.getHeight());
                    setPosition(pos.x - getWidth() / 2, pos.y - getHeight() / 2);
                    lastUpdateTime = 0;
                }

                // If we have arrived at the destination vector, clear the destination
                // And set our last position to our previous destination
                if (pos.dst(next.getVector2()) < 1 / 2f) {
                    last = next;
                    next = null;
                }

                //drawDataTag(batch);
            }

            // Once we touch down for final landing, set 'landed' to true
            else if (landings == 0 && (last == Pattern.TOUCHDOWN_28L || last == Pattern.TOUCHDOWN_28R) && !landed) {
                landed = true;
                taxiing = true;
                next = null;
            }

            // If we're landed, taxi off the runway
            else if (landed && taxiing) {

                // Get the default runway exit path for the type of aircraft, populate an array
                if (landingPath == null)
                    landingPath = new Array<Intersection>(type.getDefaultLandingPath(rwy));

                // Determine the next vector from the landingPath array
                if (next == null && landingPath.size > 0)
                    next = landingPath.removeIndex(0);

                // Decelerate to 60 while on the runway and then to 20 when on the taxiway
                if (speed > 60 || (speed > 20 && landingPath.size == 0))
                    speed += (Configuration.getPlaneDecelerationRate() * Gdx.graphics.getDeltaTime());

                // Determine velocity then add that vector to our position
                dir = next.getVector2().cpy().sub(pos).nor();
                vel = dir.cpy().scl((float) pxMovedPerSecond() * Gdx.graphics.getDeltaTime());
                pos.set(pos.x + vel.x, pos.y + vel.y);
                lastUpdateTime += Gdx.graphics.getDeltaTime();

                // Update the sprite position on the batch one time per X second(s) to mimic a radar scope
                if (lastUpdateTime >= Configuration.getRadarUpdateSpeed()) {
                    setPosition(pos.x - getWidth() / 2, pos.y - getHeight() / 2);
                    lastUpdateTime = 0;
                }

                // If we have arrived at the destination vector, clear the destination
                // And set our last position to our previous destination
                if (pos.dst(next.getVector2()) < 1 / 2f) {
                    if (landingPath.size == 0) {
                        taxiing = false;
                        last = next;
                    } else {
                        last = next;
                        next = null;
                    }
                }

                //drawDataTag(batch);

            }

            // If the aircraft exits the runway onto Ground Control's taxiway
            // Allow the aircraft to sit in that position for between 3 - 30 seconds
            else if (landed && (last == Intersection.R28R_HS_NORTH_D || last == Intersection.R28R_HS_NORTH_E ||
                    last == Intersection.R28R_HS_NORTH_F || last == Intersection.R28R_HS_NORTH_G)) {
                if (tempTimer > 0)
                    tempTimer -= Gdx.graphics.getDeltaTime();
                else if (tempTimer == -1)
                    tempTimer = 3 + (float) (Math.random() * 27);
                else {
                    outOfJurisdiction = true;
                }

            } else if (landings < 0) {

                // If we have a next variable and we're taxiing, speed up to 20 knots
                if (next != null && taxiing) {
                    if (speed < 20)
                        speed += (Configuration.getPlaneAccelerationRate() * Gdx.graphics.getDeltaTime());
                }

                // If we're neither taxiing or cleared for takeoff, stop
                else if (!taxiing & !clearedForTakeoff) {
                    if (speed > 0)
                        speed += (Configuration.getPlaneDecelerationRate() * Gdx.graphics.getDeltaTime());
                }

                // If we're cleared for takeoff & no longer taxiing
                else if (clearedForTakeoff) {

                    // Accelerate to max speed
                    if (speed < type.getSpeed())
                        speed += Configuration.getPlaneAccelerationRate() * Gdx.graphics.getDeltaTime();

                    // If we have nothing more in our departure path, and no next variable
                    // We must be departed
                    if (next == null && departurePath.size == 0) {
                        if (landings == -1) {
                            clearedForTakeoff = false;
                            taxiing = false;
                            outOfJurisdiction = true;
                        } else {
                            clearedForTakeoff = false;
                            taxiing = false;
                            landings = -1 * landings;
                        }
                    }

                    // If the departure path has objects and we need to find our next vector
                    // And we are not waiting to line up on the runway, pull from the departure path
                    else if (!(last.equals(Intersection.R28L_HS_NORTH_A) || last.equals(Intersection.R28R_HS_NORTH_A) ||
                            last.equals(Intersection.R28R_HS_NORTH_C) || last.equals(Intersection.R28L_HS_NORTH_C) ||
                            last.equals(Intersection.R16_HS_WEST_H) || last.equals(Intersection.R16_HS_EAST_H)) && next == null) {
                        next = departurePath.removeIndex(0);
                        System.out.println(departurePath);
                    }

                    // If neither of those apply, we must be waiting to line up on the runway
                    else {
                        lineUp();
                    }
                }

                if (taxiing || clearedForTakeoff) {
                    dir = next.getVector2().cpy().sub(pos).nor();
                    vel = dir.cpy().scl((float) pxMovedPerSecond() * Gdx.graphics.getDeltaTime());
                    pos.set(pos.x + vel.x, pos.y + vel.y);
                    lastUpdateTime += Gdx.graphics.getDeltaTime();

                    // Update the sprite position on the batch one time per X second(s) to mimic a radar scope
                    if (lastUpdateTime >= Configuration.getRadarUpdateSpeed()) {
                        setPosition(pos.x - getWidth() / 2, pos.y - getHeight() / 2);
                        lastUpdateTime = 0;
                    }

                    // If we reach our next vector, set the last vector equal to that value, stop taxiing
                    // and clear the next vector
                    if (pos.dst(next.getVector2()) < 1 / 4f) {
                        taxiing = false;
                        last = next;
                        next = null;
                    }

                }

                //drawDataTag(batch);

            }

            timeAlive += Gdx.graphics.getDeltaTime();
        }
    }

    public void render(SpriteBatch batch) {
        drawDataTag(batch);
        batch.draw(this, getX(), getY());
    }

    public void dispose() {
        this.getTexture().dispose();
        line.getTexture().dispose();
        font.dispose();
    }

    /**
     * Gets the next vector in the sequence using the Airplane's 'next' and 'last' Pattern objects
     * Also handles extension of pattern & pattern turn instructions (eg. Extend upwind 2 miles, Turn base)
     */
    private void getNextVector() {
        // Define default path by defining a link from each vector to the next
        if (Pattern.SEVEN_WEST.equals(last) || PatternDrawable.PATTERN_ENTRY_FIVE.equals(last) || PatternDrawable.PATTERN_ENTRY_FOUR.equals(last) || PatternDrawable.PATTERN_ENTRY_THREE.equals(last) || Pattern.SOUTH_2MI.equals(last)) {
            next = Pattern.MIDFIELD_DOWNWIND;
        }

        // When the airplane reaches the point where it should turn crosswind,
        // Check if we are extending our upwind, and change the Pattern if needed
        else if (Pattern.CROSSWIND_START.equals(last)) {
            if (extendUpwind == 0)
                next = Pattern.MIDFIELD_DOWNWIND;
            else
                next = calculateNextVector(Pattern.TOUCHDOWN_28L,
                        Pattern.R28L_ONE_MILE_FINAL,
                        Pattern.CROSSWIND_START,
                        extendUpwind, false);
        }

        // Check if extending crosswind (see above)
        else if (Pattern.MIDFIELD_DOWNWIND.equals(last)) {
            if (extendCrosswind == 0)
                next = Pattern.BASE_START;
            else
                next = calculateNextVector(Pattern.MIDFIELD_DOWNWIND,
                        Pattern.CROSSWIND_START,
                        Pattern.MIDFIELD_DOWNWIND,
                        extendCrosswind, false);
        }

        // Check if extending downwind (see above)
        else if (Pattern.BASE_START.equals(last)) {
            if (extendDownwind == 0)
                //if (rwy == Runway.R28L)
                next = Pattern.R28L_ONE_MILE_FINAL;
                //else if (rwy == Runway.R28R)
                //next = Pattern.R28R_ONE_MILE_FINAL;
            else
                next = calculateNextVector(Pattern.R28L_TWO_MILE_FINAL,
                        Pattern.R28L_ONE_MILE_FINAL,
                        Pattern.BASE_START,
                        extendDownwind, false);
        } else if (PatternDrawable.PATTERN_ENTRY_TWO.equals(last)) {
            next = Pattern.BASE_START;
        } else if (Pattern.SEVEN_EAST.equals(last) || Pattern.EIGHT_EAST.equals(last)) {
            if (rwy == Runway.R28L)
                next = Pattern.R28L_FIVE_MILE_FINAL;
            else if (rwy == Runway.R28R)
                next = Pattern.R28R_FIVE_MILE_FINAL;
        } else if (PatternDrawable.PATTERN_ENTRY_ONE.equals(last) || Pattern.BASE_2MI.equals(last)) {
            if (rwy == Runway.R28L)
                next = Pattern.R28L_TWO_MILE_FINAL;
            else if (rwy == Runway.R28R)
                next = Pattern.R28R_TWO_MILE_FINAL;
        } else if (Pattern.R28L_ONE_MILE_FINAL.equals(last) || Pattern.R28L_TWO_MILE_FINAL.equals(last) || Pattern.R28L_FIVE_MILE_FINAL.equals(last)) {
            next = Pattern.TOUCHDOWN_28L;
        } else if (Pattern.R28R_ONE_MILE_FINAL.equals(last) || Pattern.R28R_TWO_MILE_FINAL.equals(last) || Pattern.R28R_FIVE_MILE_FINAL.equals(last)) {
            next = Pattern.TOUCHDOWN_28R;
        } else if (Pattern.ILS.equals(last)) {
            next = Pattern.TOUCHDOWN_28R;
        } else if (Pattern.TOUCHDOWN_28L.equals(last)) {
            next = Pattern.CROSSWIND_START;
        } else if (Pattern.TOUCHDOWN_28R.equals(last)) {
            if (fpl == FPLType.IFR)
                next = Pattern.R10L_FIVE_MILE_FINAL;
            else
                next = Pattern.R28R_CROSSWIND_START;
        } else if (Pattern.R28R_CROSSWIND_START.equals(last)) {
            next = Pattern.MIDFIELD_DOWNWIND;
        } else if (Pattern.R10L_FIVE_MILE_FINAL.equals(last)) {
            next = Pattern.R10L_FIVE_MILE_FINAL;
            makeRight360();
        }

        // If we are at a point not defined in our static Pattern objects, handle here
        else {
            // Handles extensions of pattern
            if (extendUpwind != 0 || extendDownwind != 0 || extendCrosswind != 0) {
                // Create an alternate pattern array for the airplane to follow
                if (alternatePattern == null)
                    alternatePattern = new Array<>();

                if (alternatePattern.size == 0)
                    populateAlternatePattern();

                // Extends upwind by locating where crosswind would normally start
                // Extends the crosswind start vector by 'extendUpwind' miles
                // Extends the normal downwind start vector by 'extendUpwind' miles
                if (extendUpwind != 0) {
                    if (alternatePattern.contains(Pattern.CROSSWIND_START, true)) {
                        int i = alternatePattern.indexOf(Pattern.CROSSWIND_START, true);
                        alternatePattern.set(i, calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.CROSSWIND_START,
                                extendUpwind, false)
                        );
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendUpwind, false)
                        );
                    }

                    // If we are calling extendUpwind more than once before we complete the initial extension
                    // Find the extended upwind points & extend those further
                    else if (alternatePattern.contains(calculateNextVector(
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.CROSSWIND_START,
                            extendUpwindCache,
                            false), false)) {

                        Pattern Crosswind_Ext_Upwind = calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.CROSSWIND_START,
                                extendUpwindCache,
                                false);
                        Pattern MDW_Ext_Upwind = calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendUpwindCache,
                                false);
                        int i = alternatePattern.indexOf(Crosswind_Ext_Upwind, false);

                        alternatePattern.set(i, calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Crosswind_Ext_Upwind,
                                extendUpwind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                MDW_Ext_Upwind,
                                extendUpwind,
                                false));
                    }

                    // If we call extendUpwind while in an extended upwind before turning crosswind
                    // Change the next vector & first vector in the alternate pattern
                    else if (last.equals(calculateNextVector(
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.CROSSWIND_START,
                            extendUpwindCache,
                            false))) {
                        Pattern Crosswind_Ext_Upwind = calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.CROSSWIND_START,
                                extendUpwindCache,
                                false);
                        Pattern MDW_Ext_Upwind = calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendUpwindCache,
                                false);
                        int i = alternatePattern.indexOf(MDW_Ext_Upwind, false);

                        alternatePattern.insert(i, calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Crosswind_Ext_Upwind,
                                extendUpwind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                MDW_Ext_Upwind,
                                extendUpwind,
                                false));
                    }

                    extendUpwindCache += extendUpwind;
                    System.out.println("Extend upwind");

                }

                // Extends crosswind by locating where downwind normally starts
                // If we did not extend upwind, extends the normal pattern's downwind start & base start out
                // by 'extendCrosswind' miles
                if (extendCrosswind != 0) {
                    if (alternatePattern.contains(Pattern.MIDFIELD_DOWNWIND, true)) {
                        int i = alternatePattern.indexOf(Pattern.MIDFIELD_DOWNWIND, true);
                        alternatePattern.set(i, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendCrosswind, false)
                        );
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswind, false)
                        );
                    }

                    // If we did extend upwind, extend the alternate pattern's downwind start &
                    // the default base start vector out by 'extendCrosswind' miles
                    else if (alternatePattern.contains(calculateNextVector(
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.MIDFIELD_DOWNWIND,
                            extendUpwindCache, false), false)) {
                        Pattern MDW_Ext_Upwind = calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendUpwindCache, false);
                        int i = alternatePattern.indexOf(MDW_Ext_Upwind, false);
                        alternatePattern.set(i, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                MDW_Ext_Upwind,
                                extendCrosswind, false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswind, false)
                        );
                    }

                    // If we previously extended crosswind, extend those vectors
                    else if (alternatePattern.contains(calculateNextVector(
                            Pattern.BASE_START,
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.MIDFIELD_DOWNWIND,
                            extendCrosswindCache,
                            false), false)) {

                        Pattern MDW_Ext_Crosswind = calculateNextVector(Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendCrosswindCache,
                                false);
                        Pattern Base_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswindCache,
                                false);
                        int i = alternatePattern.indexOf(MDW_Ext_Crosswind, false);

                        alternatePattern.set(i, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                MDW_Ext_Crosswind,
                                extendCrosswind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind,
                                extendCrosswind,
                                false));
                    }

                    // If we extended upwind && we have extended crosswind more than once
                    // extend those vectors
                    else if (alternatePattern.contains(calculateNextVector(
                            Pattern.BASE_START,
                            Pattern.R28L_ONE_MILE_FINAL,
                            calculateNextVector(
                                    Pattern.R28L_ONE_MILE_FINAL,
                                    Pattern.R28L_TWO_MILE_FINAL,
                                    Pattern.MIDFIELD_DOWNWIND,
                                    extendUpwindCache,
                                    false
                            ),
                            extendCrosswindCache,
                            false), false)) {
                        Pattern MDW_Ext_Upwind = calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendUpwindCache,
                                false);
                        Pattern MDW_Ext_Upwind_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                MDW_Ext_Upwind,
                                extendCrosswindCache,
                                false);
                        Pattern Base_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswindCache,
                                false);
                        int i = alternatePattern.indexOf(MDW_Ext_Upwind_Ext_Crosswind, false);

                        alternatePattern.set(i, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                MDW_Ext_Upwind_Ext_Crosswind,
                                extendCrosswind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind,
                                extendCrosswind,
                                false));
                    }

                    // If we are extending crosswind while in an extended crosswind,
                    // Do that
                    else if (last.equals(calculateNextVector(
                            Pattern.BASE_START,
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.MIDFIELD_DOWNWIND,
                            extendCrosswindCache,
                            false))) {

                        Pattern MDW_Ext_Crosswind = calculateNextVector(Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendCrosswindCache,
                                false);
                        Pattern Base_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswindCache,
                                false);
                        int i = alternatePattern.indexOf(Base_Ext_Crosswind, false);

                        alternatePattern.insert(i, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                MDW_Ext_Crosswind,
                                extendCrosswind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind,
                                extendCrosswind,
                                false));
                    }

                    // If we are extending crosswind while in an extended crosswind, and we extended upwind
                    // Do that
                    else if (last.equals(calculateNextVector(
                            Pattern.BASE_START,
                            Pattern.R28L_ONE_MILE_FINAL,
                            calculateNextVector(
                                    Pattern.R28L_ONE_MILE_FINAL,
                                    Pattern.R28L_TWO_MILE_FINAL,
                                    Pattern.MIDFIELD_DOWNWIND,
                                    extendUpwindCache,
                                    false
                            ),
                            extendCrosswindCache,
                            false))) {
                        Pattern MDW_Ext_Upwind = calculateNextVector(
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.MIDFIELD_DOWNWIND,
                                extendUpwindCache,
                                false);
                        Pattern MDW_Ext_Upwind_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                MDW_Ext_Upwind,
                                extendCrosswindCache,
                                false);
                        Pattern Base_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswindCache,
                                false);
                        int i = alternatePattern.indexOf(Base_Ext_Crosswind, false);

                        alternatePattern.insert(i, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                MDW_Ext_Upwind_Ext_Crosswind,
                                extendCrosswind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind,
                                extendCrosswind,
                                false));
                    }

                    extendCrosswindCache += extendCrosswind;
                    System.out.println("Extend crosswind");

                }

                // Extend downwind by locating where base normally starts
                // Extend the base & 1 mile final vector out by 'extendDownwind' miles
                if (extendDownwind != 0) {
                    System.out.println(calculateNextVector(
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.BASE_START,
                            extendDownwindCache,
                            false));
                    System.out.println(last);

                    if (alternatePattern.contains(Pattern.BASE_START, true)) {
                        int i = alternatePattern.indexOf(Pattern.BASE_START, true);
                        alternatePattern.set(i, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendDownwind, false)
                        );
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                extendDownwind, false)
                        );
                    }

                    // If we extended the crosswind, extend the modified crosswind vector &
                    // the normal 1 mile final vector by 'extendDownwind' miles
                    else if (alternatePattern.contains(calculateNextVector(
                            Pattern.BASE_START,
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.BASE_START,
                            extendCrosswind, false), false)) {
                        Pattern Base_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswind, false);
                        int i = alternatePattern.indexOf(Base_Ext_Crosswind, false);
                        alternatePattern.set(i, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind,
                                extendDownwind, false)
                        );
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                extendDownwind, false)
                        );
                    }


                    // If we previously extended the downwind, locate & extend that vector
                    else if (alternatePattern.contains(calculateNextVector(
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.BASE_START,
                            extendDownwindCache,
                            false), false)) {

                        System.out.println(last + "equals!!!!!!!!!!!!!!!!!!!");

                        Pattern Base_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendDownwindCache,
                                false);
                        Pattern Final_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                extendDownwindCache,
                                false);
                        int i = alternatePattern.indexOf(Base_Ext_Downwind, false);

                        alternatePattern.set(i, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Downwind,
                                extendDownwind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Final_Ext_Downwind,
                                extendDownwind,
                                false));

                    }

                    // If we previously extended the crosswind, and we're extending downwind
                    // more than once, locate & extend those vectors
                    else if (alternatePattern.contains(calculateNextVector(
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.R28L_ONE_MILE_FINAL,
                            calculateNextVector(
                                    Pattern.BASE_START,
                                    Pattern.R28L_ONE_MILE_FINAL,
                                    Pattern.BASE_START,
                                    extendCrosswindCache,
                                    false
                            ),
                            extendDownwindCache,
                            false), false)) {

                        Pattern Base_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswindCache,
                                false);
                        Pattern Base_Ext_Crosswind_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind,
                                extendDownwindCache,
                                false);
                        Pattern Final_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                extendDownwindCache,
                                false);
                        int i = alternatePattern.indexOf(Base_Ext_Crosswind_Ext_Downwind, false);

                        alternatePattern.set(i, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind_Ext_Downwind,
                                extendDownwind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Final_Ext_Downwind,
                                extendDownwind,
                                false
                        ));
                    }

                    // If we're extending the downwind while in an extended downwind
                    // DEW IT
                    else if (last.equals(calculateNextVector(
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.R28L_ONE_MILE_FINAL,
                            Pattern.BASE_START,
                            extendDownwindCache,
                            false))) {

                        Pattern Base_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendDownwindCache,
                                false);
                        Pattern Final_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                extendDownwindCache,
                                false);
                        int i = alternatePattern.indexOf(Final_Ext_Downwind, false);

                        alternatePattern.insert(i, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Downwind,
                                extendDownwind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Final_Ext_Downwind,
                                extendDownwind,
                                false));

                    }

                    // If we're extending the downwind while in an extended downwind, and we had extended crosswind
                    // DEW DAT
                    else if (last.equals(calculateNextVector(
                            Pattern.R28L_TWO_MILE_FINAL,
                            Pattern.R28L_ONE_MILE_FINAL,
                            calculateNextVector(
                                    Pattern.BASE_START,
                                    Pattern.R28L_ONE_MILE_FINAL,
                                    Pattern.BASE_START,
                                    extendCrosswindCache,
                                    false
                            ),
                            extendDownwindCache,
                            false))) {

                        Pattern Base_Ext_Crosswind = calculateNextVector(
                                Pattern.BASE_START,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                extendCrosswindCache,
                                false);
                        Pattern Base_Ext_Crosswind_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind,
                                extendDownwindCache,
                                false);
                        Pattern Final_Ext_Downwind = calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                extendDownwindCache,
                                false);
                        int i = alternatePattern.indexOf(Final_Ext_Downwind, false);

                        alternatePattern.insert(i, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Base_Ext_Crosswind_Ext_Downwind,
                                extendDownwind,
                                false));
                        alternatePattern.set(i + 1, calculateNextVector(
                                Pattern.R28L_TWO_MILE_FINAL,
                                Pattern.R28L_ONE_MILE_FINAL,
                                Final_Ext_Downwind,
                                extendDownwind,
                                false
                        ));
                    }

                    extendDownwindCache += extendDownwind;
                    System.out.println("Extend downwind");

                }
                System.out.println(alternatePattern + " size " + alternatePattern.size);

                // After calculating extensions, reset all the variables
                // Save values in cache for later calculations
                extendDownwind = 0;
                extendCrosswind = 0;
                extendUpwind = 0;
            } else if (goingAround) {
                outOfJurisdiction = true;
            }

        }
    }

    /**
     * Gets the next default Pattern vector from a given 'last' Pattern object
     *
     * @param last The last pattern point from where we want to find the next point
     * @return Returns the next pattern point in a default closed pattern
     */
    private Pattern getNextVector(Pattern last) {
        // Define default path by defining a link from each vector to the next
        if (Pattern.SEVEN_WEST.equals(last) || PatternDrawable.PATTERN_ENTRY_FIVE.equals(last) || PatternDrawable.PATTERN_ENTRY_FOUR.equals(last) || PatternDrawable.PATTERN_ENTRY_THREE.equals(last) || Pattern.SOUTH_2MI.equals(last)) {
            return Pattern.MIDFIELD_DOWNWIND;
        } else if (Pattern.CROSSWIND_START.equals(last)) {
            return Pattern.MIDFIELD_DOWNWIND;
        } else if (Pattern.MIDFIELD_DOWNWIND.equals(last)) {
            return Pattern.BASE_START;
        } else if (Pattern.BASE_START.equals(last)) {
            if (rwy == Runway.R28L)
                return Pattern.R28L_ONE_MILE_FINAL;
            else if (rwy == Runway.R28R)
                return Pattern.R28R_ONE_MILE_FINAL;
        } else if (PatternDrawable.PATTERN_ENTRY_TWO.equals(last)) {
            return Pattern.BASE_START;
        } else if (Pattern.SEVEN_EAST.equals(last) || Pattern.EIGHT_EAST.equals(last)) {
            return Pattern.R28L_FIVE_MILE_FINAL;
        } else if (PatternDrawable.PATTERN_ENTRY_ONE.equals(last) || Pattern.BASE_2MI.equals(last)) {
            return Pattern.R28L_TWO_MILE_FINAL;
        } else if (Pattern.R28L_ONE_MILE_FINAL.equals(last) || Pattern.R28L_TWO_MILE_FINAL.equals(last) || Pattern.R28L_FIVE_MILE_FINAL.equals(last)) {
            return Pattern.TOUCHDOWN_28L;
        } else if (Pattern.ILS.equals(last)) {
            return Pattern.TOUCHDOWN_28R;
        } else if (Pattern.TOUCHDOWN_28L.equals(last)) {
            return Pattern.CROSSWIND_START;
        } else if (Pattern.TOUCHDOWN_28R.equals(last)) {
            return Pattern.R10L_FIVE_MILE_FINAL;
        }
        return null;
    }

    /**
     * Calculates the next vector if default pattern has been modified
     * Direction is calculated by picking a 'from' and 'to' vector.
     * The new Vector direction will be the perspective of the 'from' vector going towards the 'to' vector
     *
     * @param to               Vector going to
     * @param from             Vector coming from
     * @param extendVector     Vector we are adding the extension onto
     *                         (eg. add one mile to where the crosswind would normally start to extend the upwind)
     * @param distanceToExtend How many miles to travel if usePixelDistance is false, otherwise how many pixels to travel
     * @param usePixelDistance True if using pixel distance, false if using miles
     * @return Returns a new pattern object that the airplane will fly to
     */
    private Pattern calculateNextVector(Pattern to, Pattern from, Pattern extendVector, float distanceToExtend, boolean usePixelDistance) {
        if (usePixelDistance)
            return new Pattern(
                    to.getVector2()
                            .cpy()
                            .sub(from.getVector2())
                            .nor()
                            .scl(distanceToExtend)
                            .add(extendVector.getVector2())
            );
        return new Pattern(
                to.getVector2()
                        .cpy()
                        .sub(from.getVector2())
                        .nor()
                        .scl((float) GameInfo.PIXELS_PER_MILE_RADAR * distanceToExtend)
                        .add(extendVector.getVector2())
        );
    }

    /**
     * Draws the data tag of the aircraft to the batch
     *
     * @param batch Spritebatch to draw to
     */
    private void drawDataTag(SpriteBatch batch) {
        // If the aircraft is airborne, draws data tag below aircraft
        // We'll assume the aircraft gets airborne at 75% of its max speed
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        if (speed >= (.74f * type.getSpeed())) {

            if (fpl == FPLType.IFR) {

                // Switches data tag every 2 seconds
                if ((int) timeAlive % 4 == 0 || (int) timeAlive % 4 == 1)
                    font.draw(batch, getDataTag1(), getX() + 30, getY() - 30);
                else
                    font.draw(batch, getDataTag2(), getX() + 30, getY() - 30);
            } else
                font.draw(batch, "1200\n---   " + (int) speed, getX() + 30, getY() - 30);

            // Extremely stupid way to draw a line to the spritebatch because I don't know any better

            line.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            line.setPosition(getX() + getWidth() + 2 + 9, getY() - 2 - 12);
            line.draw(batch);
        }
    }

    /**
     * Issues a command to an airplane to turn base immediately
     *
     * @return Returns true if command was successful, false if unable
     */
    public boolean turnBase() {
        // If we are preparing to turn base or if we only have 3 instructions left to execute
        // (Base > Final > Touchdown), then we can turn base early
        // NOTE: The turnBase() function does not properly handle an instruction given too early
        // The plane will always turn base if it satisfies these requirements
        if (next.equals(Pattern.BASE_START) ||
                (last.equals(Pattern.BASE_START) && !(next.equals(Pattern.R28L_ONE_MILE_FINAL) || next.equals(Pattern.R28R_ONE_MILE_FINAL))) ||
                (alternatePattern != null && alternatePattern.size == 2)) {

            // Set 'n' variable to one step forward, forcing the plane to update its instructions
            Pattern n = new Pattern(new Vector2(pos.x + vel.x, pos.y + vel.y));

            // If we are already in an alternate pattern, modify the elements to turn base
            if (alternatePattern != null && alternatePattern.size == 2) {
                alternatePattern.set(0, calculateNextVector(Pattern.R28L_ONE_MILE_FINAL,
                        Pattern.BASE_START,
                        n,
                        next.getVector2()
                                .cpy()
                                .dst(alternatePattern.get(0).getVector2()),
                        true));
            }

            // Otherwise, create a new alternate pattern with a modified base > final then directly to touchdown
            else {
                alternatePattern = new Array<Pattern>(new Pattern[]{
                        //next,
                        calculateNextVector(Pattern.R28L_ONE_MILE_FINAL,
                                Pattern.BASE_START,
                                n,
                                52, true),
                        Pattern.TOUCHDOWN_28L
                });
            }

            next = n;
            return true;
        }

        // If unable to turn base, return false
        return false;
    }

    /**
     * Gets the next Intersection in order to line up on the runway that the aircraft is currently holding short of
     */
    public void lineUp() {
        taxiing = true;
        if (rwy == Runway.R28R) {
            if (last.equals(Intersection.R28R_HS_NORTH_A))
                next = Intersection.R28R_A;
            else if (last.equals(Intersection.R28R_HS_NORTH_C))
                next = Intersection.R28R_C;
            else
                taxiing = false;
        } else if (rwy == Runway.R28L) {
            if (last.equals(Intersection.R28L_HS_NORTH_A) || last.equals(Intersection.R28R_HS_NORTH_A))
                next = Intersection.R28L_A;
            else if (last.equals(Intersection.R28L_HS_NORTH_C) || last.equals(Intersection.R28R_HS_NORTH_C))
                next = Intersection.R28L_C;
            else
                taxiing = false;
        } else if (rwy == Runway.R16) {
            if (last.equals(Intersection.R16_HS_EAST_H) || last.equals(Intersection.R16_HS_WEST_H))
                next = Intersection.R16_H;
            else
                taxiing = false;
        }
    }

    /**
     * Gets the next Intersection in order to cross the runway in front of the aircraft
     *
     * @return Returns Intersection across the runway
     */
    public void cross() {
        taxiing = true;
        if (last.equals(Intersection.R28R_HS_NORTH_A)) {
            next = Intersection.R28L_HS_NORTH_A;
            rwy = Runway.R28L;
            clearDeparturePath();
            generateDeparturePath();
        } else if (last.equals(Intersection.R28R_HS_NORTH_C)) {
            next = Intersection.R28L_HS_NORTH_C;
            rwy = Runway.R28L;
            clearDeparturePath();
            generateDeparturePath();
        } else if (last.equals(Intersection.R28R_HS_SOUTH_D) || last.equals(Intersection.R28L_D))
            next = Intersection.R28R_HS_NORTH_D;
        else if (last.equals(Intersection.R28R_HS_SOUTH_E) || last.equals(Intersection.R28L_E))
            next = Intersection.R28R_HS_NORTH_E;
        else if (last.equals(Intersection.R28R_HS_SOUTH_F) || last.equals(Intersection.R28L_F))
            next = Intersection.R28R_HS_NORTH_F;
        else if (last.equals(Intersection.R28R_HS_SOUTH_G) || last.equals(Intersection.R28L_G))
            next = Intersection.R28R_HS_NORTH_G;
        else if (last instanceof Intersection)
            taxiing = false;
    }

    public void clearForTakeoff() {
        clearedForTakeoff = true;
    }

    public void clearToLand() {
        this.clearedToLand = true;
    }

    /**
     * Instructs aircraft to use the designated runway
     *
     * @param rwy Runway enum
     */
    public void setRwy(Runway rwy) {
        this.rwy = rwy;
        if (landings < 0) {
            departurePath.clear();
            generateDeparturePath();
        } else if (landings > 0) {
            getNextVector();
            dir = next.getVector2().cpy().sub(pos).nor();
        }
    }

    public void setDeparturePath(DeparturePoint p) {

        // If the departurePath contains the SID TURN START, we have not yet reached the departure end
        // Of the runway, so change the path
        if (departurePath != null) {
            if (departurePath.contains(DeparturePoint.R28R_SID_TURN_START, true)) {
                departurePath.clear();
                departurePath.add(DeparturePoint.R28R_DEPARTURE_END);
                departurePath.add(p);
            } else if (departurePath.contains(DeparturePoint.R28L_SID_TURN_START, true)) {
                departurePath.clear();
                departurePath.add(DeparturePoint.R28L_DEPARTURE_END);
                departurePath.add(p);
            } else if (departurePath.contains(DeparturePoint.R16_SOUTH1, true) || departurePath.contains(DeparturePoint.R16_RH, true)) {
                departurePath.clear();
                departurePath.add(DeparturePoint.R16_DEPARTURE_END);
                departurePath.add(p);
            } else
                System.out.println(callsign + ": Unable to change heading.");
        } else
            System.out.println(callsign + ": Unable to change heading.");
    }

    /**
     * Instructs airplane to make a standard rate 360 degree turn to the left
     */
    public void makeLeft360() {
        make360('l');
    }

    /**
     * Instructs airplane to make a standard rate 360 degree turn to the right
     */
    public void makeRight360() {
        make360('r');
    }

    /**
     * Instructs airplane to extend their downwind for a number of miles
     *
     * @param miles
     */
    public void extendDownwind(float miles) {
        this.extendDownwind += miles;
    }

    /**
     * Instructs airplane to extend their crosswind for a number of miles
     *
     * @param miles
     */
    public void extendCrosswind(float miles) {
        this.extendCrosswind += miles;
    }

    /**
     * Instructs airplane to extend their upwind for a number of miles
     *
     * @param miles
     */
    public void extendUpwind(float miles) {
        this.extendUpwind += miles;
    }

    public void cancelLandingClearance() {
        this.clearedToLand = false;
    }

    public void goAroundIFR() {
        if (clearedToLand) {
            landings++;
            clearedToLand = false;
        }
        this.alternatePattern = null;
        this.next = new Pattern("IFR Go Around",
                this.dir.cpy()
                        .setAngle(70)
                        .scl((int) (20 * GameInfo.PIXELS_PER_MILE_RADAR))
                        .add(pos));
        this.dir = next.getVector2().cpy().sub(pos).nor();
        goingAround = true;
    }

    public void goAroundVFR() {
        if (clearedToLand) {
            clearedToLand = false;
        }
        this.alternatePattern = null;
    }

    /**
     * HELPER FUNCTION NOT TO BE ACCESSED DIRECTLY
     * Updates the aircraft's instructions to make a standard rate 360 turn in the indicated direction
     *
     * @param direction 'l' for left, 'r' for right
     */
    private void make360(char direction) {
        Array<Pattern> temp = new Array<Pattern>();

        // We will use the alternatePattern array to construct a 360 turn relative to airplane speed
        // If it already exists and has elements, store them in a temp array
        // If it doesn't exist, create it
        if (alternatePattern != null && alternatePattern.size > 0) {
            temp = alternatePattern;
            alternatePattern.clear();
        } else if (alternatePattern == null) {
            alternatePattern = new Array<Pattern>();
        }

        // Calculate approx bank angle for a 3 degree standard rate turn
        // Approximated by 15% of true airspeed
        double bankAngle = 0.15f * speed;

        // Calculate radius of turn
        // Turn Radius in feet = V^2 / 11.26 * tan(bank angle in RADIANS)
        double turnRadiusInFeet = (speed * speed) / (11.26 * Math.tan(Math.toRadians(bankAngle)));
        double turnRadiusInNM = turnRadiusInFeet / 6076.11549;

        // Calculate points of 360 turn
        if (direction == 'l') {
            Vector2 radius = dir.cpy().rotate90(1).scl((float) GameInfo.PIXELS_PER_MILE_RADAR * (float) turnRadiusInNM);
            Vector2 center = pos.cpy().add(radius);

            for (int i = 0; i != 12; i++) {
                Vector2 point = center.cpy().add(radius.cpy().rotate(-150 + (i * 30)));
                alternatePattern.add(new Pattern(point));
            }
        } else {
            Vector2 radius = dir.cpy().rotate90(-1).scl((float) GameInfo.PIXELS_PER_MILE_RADAR * (float) turnRadiusInNM);
            Vector2 center = pos.cpy().add(radius);

            for (int i = 0; i != 12; i++) {
                Vector2 point = center.cpy().add(radius.cpy().rotate(150 + (i * -30)));
                alternatePattern.add(new Pattern(point));
            }
        }

        // Add the most recent next variable back to the alternate pattern
        alternatePattern.add(next);

        // If the alternatePattern previously had values, add them back
        if (temp.size > 0)
            alternatePattern.addAll(temp);

        // Clear out the next variable, forcing the plane to update its routing
        next = null;
    }

    /**
     * Populates our departure path with a default departure path
     */
    public void generateDeparturePath(DeparturePoint departure) {
        if (rwy == Runway.R28R)
            departurePath.add(DeparturePoint.R28R_DEPARTURE_END, DeparturePoint.R28R_SID_TURN_START, departure);
        else if (rwy == Runway.R28L)
            departurePath.add(DeparturePoint.R28L_DEPARTURE_END, DeparturePoint.R28L_SID_TURN_START, departure);
        else if (rwy == Runway.R16)
            departurePath.add(DeparturePoint.R16_DEPARTURE_END, DeparturePoint.R16_SOUTH1);
    }

    public void generateDeparturePath() {
        // Generate departure path for aircraft departing
        if (landings == -1) {
            // Generate SID for IFR aircraft with no assigned departure
            if (fpl == FPLType.IFR) {
                generateDeparturePath((Math.random() >= .5f ? DeparturePoint.R28R_L_NORTH1 : DeparturePoint.R28R_L_SOUTH1));
            }

            // VFR aircraft will fly runway heading
            // Seven west of R28L is equivalent to RH
            else {
                if (rwy == Runway.R28R)
                    departurePath.add(DeparturePoint.R28R_DEPARTURE_END, DeparturePoint.R28R_RH);
                else if (rwy == Runway.R28L)
                    departurePath.add(DeparturePoint.R28L_DEPARTURE_END, DeparturePoint.SEVEN_WEST);
                else if (rwy == Runway.R16)
                    departurePath.add(DeparturePoint.R16_DEPARTURE_END, DeparturePoint.R16_RH);
            }
        }

        // Generate departure path for aircraft departing for touch & goes
        else if (landings < -1) {
            if (rwy == Runway.R28R) {
                departurePath.add(Pattern.R28R_CROSSWIND_START);
            } else if (rwy == Runway.R28L) {
                departurePath.add(Pattern.CROSSWIND_START);
            }
        }
    }

    public void clearDeparturePath() {
        this.departurePath.clear();
    }

    /**
     * Populates our alternate pattern with the default closed pattern all the way to touchdown
     */
    private void populateAlternatePattern() {
        // We can determine if we need to start on the upwind, crosswind, or downwind by checking sequentially
        if (alternatePattern.size == 0) {
            if (extendUpwind != 0)
                alternatePattern.add(Pattern.CROSSWIND_START);
            else if (extendCrosswind != 0)
                alternatePattern.add(Pattern.MIDFIELD_DOWNWIND);
            else if (extendDownwind != 0)
                alternatePattern.add(Pattern.BASE_START);
            System.out.println(alternatePattern.get(0));
        }

        // Generate the pattern points until the airplane should touchdown, then return to default
        while (alternatePattern.get(alternatePattern.size - 1) != Pattern.TOUCHDOWN_28L) {
            System.out.println(alternatePattern.get(alternatePattern.size - 1));
            alternatePattern.add(getNextVector(alternatePattern.get(alternatePattern.size - 1)));
        }
    }

    /**
     * Determines how many pixels to move per second to move correct screen on radar display
     *
     * @return Returns double value for pixels to move per second
     */
    private double pxMovedPerSecond() {
        return (GameInfo.PIXELS_PER_MILE_RADAR * this.speed) / 3600;
    }

    public BitmapFont getFont() {
        return font;
    }

    /**
     * Initializes font object & declares settings
     */
    private void initFont() {
        this.font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1);
        font.setColor(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, .6f);
    }

    /**
     * Generates data tag part 1 for aircraft (Callsign, altitude, and type)
     *
     * @return Returns Data Tag String
     */
    public String getDataTag1() {
        return callsign + "\n---   " + type.getId();
    }

    /**
     * Generates data tag part 2 for aircraft (Callsign, altitude, and speed)
     */
    public String getDataTag2() {
        return callsign + "\n---   " + (int) speed;
    }

    public float getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Determines if two aircraft are equal using callsign & type
     *
     * @param o Airplane to compare to
     * @return Returns true if equal
     */
    @Override
    public boolean equals(Object o) {
        Airplane p = (Airplane) o;
        return (this.callsign.equals(p.callsign) && this.type == p.type);
    }

    @Override
    public String toString() {
        return callsign + " (" + type.getId() + ")";
    }

    /**
     * Determines if an aircraft has departed the airspace
     *
     * @return Return true if aircraft has departed
     */
    public boolean isGone() {
        return outOfJurisdiction;
    }

    /**
     * Gets the connecting line sprite between the data tag and the target
     *
     * @return Returns the sprite line
     */
    public Sprite getLine() {
        return line;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setTextButton(TextButton button) {
        this.textButton = button;
    }

    public TextButton getTextButton() {
        return this.textButton;
    }

    public int getLandings() {
        return this.landings;
    }

    public int getSpeed() {
        return (int) this.speed;
    }

    public void setLandings(int landings) {
        this.landings = landings;
    }

    public Pattern getNext() {
        return next;
    }

    public Pattern getLast() {
        return last;
    }

    public PlaneType getType() {
        return this.type;
    }

    public boolean isAirborne() {
        return speed >= (.74f * type.getSpeed());
    }

    public int getTouchAndGoCount() {
        if (landings <= 1 && landings >= -1)
            return 0;
        else if (landings > 1)
            return landings - 1;
        else
            return (landings * -1) - 1;
    }

    public String inboundOrOutbound() {
        if (landings >= 0)
            return "Inbound";
        return "Outbound";
    }

    public int getDistanceFromAirport() {
        double distance = pos.dst(new Vector2(625, 625)) / GameInfo.PIXELS_PER_MILE_RADAR;

        return (int) Math.round(distance);
    }

    public String getDirectionFromAirport() {
        float angle = pos.cpy().sub(new Vector2(625, 625)).angle();

        if (angle > 337.5 || angle <= 22.5)
            return "E";
        else if (angle > 22.5 && angle <= 67.5)
            return "NE";
        else if (angle > 67.5 && angle <= 112.5)
            return "N";
        else if (angle > 112.5 && angle <= 157.5)
            return "NW";
        else if (angle > 157.5 && angle <= 202.5)
            return "W";
        else if (angle > 202.5 && angle <= 247.5)
            return "SW";
        else if (angle > 247.5 && angle < 292.5)
            return "S";
        else if (angle > 292.5 && angle <= 337.5)
            return "SE";

        return "Unknown Direction";
    }

    public void setNextVector(Pattern p) {
        this.next = p;
        this.dir = next.getVector2().cpy().sub(pos).nor();
    }

    /**
     * Changes the runway exit to the defined taxiway
     * (eg. Turn right taxiway foxtrot)
     *
     * @param taxiway Char (D, E, F, G) that the airplane should exit the runway at
     */
    public void exitRunway(char taxiway) {
        if (landingPath != null)
            landingPath.clear();
        else
            landingPath = new Array<Intersection>();

        if (this.rwy == Runway.R28R) {
            if (taxiway == 'D')
                landingPath.add(Intersection.R28R_D, Intersection.R28R_HS_NORTH_D);
            else if (taxiway == 'E')
                landingPath.add(Intersection.R28R_E, Intersection.R28R_HS_NORTH_E);
            else if (taxiway == 'F')
                landingPath.add(Intersection.R28R_F, Intersection.R28R_HS_NORTH_F);
            else if (taxiway == 'G')
                landingPath.add(Intersection.R28R_G, Intersection.R28R_HS_NORTH_G);
        } else if (this.rwy == Runway.R28L) {
            if (taxiway == 'D')
                landingPath.add(Intersection.R28L_D, Intersection.R28R_HS_SOUTH_D);
            else if (taxiway == 'E')
                landingPath.add(Intersection.R28L_E, Intersection.R28R_HS_SOUTH_E);
            else if (taxiway == 'F')
                landingPath.add(Intersection.R28L_F, Intersection.R28R_HS_SOUTH_F);
            else if (taxiway == 'G')
                landingPath.add(Intersection.R28L_G, Intersection.R28R_HS_SOUTH_G);
        } else
            landingPath = new Array<>(type.getDefaultLandingPath(rwy));
    }

    public FPLType getFPLType() {
        return this.fpl;
    }

    public boolean contains(float x, float y) {
        Rectangle boundingRectangle = this.getBoundingRectangle();
        return x >= boundingRectangle.x && x <= boundingRectangle.x + boundingRectangle.width &&
                y >= boundingRectangle.y && y <= boundingRectangle.y + boundingRectangle.height;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public Pattern getFinalDepartureInstruction() {
        if (this.finalDepartureInstruction != null)
            return this.finalDepartureInstruction;
        return null;
    }

}
