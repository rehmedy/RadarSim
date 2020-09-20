package com.adamjrehm.radarsim.scenes;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.config.Configuration;
import com.adamjrehm.radarsim.geography.PatternDrawable;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.adamjrehm.radarsim.huds.SimInfo;
import com.adamjrehm.radarsim.planes.PlaneController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

public class Gameplay implements Screen {

    private InputMultiplexer multiplexer;
    private OrthographicCamera camera;
    private Viewport viewport;

    private RadarSim sim;

    private PlaneController planeController;

    private SimInfo simInfo;

    private Texture bg, radarimg;

    private float deltaTotal = 0, delt = 0;
    private int frameCounter = 0;
    private String timeElapsed;

    public Gameplay(RadarSim sim) {
        this.sim = sim;

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameInfo.VIRTUAL_WIDTH, GameInfo.VIRTUAL_HEIGHT, camera);
        this.viewport.getCamera().position.set(GameInfo.VIRTUAL_WIDTH / 2f, GameInfo.VIRTUAL_HEIGHT / 2f, 0);

        bg = new Texture(GameInfo.SIM_BACKGROUND_IMAGE_PATH);
        radarimg = new Texture(GameInfo.RADAR_IMAGE_PATH);
        radarimg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        planeController = new PlaneController(sim);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(planeController);
        multiplexer.addProcessor(planeController.getCommandHandler().getStage());

        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        sim.getBatch().setProjectionMatrix(viewport.getCamera().combined);

        sim.getBatch().begin();
        sim.getBatch().draw(radarimg, 0, 0);
        PatternDrawable.drawPatternPoints(sim.getBatch());
        planeController.render(sim.getBatch());
        sim.getBatch().end();

        planeController.getCommandHandler().render();
        planeController.getStripHandler().render();
        //simInfo.render();


        // Printing FPS
        deltaTotal += Gdx.graphics.getDeltaTime();
        delt += Gdx.graphics.getDeltaTime();
        frameCounter += 1;
        timeElapsed = (int)deltaTotal / 60 + ":" + (((int)deltaTotal % 60) < 10 ? "0" + (int)deltaTotal % 60 : (int)deltaTotal % 60);

        if (delt >= 1) {
            System.out.println(frameCounter + " FPS // " + timeElapsed + " elapsed // " + planeController.getOperationsCounter() + " operations completed");
            frameCounter = 0;
            delt = 0;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        planeController.getCommandHandler().resize(width, height);
        planeController.getStripHandler().resize(width, height);
        //planeController.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        bg.dispose();
        radarimg.dispose();
        planeController.dispose();
        PatternDrawable.dispose();
    }
}
