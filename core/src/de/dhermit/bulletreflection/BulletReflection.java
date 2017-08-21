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
    TiledMapTileLayer walls, background;
    Vector2 dir, newPosition;

    @Override
    public void create () {
        map = new TmxMapLoader().load("test.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/64f);
        camera = new OrthographicCamera(10, 10);
        mapWidth = map.getProperties().get("width",Integer.class);
        mapHeight = map.getProperties().get("height",Integer.class);
        walls = (TiledMapTileLayer) map.getLayers().get("walls");
        background = (TiledMapTileLayer) map.getLayers().get("background");

        batch = new SpriteBatch();
        player = new Sprite(new Texture(Gdx.files.internal("player.png")));
        player.setSize(1, 1);

        dir = new Vector2();
        newPosition = new Vector2();
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
                player.setPosition(newPosition.x, newPosition.y);
                newPosition = new Vector2();
                break;
            case Input.Keys.ESCAPE:
                dir = new Vector2();
                player.setPosition(0, 0);
                break;
        }
        return false;
    }

    private Vector2 bounceBullet(Vector2 start, Vector2 dir, int num) {
        Vector2 direction = new Vector2(dir);
        direction.nor();
        Vector2 startPosition = new Vector2(start);
        Vector2 position = new Vector2(start);
        TiledMapTileLayer.Cell cell = walls.getCell((int) position.x, (int) position.y);
        int reflCount = 0;
        int cellId = 1;
        boolean reflected = false;

        if (cell != null) {
            cellId = walls.getCell((int) position.x, (int) position.y).getTile().getId();
        }
        while (reflCount < num) {
            position.add(direction);
            if (position.x < 0) {
                direction.x = -direction.x;
                position.x = 0;
                reflected = true;
            }
            if (position.x > mapWidth) {
                direction.x = -direction.x;
                position.x = mapWidth - 1;
                reflected = true;
            }
            if (position.y < 0) {
                direction.y = -direction.y;
                position.y = 0;
                reflected = true;
            }
            if (position.y > mapHeight) {
                direction.y = -direction.y;
                position.y = mapHeight - 1;
                reflected = true;
            }
            cell = walls.getCell((int) position.x, (int) position.y);
            if (cell != null) {
                cellId = walls.getCell((int) position.x, (int) position.y).getTile().getId();
            } else {
                cellId = 1;
            }
            switch (cellId) {
                case 3:
                    direction.rotate90(1);
                    direction.y = -direction.y;
                    reflected = true;
                    break;
                case 4:
                    direction.rotate90(1);
                    direction.x = -direction.x;
                    reflected = true;
                    break;
            }
            if (reflected) {
                reflected = false;
                reflCount++;
                shapeRenderer.line(startPosition.x + 0.5f, startPosition.y + 0.5f,
                    position.x + 0.5f, position.y + 0.5f);
                startPosition = new Vector2(position);
            }
        }
        return position;
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
        if (!dir.isZero()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 0, 1);
            Vector2 playerPos = new Vector2(player.getX(), player.getY());
            newPosition = bounceBullet(playerPos, dir, 5);
            shapeRenderer.end();
        }

        // Draw player.
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();

        // Test if game is won.
        TiledMapTileLayer.Cell current = background.getCell((int) player.getX(), (int) player.getY());
        if (current != null) {
            System.out.println(current.getTile().getId());
            if (current.getTile().getId() == 6) {
                Gdx.app.exit();
            }
        }
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
