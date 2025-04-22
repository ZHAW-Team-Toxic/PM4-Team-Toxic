package com.zhaw.frontier.entityFactories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.zhaw.frontier.utils.AssetManagerInstance;

public class CursorFactory {

    private static Cursor defaultCursor = null;
    private static Cursor deleteCursor = null;
    private static Cursor buildingCursor = null;

    private static Pixmap spriteToPixmap(Sprite texture) {
        TextureData textureData = texture.getTexture().getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }
        Pixmap pixmap = new Pixmap(
            texture.getRegionWidth(),
            texture.getRegionHeight(),
            textureData.getFormat()
        );
        pixmap.drawPixmap(
            textureData.consumePixmap(), // The other Pixmap
            0, // The target x-coordinate (top left corner)
            0, // The target y-coordinate (top left corner)
            texture.getRegionX(), // The source x-coordinate (top left corner)
            texture.getRegionY(), // The source y-coordinate (top left corner)
            texture.getRegionWidth(), // The width of the area from the other Pixmap in pixels
            texture.getRegionHeight() // The height of the area from the other Pixmap in pixels
        );
        return pixmap;
    }

    public static Cursor createDefaultCursor() {
        if (defaultCursor != null) {
            return defaultCursor;
        }
        var atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
        var texture = new Sprite(atlas.findRegion("cursor/medivial_cursor"));
        var pixmap = spriteToPixmap(texture);
        var cursor = Gdx.graphics.newCursor(pixmap, 8, 8);

        pixmap.dispose();
        defaultCursor = cursor;
        return cursor;
    }

    public static Cursor createDeleteCursor() {
        if (deleteCursor != null) {
            return deleteCursor;
        }
        var atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
        var texture = new Sprite(atlas.findRegion("cursor/medivial_cursor_demolish"));
        texture.setSize(32, 32);
        var pixmap = spriteToPixmap(texture);
        var cursor = Gdx.graphics.newCursor(pixmap, 8, 8);

        pixmap.dispose();
        deleteCursor = cursor;
        return cursor;
    }

    public static Cursor createBuildingCursor() {
        if (buildingCursor != null) {
            return buildingCursor;
        }
        var atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
        var texture = new Sprite(atlas.findRegion("cursor/medivial_cursor_building"));
        texture.setSize(32, 32);
        var pixmap = spriteToPixmap(texture);
        var cursor = Gdx.graphics.newCursor(pixmap, 8, 8);

        pixmap.dispose();
        buildingCursor = cursor;
        return cursor;
    }
}
