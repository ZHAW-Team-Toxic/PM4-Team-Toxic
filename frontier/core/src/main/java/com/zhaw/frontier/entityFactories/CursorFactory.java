package com.zhaw.frontier.entityFactories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class CursorFactory {
    public static Cursor createDefaultCursor() {
        var pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(0x181818FF);
        pixmap.fill();

        var cursor = Gdx.graphics.newCursor(pixmap, 8, 8);

        pixmap.dispose();
        return cursor;
    }

    public static Cursor createDeleteCursor() {
        var pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(0xFF0000FF);
        pixmap.fill();

        var cursor = Gdx.graphics.newCursor(pixmap, 8, 8);

        pixmap.dispose();
        return cursor;
    }
}
