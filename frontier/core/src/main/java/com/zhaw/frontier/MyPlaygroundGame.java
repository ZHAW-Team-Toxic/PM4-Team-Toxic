package com.zhaw.frontier;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.systems.BoundsSystem;
import com.zhaw.frontier.systems.MovementSystem;
import com.zhaw.frontier.systems.RenderSystem;

public class MyPlaygroundGame extends ApplicationAdapter {

    private Engine engine;
    private SpriteBatch batch;
    private ExtendViewport extendedViewport;
    private ScreenViewport screenViewport;
    private Stage stage;

    @Override
    public void create () {
    }

    private void create_map(int x_length, int y_length) {

    }

    @Override
    public void resize (int width, int height) {
    }

    @Override
    public void render () {
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
    }


}
