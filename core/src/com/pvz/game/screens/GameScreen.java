package com.pvz.game.screens;

import java.util.*;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pvz.game.TileMapSingleton;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.MusicManager;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.tiles.*;
import com.pvz.game.ui.*;
import com.pvz.game.plants.*;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Tilemap mapObjects;
    private int scrollSpeed = 100;
    private int zoomSpeed = 5;
    private Vector3 unprojectVector = new Vector3();
    private Vector2 worldMousePosition = new Vector2();
    private Clickable prevHover;

    private TmxMapLoader maploader;
    private TiledMap map;
    private IsometricTiledMapRenderer renderer;
    private Viewport port;

    private Map<String, Plant> plants = new HashMap<String, Plant>();
    private PlantLoader plantLoader;
    private Label sunPointsLabel;
    private UiSeedTile currentPacket = null;
    private Plant hoverPlant = null;


    private ArrayList<UiSeedTile> packets = new ArrayList<UiSeedTile>();


    private SunManager sunManager;
    private Sun hoverSun = null;
    private Sun floatSun = null;

    private int hoverMode = -1;


    private enum MouseState {NONE, HOVER, CLICKED, DRAGGING}

    ;
    private MouseState mouseState;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private Integer sunAmount = 300;

    private Ui ui;

    private Clickable hovered;
    private float skySunTimer;

    private boolean gameOver;

    private float ASPECT_RATIO = 480f / 270f;

    private boolean isPaused = false;
    private GameOverScreen gameOverScreen = new GameOverScreen();
    private boolean mouseReleased = false;


    public GameScreen(SpriteBatch batch) {

        this.batch = batch;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        port = new FitViewport(1920 / 4, 1080 / 4, camera);
        camera.position.set(170, 30, 0);
        maploader = new TmxMapLoader();
        map = maploader.load("tileset.tmx");
        renderer = new IsometricTiledMapRenderer(map);
        sunManager = new SunManager();

        mapObjects = new Tilemap(map, sunManager, camera, this);

        packets = mapObjects.getPackets();
        ui = new Ui(mapObjects.getCorner(), this);

        mouseState = MouseState.NONE;

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    mouseReleased = false;
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    mouseReleased = true;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.ESCAPE) {
                    isPaused = !isPaused;
                }
                return true;
            }
        });

        SoundManager.getInstance().play("readysetplant");
        MusicManager.getInstance().play("track2");


    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void show() {

    }

    public boolean getMouseReleased() {
        return mouseReleased;
    }

    public Integer getSunAmount() {
        return sunAmount;
    }

    public void createSunPointsCounter() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont(Gdx.files.internal("default.fnt"));

        sunPointsLabel = new Label("Sun Points: 50", style); // Start with 50 sun points
        sunPointsLabel.setPosition(10, Gdx.graphics.getHeight() - 60);
    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Keys.A))
            camera.position.x -= delta * scrollSpeed;
        if (Gdx.input.isKeyPressed(Keys.D))
            camera.position.x += delta * scrollSpeed;
        if (Gdx.input.isKeyPressed(Keys.W))
            camera.position.y += delta * scrollSpeed;
        if (Gdx.input.isKeyPressed(Keys.S))
            camera.position.y -= delta * scrollSpeed;

        if (Gdx.input.isKeyPressed(Keys.K))
            gameOver();
//            camera.zoom = Math.min(camera.zoom + zoomSpeed * delta, 8.0f);
        if (Gdx.input.isKeyPressed(Keys.L))
            camera.zoom = Math.max(camera.zoom - zoomSpeed * delta, 0.5f);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mouseState == MouseState.HOVER) {
                mouseState = MouseState.CLICKED;
            }
        }

    }

