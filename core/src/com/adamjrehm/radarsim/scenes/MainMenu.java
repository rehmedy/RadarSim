package com.adamjrehm.radarsim.scenes;

import com.adamjrehm.radarsim.RadarSim;
import com.adamjrehm.radarsim.helpers.GameInfo;
import com.adamjrehm.radarsim.huds.MainMenuButtons;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenu implements Screen {

    private RadarSim radarSim;

    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

    private Texture bg;

    private MainMenuButtons btns;

    public MainMenu(RadarSim sim) {

        this.radarSim = sim;

        mainCamera = new OrthographicCamera();
        gameViewport = new FitViewport(GameInfo.VIRTUAL_WIDTH, GameInfo.VIRTUAL_HEIGHT, mainCamera);
        gameViewport.getCamera().position.set(GameInfo.VIRTUAL_WIDTH / 2f, GameInfo.VIRTUAL_HEIGHT / 2f, 0);

        bg = new Texture("images/Sim BG.png");
        bg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        btns = new MainMenuButtons(radarSim);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.apply();

        radarSim.getBatch().begin();
        radarSim.getBatch().draw(bg, 0, 0);
        radarSim.getBatch().end();

        btns.getStage().getViewport().apply();
        btns.getStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
        btns.getStage().getViewport().update(width, height);
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
        btns.dispose();
    }
}
