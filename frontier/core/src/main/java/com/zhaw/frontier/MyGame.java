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

public class MyGame extends ApplicationAdapter {
    private Engine engine;
    private SpriteBatch batch;
    private ExtendViewport extendedViewport;
    // ui
    private ScreenViewport screenViewport;
    private Skin skin;
    private Stage stage;

    @Override
    public void create() {
        batch = new SpriteBatch();
        var texture = new Texture(Gdx.files.internal("texture-long.png"));
        var textureRegion = new TextureRegion(texture);

        extendedViewport = new ExtendViewport(8, 8);
        extendedViewport.getCamera().position.set(8, 4, 0);

        engine = new Engine();

        // Add systems
        engine.addSystem(new MovementSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new RenderSystem(batch, extendedViewport, engine, textureRegion));

        // Add characters
        engine.addEntity(createCharacter("donkey.png", 1, 1, true));
        engine.addEntity(createCharacter("donkey.png", 3, 3, false));

        // create ui
        skin = new Skin(Gdx.files.internal("ui/Particle Park UI.json"));
        screenViewport = new ScreenViewport();

        var worldInputProcessor = createMouseProcessor();

        stage = new Stage(screenViewport, batch);

        var mx = new InputMultiplexer();

        mx.addProcessor(stage);
        mx.addProcessor(worldInputProcessor);

        Gdx.input.setInputProcessor(mx);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(10);
        stage.addActor(root);

        Table table = new Table();
        root.add(table).growX();

        table.defaults().space(5);
        Label label = new Label("Player Health:", skin);
        table.add(label);

        ProgressBar progressBar = new ProgressBar(0, 100, 1, false, skin);
        progressBar.setValue(50);
        table.add(progressBar).expandX().left();

        TextButton textButton = new TextButton("Menu", skin);
        table.add(textButton);

        root.row();
        table = new Table();
        table.setBackground(skin.getDrawable("highlight"));
        root.add(table).growX().expandY().bottom();

        ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<>();
        table.defaults().space(20);
        CheckBox checkBox = new CheckBox("Option1", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option2", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option3", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option4", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);

        checkBox = new CheckBox("Option5", skin, "radio");
        table.add(checkBox);
        buttonGroup.add(checkBox);
    }

    private InputProcessor createMouseProcessor() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                Gdx.app.log("test", "down");
                Gdx.app.log("x,y", new Vector2(x, y).toString());
                Gdx.app.log("world x,y", extendedViewport.unproject(new Vector2(x, y)).toString());
                Gdx.app.log("pointer", pointer + "");
                Gdx.app.log("button", button + "");
                // your touch down code here
                return true; // return true to indicate the event was handled
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                Gdx.app.log("test", "up");

                // your touch up code here
                return true; // return true to indicate the event was handled
            }
        };
    }

    private Entity createCharacter(String texturePath, float x, float y, boolean circularMovement) {
        Entity entity = new Entity();

        PositionComponent pos = new PositionComponent();
        pos.position.set(x, y);

        VelocityComponent vel = new VelocityComponent();

        if (circularMovement) {
            vel.velocity.set(0.5f, 0);
        } else {
            vel.velocity.set(1, 0);
        }

        var render = new RenderComponent();
        render.texture = new Texture(Gdx.files.internal(texturePath));

        entity.add(pos).add(vel).add(render);
        return entity;
    }

    @Override
    public void resize(int width, int height) {
        extendedViewport.update(width, height);
        screenViewport.update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.graphics.getDeltaTime();

        engine.update(Gdx.graphics.getDeltaTime());

        screenViewport.apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        skin.dispose();
        for (Entity entity : engine.getEntities()) {
            RenderComponent render = ComponentMapper.getFor(RenderComponent.class).get(entity);
            if (render != null && render.texture != null) {
                render.texture.dispose();
            }
        }
    }
}