//	public void setCurrentPlant(Plant p){
//		currentPlant = p;
//	}

    public void update(float delta) {


        hovered = getHovered(); // Update hovered object

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clickTriggered();
        }

        //Click is also triggered on mouseReleased
        if (mouseReleased) {
            clickTriggered();
            mouseReleased = false;
        }
        if (hovered != null) {
            mouseState = MouseState.HOVER;
        } else {
            mouseState = MouseState.NONE; // Reset if nothing is hovered
        }

        if (!isPaused) {
            camera.update();
            renderer.setView(camera);

            mapObjects.update(delta);
            sunManager.update(delta);
            mapObjects.updateUI(delta);

            produceSkySun(delta);
        }
    }

    public void setCurrentPacket(UiSeedTile u) {
        currentPacket = u;
    }

    public Vector2 getWorldMousePosition() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        // Transform to world coordinates
        camera.unproject(unprojectVector.set(mouseX, mouseY, 0.0f));
        worldMousePosition.set(unprojectVector.x, unprojectVector.y);

        return worldMousePosition;

    }

    public Clickable getHovered() {
        // Get mouse position in screen coordinates
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        hoverPlant = null;

        // Transform to world coordinates
        camera.unproject(unprojectVector.set(mouseX, mouseY, 0.0f));
        worldMousePosition.set(unprojectVector.x, unprojectVector.y);

        if (isPaused) {
            for (SliderButton sliderButton : ui.getSlider()) {
                if (sliderButton.isHovered(worldMousePosition, this)) {
                    return sliderButton;
                }
            }
        }

        if (isPaused && ui.getMenuButton().isHovered(worldMousePosition, this)) {
            return ui.getMenuButton();
        }

        if (!isPaused && ui.getPauseButton().isHovered(worldMousePosition, this)) {
            return ui.getPauseButton();
        }

        if (isPaused) {
            return null;
        }

        WinningItem winningItem = mapObjects.getZombies().getLevel().getWinningItem();

        if(winningItem != null && winningItem.isHovered(worldMousePosition, this)){
            hovered = winningItem;
        }

        if (prevHover != null) {
            mapObjects.resetTileTexture((HoverTile) prevHover);
            prevHover = null;
        }

        for (Sun sun : sunManager.getSuns()) {
            if (sun.isHovered(worldMousePosition, this)) {
                return sun;
            }
        }

        if (ui.getShovel().isHovered(worldMousePosition, this)) {
            mouseReleased = false;
            return ui.getShovel();
        }

        for (UiSeedTile tile : packets) {
            if (tile.isHovered(worldMousePosition, this)) {
                hoverPlant = tile.getPlant();
                mouseReleased = false;
                return tile;
            }
        }

        // Tile hover
        HoverTile t = (HoverTile) mapObjects.get(worldMousePosition.x, worldMousePosition.y);
        if (t != null) {

            if (currentPacket != null && mapObjects.getPlantLayer().get(((HoverTile) t).getTilemapPos()).getPlant() == null) {
                prevHover = t;
                mapObjects.set(t, currentPacket.getPlant().getTexture());
            }
            return t;
        }

        return null;
    }

    public void clickTriggered() {

        if (isPaused && hovered instanceof SliderButton) {
            hovered.onClick(this);
        }


        if (isPaused) {
            for (SliderButton sliderButton : ui.getSlider()) {
                if (sliderButton.getSelected() && !(hovered instanceof SliderButton)) {
                    sliderButton.onClick(this);
                }
            }
        }

        if (isPaused && hovered instanceof MenuButton) {
            hovered.onClick(this);
            return;
        }

        if (!isPaused && hovered instanceof PauseButton) {
            hovered.onClick(this);
        }

        if (isPaused) {
            return;
        }

        if(hovered instanceof WinningItem){
            mapObjects.getZombies().getLevel().getWinningItem().onClick(this);
            return;
        }

        if (hovered instanceof UiSeedTile && currentPacket != null && currentPacket.getPlant() == ((UiSeedTile) hovered).getPlant()) {
            currentPacket.deselect();
            currentPacket = null;
            return;
        }
        if (currentPacket != null && !(hovered instanceof HoverTile)) {
            currentPacket.deselect();
            currentPacket = null;
            return;
        }

        if ((hovered == null || hovered instanceof UiSeedTile) && ui.getShovel().getClicked() && !mouseReleased) {
            ui.getShovel().setClicked(false);
            return;
        }

        if (hovered instanceof Sun) {
            hovered.onClick(this);
            return;
        }

        //Invalid plant
        if (hovered instanceof UiSeedTile && currentPacket == null && (sunAmount < ((UiSeedTile) hovered).getPlant().getCost() || ((UiSeedTile) hovered).getRecharge() < 100f)) {
            SoundManager.getInstance().play("buzzer");
        }

        //Select Plant
        if (hovered instanceof UiSeedTile && sunAmount >= ((UiSeedTile) hovered).getPlant().getCost()
                && !ui.getShovel().getClicked()) {
            System.out.println("Select Plant Ui");
            hovered.onClick(this);
            return;
        }

        if (hovered instanceof HoverTile && ui.getShovel().getClicked()) {
            PlantTile pt = mapObjects.getPlantLayer().get(((HoverTile) hovered).getTilemapPos());

            pt.removePlant();
            ui.getShovel().setClicked(false);
            return;
        }

        if (hovered instanceof Shovel && ((Shovel) hovered).getClicked()) {
            ((Shovel) hovered).setClicked(false);
            return;
        }
        if (hovered instanceof Shovel) {
            ((Shovel) hovered).onClick(this);
            return;
        }

        if (currentPacket != null && hovered instanceof HoverTile) {
            prevHover = hovered;
            // Only change the tile texture if there isnt a plant there
            if (TileMapSingleton.getInstance().getMap().get(((HoverTile) hovered).getTilemapPos()) == null) {
                mapObjects.set((HoverTile) prevHover, currentPacket.getPlant().getTexture());
            }

            if (prevHover != null && sunAmount >= currentPacket.getPlant().getCost() && mapObjects.getPlantLayer().get(((HoverTile) prevHover).getTilemapPos()).getPlant() == null) {
                sunAmount -= currentPacket.getPlant().getCost();
                currentPacket.rechargeSeedPacket();
                mapObjects.plant((HoverTile) prevHover, currentPacket.getPlant());
                currentPacket.setDark(true);
                currentPacket.deselect();
                currentPacket = null;
                hoverPlant = null;
                hovered = null;
                return;
            }
        }


    }


    public void setHoverPlant(Plant p) {
        hoverPlant = p;
    }

    public void setSunAmount(int sun) {
        sunAmount = sun;
    }

    public Plant getCurrentPlant() {
        return currentPacket.getPlant();
    }

    public Tilemap getMap() {
        return mapObjects;
    }

    public void produceSkySun(float delta) {
        if (skySunTimer >= 5.5f) {

            Random rand = new Random();
            int randomMapX = rand.nextInt(mapObjects.map.length);           // 0 to map.length - 1
            int randomMapY = rand.nextInt(mapObjects.map[0].length);        // 0 to map[0].length - 1

            Vector2 randomWorldPos = mapObjects.getBase().get(new Vector2(randomMapX, randomMapY)).getWorldPos();
            sunManager.addSun(new Sun(randomWorldPos, true));
            skySunTimer = 0;
            return;
        }

        skySunTimer += delta;
    }

    public void gameOver() {
        gameOver = true;
    }

    private void refreshPackets() {
        for (UiSeedTile packet : mapObjects.getPackets()) {
            packet.setDark(!(sunAmount >= packet.getPlant().getCost()));
        }
    }

    @Override
    public void render(float delta) {

        int fps = Gdx.graphics.getFramesPerSecond();
        handleInput(delta);
        refreshPackets();
        // Update cursor appearance
        if (mouseState == MouseState.NONE) {
            Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
        } else {
            Gdx.graphics.setSystemCursor(SystemCursor.Hand);
        }


        update(delta);

        if (isPaused && !gameOver) {
            ui.updateMenu(worldMousePosition);
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND); // Enable transparency
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderer.render();
        batch.begin();
        batch.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapObjects.render(batch, delta);
        ui.render(batch, delta);
        mapObjects.renderUI(batch, delta);
        sunManager.render(batch, delta);

        ui.renderShovel(batch, delta);

        if (!gameOver) {
            ui.renderMenu(batch, delta);
        }
        if (gameOver) {
            gameOverScreen.render(batch, delta, camera, port);
            pause();
        }

        batch.end();


        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
//			System.out.println("CURRENT ZOMBIES: ");
//			for(int row = 0; row < mapObjects.map.length; row++) {
//				ArrayList<Zombie> laneZombies = new ArrayList<>(mapObjects.getZombies().getLanezombies().get(row));
////		System.out.println(zombiesCopy);
//				for (Zombie zombie : laneZombies) {
//					Vector2 rect = mapObjects.getDebug();
//					rect.x = zombie.getHitbox().x;
//					rect.y = zombie.getHitbox().y;
//					shapeRenderer.rect(rect.x, rect.y, 3, 3);
//				}
//			}
//		for(Map.Entry<Integer, Mower> entry : mapObjects.getMowers().getmowers().entrySet()){
//
//			Rectangle rect = entry.getValue().getHitbox();
////			rect.x = entry.getValue().getPosition().x;
////			rect.y = entry.getValue().getPosition().y;
////			rect.add(new Vector2(TilemapOverlay.TILE_WIDTH/2, TilemapOverlay.TILE_HEIGHT/2));
//			shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//		}
//		for(Map.Entry<Vector2, TargetTile> entry: mapObjects.getTargetLayer().entrySet()){
//			TargetTile tile = entry.getValue();
//
//
//			Vector2 rect = mapObjects.getDebug();
//			rect.x = tile.getWorldPos().x;
//			rect.y = tile.getWorldPos().y;
//			shapeRenderer.rect(rect.x, rect.y, 3, 3);
//		}
//		for(Map.Entry<Vector2, AbstractTile> entry: mapObjects.getBase().entrySet()) {
//			AbstractTile tile = entry.getValue();
//
//
//			Vector2 rect = mapObjects.getDebug();
//			rect.x = tile.getWorldPos().x;
//			rect.y = tile.getWorldPos().y;
//			shapeRenderer.rect(rect.x, rect.y, 3, 3);
//		}
//		for(Map.Entry<Vector2, PlantTile> entry: mapObjects.getPlantLayer().entrySet()) {
//			PlantTile tile = entry.getValue();
//
//
//			Vector2 rect = mapObjects.getDebug();
//			rect.x = tile.getWorldPos().x;
//			rect.y = tile.getWorldPos().y;
//			shapeRenderer.rect(rect.x, rect.y, 3, 3);
//		}
//		shapeRenderer.setColor(Color.TEAL);
//		for(AbstractTile tile : new ArrayList<>(DebuggerSingleton.getInstance().getTiles())) {
//			if(tile != null) {
//				Vector2 rect = new Vector2();
//
//				rect.x = tile.getWorldPos().x;
//				rect.y = tile.getWorldPos().y;
//				shapeRenderer.rect(rect.x, rect.y, 5, 5);
//			}
//		}
//
//			for(int row = 0; row < mapObjects.map.length; row++) {
//				ArrayList<Zombie> laneZombies = new ArrayList<>(mapObjects.getZombies().getLanezombies().get(row));
//				for (Zombie zombie : laneZombies) {
//					if(zombie.getTile() != null){
//
//						Vector2 rect = new Vector2();
//
//						rect.x = zombie.getTile().getWorldPos().x;
//						rect.y = zombie.getTile().getWorldPos().y;
//						shapeRenderer.rect(rect.x, rect.y, 5, 5);
//					}
//					Vector2 rect = new Vector2();
//
//					rect.x = zombie.getHitbox().x;
//					rect.y = zombie.getHitbox().y;
//					shapeRenderer.rect(rect.x, rect.y, 5, 5);
//				}
//			}
//		for(UiSeedTile entry: mapObjects.getSeedPackets()) {
//
//
//			Vector2 rect = mapObjects.getDebug();
//			rect.x = entry.getWorldPos().x;
//			rect.y = entry.getWorldPos().y;
////			shapeRenderer.rect(rect.x, rect.y, 14, 19);
//		}
//		shapeRenderer.setColor(Color.BLUE);
//		Vector2 testVector = new Vector2(3,1);
//		shapeRenderer.rect(mapObjects.getBase().get(testVector).getWorldPos().x, mapObjects.getBase().get(testVector).getWorldPos().y, 3, 3);
////		shapeRenderer.rect(mapObjects.getCorner().x, mapObjects.getCorner().y, 3, 3);
//
//		shapeRenderer.setColor(Color.GREEN);
//		shapeRenderer.rect(mapObjects.getCorner2().x, mapObjects.getCorner2().y, 3, 3);
//
//		shapeRenderer.rect(0, 0, 3, 3);

        shapeRenderer.end();
//
//		}
    }

    public boolean getPaused() {
        return isPaused;
    }

    public void addSun(int sun) {
        sunAmount += sun;
    }


    @Override
    public void resize(int width, int height) {
        float currentRatio = (float) width / height;

        int newWidth = width;
        int newHeight = height;

        if (currentRatio > ASPECT_RATIO) {
            // Too wide, adjust width
            newWidth = (int) (height * ASPECT_RATIO);
        } else if (currentRatio < ASPECT_RATIO) {
            // Too tall, adjust height
            newHeight = (int) (width / ASPECT_RATIO);
        }

        // Apply the corrected size using a Viewport
        port.update(newWidth, newHeight);
    }


    @Override
    public void pause() {
        isPaused = true;
        MusicManager.getInstance().pause();

    }

    @Override
    public void resume() {
        isPaused = false;
        MusicManager.getInstance().resume();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        SoundManager.getInstance().dispose();
    }

}
