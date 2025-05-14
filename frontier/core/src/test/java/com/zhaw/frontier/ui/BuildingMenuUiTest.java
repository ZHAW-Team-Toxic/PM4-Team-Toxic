package com.zhaw.frontier.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.ButtonClickObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

@ExtendWith(GdxExtension.class)
public class BuildingMenuUiTest {

    private Engine engine;
    private Stage stage;
    private Viewport viewport;

    @Mock
    private Skin skin;

    @Mock
    private TextureAtlas atlas;

    @Mock
    private TextureRegionDrawable drawable;

    @Mock
    private ButtonClickObserver observer;

    private BuildingMenuUi menuUi;

    @BeforeEach
    void setUp() {
        engine = mock(Engine.class);
        stage = mock(Stage.class);
        viewport = mock(Viewport.class);
        when(stage.getViewport()).thenReturn(viewport);
        when(viewport.getWorldWidth()).thenReturn(800f);

        // Mock AssetManagerInstance
        AssetManagerInstance.getManager().load("packed/textures.atlas", TextureAtlas.class);
        AssetManagerInstance.getManager().load("skins/skin.json", Skin.class);
        AssetManagerInstance.getManager().finishLoading();

        menuUi = new BuildingMenuUi(engine, stage);
    }

    @Test
    void testInitiallyInvisible() {
        assertFalse(menuUi.isVisible(), "Menu should start hidden");
    }

    @Test
    void testShowMakesMenuVisible() {
        menuUi.show();
        assertTrue(menuUi.isVisible());
    }

    @Test
    void testHideMakesMenuInvisible() {
        menuUi.show();
        menuUi.hide();
        assertFalse(menuUi.isVisible());
    }

    @Test
    void testAllExpectedButtonsAreCreated() {
        // There should be 2 tower buttons, 3 resource buttons, and 3 wall buttons
        int expectedButtonCount = 2 + 3 + 3;
        assertEquals(
            expectedButtonCount,
            menuUi.getTotalButtonCount(),
            "All building buttons should be present"
        );
    }
}
