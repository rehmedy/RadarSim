package com.adamjrehm.radarsim.planes;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.config.CallsignManager;
import com.adamjrehm.radarsim.geography.PatternDrawable;
import com.adamjrehm.radarsim.huds.AirplaneCommandHandler;
import com.adamjrehm.radarsim.geography.Intersection;
import com.adamjrehm.radarsim.geography.Pattern;
import com.adamjrehm.radarsim.huds.StripHandler;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PlaneController implements InputProcessor {
    private RadarSim sim;

    private Array<Airplane> planelist;

    private AirplaneCommandHandler commandHandler;

    private StripHandler stripHandler;

    private Pattern lastUsedInboundStartPoint;

    private Array<CallsignManager.Callsign> tempGAPlaneList;
    private Array<CallsignManager.Callsign> tempCommercialPlaneList;
    private Array<Airplane> arrivalList, departureList;

    private boolean simPaused = false;

    private int operationsCounter = 0;

    public PlaneController(RadarSim sim) {
        this.sim = sim;

        planelist = new Array<>();
        commandHandler = new AirplaneCommandHandler(sim, this);

        tempCommercialPlaneList = CallsignManager.getCommercialPlaneCallsigns();
        tempGAPlaneList = CallsignManager.getGAPlaneCallsigns();

        arrivalList = new Array<>();
        populateArrivals(4);

        departureList = new Array<>();
        populateDepartures(4);

        stripHandler = new StripHandler(this);
    }

    private void populateArrivals(int count) {
        for (int i = 0; i != count; i++){
            int randomIndex = (int)(Math.random() * (tempCommercialPlaneList.size - 1));
            arrivalList.add(createNewUnspawnedArrival(tempCommercialPlaneList.get(randomIndex).getCallsign(),
                    tempCommercialPlaneList.get(randomIndex).getType(),
                    Pattern.ILS,
                    1));
            tempCommercialPlaneList.removeIndex(randomIndex);
        }
    }

    private void populateDepartures(int count){
        for (int i = 0; i != count; i++) {
            int randomIndex = (int) (Math.random() * (tempCommercialPlaneList.size - 1));
            departureList.add(createNewUnspawnedDeparture(tempCommercialPlaneList.get(randomIndex).getCallsign(),
                    tempCommercialPlaneList.get(randomIndex).getType(),
                    FPLType.IFR,
                    Intersection.R28R_HS_NORTH_A));
            tempCommercialPlaneList.removeIndex(randomIndex);
        }
    }

    /**
     * Create a new inbound plane
     * @param callsign
     * @param type
     * @param startPoint
     * @param numLandings
     * @return
     */
    public Airplane createNewPlane(String callsign, PlaneType type, Pattern startPoint, int numLandings) {
        Airplane p = new Airplane(callsign, type, startPoint, numLandings);
        planelist.add(p);
        commandHandler.addAirplane(p);
        operationsCounter++;
        return p;
    }

    /**
     * Create a new outbound plane
     * @param callsign
     * @param type
     * @param fpl
     * @param startPoint
     * @return
     */
    public Airplane createNewPlane(String callsign, PlaneType type, FPLType fpl, Intersection startPoint) {
        Airplane p = new Airplane(callsign, type, fpl, startPoint);
        planelist.add(p);
        commandHandler.addAirplane(p);
        operationsCounter++;
        return p;
    }

    /**
     * Create a new outbound plane UNSPAWNED
     * @param callsign
     * @param type
     * @param fpl
     * @param startPoint
     * @return
     */
    public Airplane createNewUnspawnedDeparture(String callsign, PlaneType type, FPLType fpl, Intersection startPoint) {
        return new Airplane(callsign, type, fpl, startPoint);
    }

    /**
     * Create a new inbound plane UNSPAWNED
     * @param callsign
     * @param type
     * @param startPoint
     * @param numLandings
     * @return
     */
    public Airplane createNewUnspawnedArrival(String callsign, PlaneType type, Pattern startPoint, int numLandings) {
        return new Airplane(callsign, type, startPoint, numLandings);
    }

    public void spawn(Airplane p){
        planelist.add(p);
        commandHandler.addAirplane(p);
        operationsCounter++;
    }

    /**
     * Update and render the planes in our sim
     * @param batch Spritebatch in use
     */
    public void render(SpriteBatch batch) {
        for (Airplane p : planelist) {
            if (!simPaused)
                p.update();

            p.render(batch);

            // Unnecessary extra code
//            if (p.getTextButton().isChecked())
//                commandHandler.select(p);

            if (p.isGone()) {
//                dispose(p);
//                planelist.removeValue(p, false);
//                commandHandler.removeAirplane(p);
//                stripHandler.update();
                removePlane(p);
            }
        }
        commandHandler.update(planelist);
    }

    public void pause(){
        simPaused = true;
        System.out.println("Simulation paused.");
    }

    public void resume(){
        simPaused = false;
        System.out.println("Simulation resumed.");
    }

    public void pause(Airplane p){
        p.pause();
        System.out.println(p.getCallsign() + ": Paused.");
    }

    public void resume(Airplane p){
        p.resume();
        System.out.println(p.getCallsign() + ": Resumed.");
    }

    /**
     * Dispose of a particular airplane's textures
     * @param p Airplane
     */
    public void dispose(Airplane p){
        p.getTexture().dispose();
        p.getLine().getTexture().dispose();
    }

    /**
     * Dispose of all airplanes
     */
    public void dispose(){
        for (Airplane p : planelist) {
            dispose(p);
        }
        commandHandler.dispose();
        stripHandler.dispose();
    }

    public Array<Airplane> getPlanelist(){
        return this.planelist;
    }

    /**
     * Generates a random IFR Inbound plane on the R28R ILS Approach
     */
    public void generateRandomIFRInbound(){
        for (Airplane p : arrivalList)
            if (!planelist.contains(p, false)) {
                spawn(p);
                return;
            }
    }

    /**
     * Generates a random VFR Inbound plane at a random pattern entry point
     */
    public void generateRandomVFRInbound(){
        int index = (int)(Math.random() * (tempGAPlaneList.size - 1));

        createNewPlane(tempGAPlaneList.get(index).getCallsign(),
                tempGAPlaneList.get(index).getType(),
                getRandomUsablePatternEntry(),
                1);

        tempGAPlaneList.removeIndex(index);
    }

    /**
     * Gets a random usable pattern entry, skipping the last used pattern entry point
     * so planes do not spawn overlapping
     * @return Returns usable pattern point
     */
    private Pattern getRandomUsablePatternEntry(){
        Array<Pattern> points = new Array<>(new Pattern[]{
                PatternDrawable.PATTERN_ENTRY_ONE,
                PatternDrawable.PATTERN_ENTRY_TWO,
                PatternDrawable.PATTERN_ENTRY_THREE,
                PatternDrawable.PATTERN_ENTRY_FOUR,
                PatternDrawable.PATTERN_ENTRY_FIVE,
                Pattern.SEVEN_EAST,
                Pattern.SEVEN_WEST,
                Pattern.EIGHT_EAST
        });

        points.removeValue(lastUsedInboundStartPoint, true);
        Pattern p = points.get((int)(Math.random() * (points.size - 1)));

        lastUsedInboundStartPoint = p;

        return p;
    }

    /**
     * Generates a random commercial departure
     *
     * All commercial departures start at R28R/A
     */
    public void generateRandomCommercialOutbound(){
        for (Airplane p : departureList)
            if (!planelist.contains(p, false)) {
                spawn(p);
                return;
            }
    }

    /**
     * Generates a random General Aviation departure
     *
     * Can spawn at either 28R/A or C
     * @param isIntersectionDeparture If true, spawns at 28R/C
     */
    public void generateRandomGAOutbound(boolean isIntersectionDeparture){
        int index = (int)(Math.random() * (tempGAPlaneList.size - 1));

        if (isIntersectionDeparture){
            createNewPlane(tempGAPlaneList.get(index).getCallsign(),
                    tempGAPlaneList.get(index).getType(),
                    FPLType.VFR,
                    Intersection.R28R_HS_NORTH_C);
        } else {
            createNewPlane(tempGAPlaneList.get(index).getCallsign(),
                    tempGAPlaneList.get(index).getType(),
                    FPLType.VFR,
                    Intersection.R28R_HS_NORTH_A);
        }

        tempGAPlaneList.removeIndex(index);
    }

    /**
     * Generates a random R16 departure
     *
     * All R16 departures are GA aircraft
     * @param isIFR If true, spawns an IFR aircraft, otherwise uses VFR
     */
    public void generateRandom16Outbound(boolean isIFR){
        int index = (int)(Math.random() * (tempGAPlaneList.size - 1));
        Intersection startPoint = (Math.random() >= .5f ? Intersection.R16_HS_WEST_H : Intersection.R16_HS_EAST_H);

        if (isIFR){
            createNewPlane(tempGAPlaneList.get(index).getCallsign(),
                    tempGAPlaneList.get(index).getType(),
                    FPLType.IFR,
                    startPoint);
        } else {
            createNewPlane(tempGAPlaneList.get(index).getCallsign(),
                    tempGAPlaneList.get(index).getType(),
                    FPLType.VFR,
                    startPoint);
        }

        tempGAPlaneList.removeIndex(index);
    }

    /**
     * Removes the last plane from the simulator
     *
     * @return Returns true if successful
     */
    public boolean removeLastPlane(){
        if (planelist.size > 0) {
            Airplane p = planelist.get(planelist.size - 1);
            dispose(p);
            commandHandler.removeAirplane(p);
            planelist.removeIndex(planelist.size - 1);

            if (arrivalList.contains(p, false)) {
                arrivalList.removeValue(p, false);
                populateArrivals(1);
                stripHandler.updateArrivals();
            }
            else if (departureList.contains(p, false)) {
                departureList.removeValue(p, false);
                populateDepartures(1);
                stripHandler.updateDepartures();
            }


            return true;
        }
        return false;
    }

    public void removePlane(Airplane p){
        dispose(p);
        commandHandler.removeAirplane(p);
        planelist.removeValue(p, false);

        if (arrivalList.contains(p, false)) {
            arrivalList.removeValue(p, false);
            populateArrivals(1);
            stripHandler.updateArrivals();
        }
        else if (departureList.contains(p, false)) {
            departureList.removeValue(p, false);
            populateDepartures(1);
            stripHandler.updateDepartures();
        }
    }

    public AirplaneCommandHandler getCommandHandler(){
        return this.commandHandler;
    }

    public StripHandler getStripHandler(){ return this.stripHandler; }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 v = commandHandler.getStage().getViewport().unproject(new Vector2(screenX, screenY));
        for (Airplane p: planelist){
            if (p.contains(v.x, v.y)){
                commandHandler.select(p);
                return true;
            }
        }

        // If we click anywhere on the radar screen that isn't an aircraft, deselect any selected buttons
        if (screenX < commandHandler.getStage().getViewport().getScreenWidth() / 2)
            commandHandler.deselectAll();
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public int getOperationsCounter(){
        return this.operationsCounter;
    }

    public Array<Airplane> getArrivalList(){
        return this.arrivalList;
    }

    public Array<Airplane> getDepartureList(){
        return this.departureList;
    }
}
