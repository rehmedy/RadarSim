package com.adamjrehm.radarsim.scenes;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.geography.PatternDrawable;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.adamjrehm.radarsim.huds.GameplayUI;
import com.adamjrehm.radarsim.planes.PlaneController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Gameplay implements Screen {

    private InputMultiplexer multiplexer;
    private OrthographicCamera camera;
    private Viewport viewport;

    private RadarSim sim;

    private GameplayUI ui;

    private PlaneController planeController;

    private Texture bg, radarimg;

    private float d = 0, pauseTime = 0;

    private final long START_TIME_MILLIS = System.currentTimeMillis();

    public Gameplay(RadarSim sim) {
        this.sim = sim;

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameInfo.VIRTUAL_WIDTH, GameInfo.VIRTUAL_HEIGHT, camera);
        this.viewport.getCamera().position.set(GameInfo.VIRTUAL_WIDTH / 2f, GameInfo.VIRTUAL_HEIGHT / 2f, 0);

        bg = new Texture(GameInfo.SIM_BACKGROUND_IMAGE_PATH);
        radarimg = new Texture(GameInfo.RADAR_IMAGE_PATH);
        radarimg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        planeController = new PlaneController(this);

        ui = new GameplayUI(this);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(planeController);
        multiplexer.addProcessor(ui.getStage());

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

        ui.render();

        // Statistics
        d += Gdx.graphics.getDeltaTime();
        if (d >= 1) {
            System.out.println(Gdx.graphics.getFramesPerSecond() + " FPS // " + getTotalTimeElapsed() + " elapsed // " + planeController.getOperationsCounter() + " operations completed");
            d = 0;
        }

        if (planeController.isPaused()) {
            pauseTime += Gdx.graphics.getDeltaTime();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        ui.resize(width, height);
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
        ui.dispose();
    }

    public PlaneController getPlaneController() {
        return this.planeController;
    }

    public RadarSim getSim() {
        return this.sim;
    }

    public GameplayUI getUI() {
        return this.ui;
    }

    public Viewport getViewport() {
        return this.viewport;
    }

    public String getTotalTimeElapsed() {
        long time = (System.currentTimeMillis() - START_TIME_MILLIS) / 1000;
        return time / 60 + ":" + ((time % 60) < 10 ? "0" + time % 60 : time % 60);
    }

    public String getRunningTime() {
        double t = ((System.currentTimeMillis() - START_TIME_MILLIS) / 1000d) - pauseTime;
        int time = (int) t;
        return time / 60 + ":" + ((time % 60) < 10 ? "0" + time % 60 : time % 60);
    }
}
