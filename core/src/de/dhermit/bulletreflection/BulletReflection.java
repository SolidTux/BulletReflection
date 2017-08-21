package de.dhermit.bulletreflection;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.Vector2;

public class BulletReflection extends ApplicationAdapter implements InputProcessor {
    TiledMap map;
    Sprite player;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    OrthogonalTiledMapRenderer mapRenderer;
    OrthographicCamera camera;
    int mapWidth, mapHeight;
    Vector2 dir;

    @Override
    public void create () {
        map = new TmxMapLoader().load("test.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/64f);
        camera = new OrthographicCamera(10, 10);
        mapWidth = map.getProperties().get("width",Integer.class);
        mapHeight = map.getProperties().get("height",Integer.class);

        batch = new SpriteBatch();
        player = new Sprite(new Texture(Gdx.files.internal("player.png")));
        player.setSize(1, 1);

        dir = new Vector2();
        shapeRenderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {
        float factorX = width/mapWidth;
        float factorY = height/mapHeight;
        float f;
        if (factorX > factorY) {
            f = factorY;
        } else {
            f = factorX;
        }
        camera.setToOrtho(false, width/f, height/f);
        camera.position.x = mapWidth/2;
        camera.position.y = mapWidth/2;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                dir = new Vector2(-1, 0);
                break;
            case Input.Keys.RIGHT:
                dir = new Vector2(1, 0);
                break;
            case Input.Keys.DOWN:
                dir = new Vector2(0, -1);
                break;
            case Input.Keys.UP:
                dir = new Vector2(0, 1);
                break;
            case Input.Keys.ENTER:
                dir = new Vector2();
                break;
        }
        return false;
    }

    @Override
    public void render () {
        // Clear screen.
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw map.
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Draw path.
        if (dir.len() != 0) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 0, 1);
            Vector2 playerPos = new Vector2(player.getX(), player.getY());
            playerPos.add(0.5f, 0.5f);
            Vector2 endPos = new Vector2(dir);
            endPos.scl(3);
            System.out.println(endPos);
            endPos.add(playerPos);
            shapeRenderer.line(playerPos.x, playerPos.y, endPos.x, endPos.y);
            shapeRenderer.end();
        }

        // Draw player.
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public void dispose () {
        mapRenderer.dispose();
        map.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
}
