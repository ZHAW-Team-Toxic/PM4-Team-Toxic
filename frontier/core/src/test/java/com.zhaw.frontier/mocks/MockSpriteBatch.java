package com.zhaw.frontier.mocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import java.util.ArrayList;
import java.util.List;

public class MockSpriteBatch implements SpriteBatchInterface {

    private final List<String> methodCalls = new ArrayList<>();
    private SpriteBatch batch;

    public MockSpriteBatch() {}

    public MockSpriteBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void begin() {
        methodCalls.add("begin()");
    }

    @Override
    public void end() {
        methodCalls.add("end()");
    }

    @Override
    public void draw(Texture texture, float x, float y) {
        methodCalls.add(String.format("draw(%s, %s, %s)", texture.toString(), x, y));
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        methodCalls.add(
            String.format("draw(%s, %s, %s, %s, %s)", texture.toString(), x, y, height, width)
        );
    }

    @Override
    public void dispose() {
        methodCalls.add("dispose()");
    }

    @Override
    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void setBatch(SpriteBatchInterface batch) {
        this.batch = batch.getBatch();
    }

    public List<String> getMethodCalls() {
        return methodCalls;
    }
}
