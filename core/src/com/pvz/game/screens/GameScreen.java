package com.pvz.game.screens;

import java.util.*;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pvz.game.IsoGame;
import com.pvz.game.TileMapSingleton;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.MusicManager;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.levels.*;
import com.pvz.game.tiles.*;
import com.pvz.game.ui.*;
import com.pvz.game.plants.*;
import com.pvz.game.zombies.ZombieManager;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private AssetManager assetManager;
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


    public enum MouseState {NONE, HOVER, CLICKED, DRAGGING}

    private MouseState mouseState;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private Integer sunAmount = 50;

    private Ui ui;

    private Clickable hovered;
    private float skySunTimer;

    private boolean gameOver;

    private float ASPECT_RATIO = 480f / 270f;

    private boolean isPaused = false;
    private GameOverScreen gameOverScreen = new GameOverScreen();
    private boolean mouseReleased = false;

    private HashMap<Integer, Level> levels = new HashMap<>();
    private boolean gameStarted = false;
    private IsoGame isoGame;
    private Level currentLevel;
    private RadialWhiteOutTransition radialWhiteOutTransition;
    private boolean hidden = false;


    public GameScreen(SpriteBatch batch, IsoGame isoGame, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.batch = batch;
        this.isoGame = isoGame;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        port = new FitViewport(1920 / 4, 1080 / 4, camera);
        camera.position.set(170, 30, 0);
        maploader = new TmxMapLoader();
        map = maploader.load("tileset.tmx");
        renderer = new IsometricTiledMapRenderer(map);
        sunManager = new SunManager();

        mapObjects = new Tilemap(map, sunManager, camera, this, assetManager);

        packets = mapObjects.getPackets();
        ui = new Ui(mapObjects.getCorner(), this, assetManager);

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


    }

    public void setLevel(int level) {
        System.out.println("Set level" + level);
        Level newLevel = getLevel(level);
        if (newLevel != null) {
            mapObjects.getZombies().setLevel(newLevel);
            currentLevel = newLevel;
            System.out.println("Set level to " + level);
        }
    }

    public Level getCurrentLevel(){
        return currentLevel;
    }

    public void startGame() {
        gameStarted = true;
        // Add debug message to verify game is starting
        System.out.println("Starting game with zombies...");
        mapObjects.getZombies().startSpawning();
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void show() {
        removeWhiteOut();
        hidden = false;

        SoundManager.getInstance().play("readysetplant");
        MusicManager.getInstance().play("track2", true);
    }

    public boolean getMouseReleased() {
        return mouseReleased;
    }

    public Integer getSunAmount() {
        return sunAmount;
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
//            gameOver();
            camera.zoom = Math.min(camera.zoom + zoomSpeed * delta, 8.0f);
        if (Gdx.input.isKeyPressed(Keys.L))
            camera.zoom = Math.max(camera.zoom - zoomSpeed * delta, 0.5f);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mouseState == MouseState.HOVER) {
                mouseState = MouseState.CLICKED;
            }
        }

    }


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

        if(radialWhiteOutTransition != null){
            radialWhiteOutTransition.update(delta);
        }
    }

    public void reset() {
        gameStarted = false;
        sunAmount = currentLevel.getStartingSun();
        hovered = null;
        hoverPlant = null;
        isPaused = false;
        mouseState = MouseState.NONE;
        sunManager.removeAllSun();
        mapObjects.reset();
    }

    public void loadLevels() {
        ZombieManager zm = mapObjects.getZombies();
        levels.put(1, new Level1(zm, assetManager));
        levels.put(2, new Level2(zm, assetManager));
        levels.put(3, new Level3(zm, assetManager));
        levels.put(4, new Level4(zm, assetManager));
    }

    public Level getLevel(int level) {
        return levels.get(level);
    }


    public void startLevel(int level) {
        try {
            mapObjects.getZombies().setLevel((Level) Class.forName("Level" + level).newInstance());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
        if (isPaused && ui.getBackToLevelsButton().isHovered(worldMousePosition, this)) {
            return ui.getBackToLevelsButton();
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

        WinningItem winningItem = null;
        if (mapObjects.getZombies().getLevel() != null) {
            winningItem = mapObjects.getZombies().getLevel().getWinningItem();
        }

        if (winningItem != null && winningItem.isHovered(worldMousePosition, this)) {
            return winningItem;
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

        if (hovered instanceof WinningItem) {
            mapObjects.getZombies().getLevel().getWinningItem().onClick(this);
            return;
        }
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


        if (isPaused && (hovered instanceof MenuButton || hovered instanceof BackToLevelSelectButton)) {
            hovered.onClick(this);
            return;
        }

        if (!isPaused && hovered instanceof PauseButton) {
            hovered.onClick(this);
        }

        if (isPaused) {
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

    public void removeWhiteOut(){
        radialWhiteOutTransition = null;
    }

    public void whiteOutTransitionStart(WinningItem winningItem){
        if(radialWhiteOutTransition != null){
            return;
        }
        MusicManager.getInstance().pause();
        SoundManager.getInstance().play("win_music");
        radialWhiteOutTransition = new RadialWhiteOutTransition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 3f);
        radialWhiteOutTransition.start(worldMousePosition.x, worldMousePosition.y,() -> finishGame(winningItem));
    }

    public void toLevelScreen(){
        isoGame.backToLevelSelect();
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND); // Enable transparency
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (!gameStarted || hidden) {
            return;
        }

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
        renderer.render();

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

        if(radialWhiteOutTransition != null){
            radialWhiteOutTransition.render(batch);
        }
        batch.end();
    }

    public Viewport getViewport(){
        return port;
    }
    public boolean getPaused() {
        return isPaused;
    }

    public void addSun(int sun) {
        sunAmount += sun;
    }

    public void finishGame(WinningItem item) {
        mapObjects.addUnlockedPlant(item.getPlant());
        isoGame.unlockPlant(item.getPlant());
        isoGame.addCompletedLevel(currentLevel);
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
        if (MusicManager.getInstance() != null && MusicManager.getInstance().getCurrentlyPlaying() != null) {
            MusicManager.getInstance().pause();
        }

    }

    @Override
    public void resume() {
        isPaused = false;
        if (MusicManager.getInstance() != null && MusicManager.getInstance().getCurrentlyPlaying() != null) {
            MusicManager.getInstance().resume();
        }
    }

    @Override
    public void hide() {
        ScreenUtils.clear(1, 1, 1, 1, true);
        hidden = true;

    }

    @Override
    public void dispose() {
        SoundManager.getInstance().dispose();
    }

}
