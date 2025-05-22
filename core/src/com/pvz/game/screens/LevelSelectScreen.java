package com.pvz.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pvz.game.IsoGame;
import com.pvz.game.ui.LevelSelect;

import java.util.ArrayList;

public class LevelSelectScreen implements Screen {

    private Camera camera;
    private Viewport port;
    private Texture background = new Texture("levelSelectBackground.png");
    private SpriteBatch batch;
    private Clickable hovered;
    private Vector3 unprojectVector = new Vector3();
    private Vector2 worldMousePosition = new Vector2();
    private IsoGame game;
    private ArrayList<LevelSelect> levels = new ArrayList<>();
    private boolean levelStarting = false;
    private AssetManager assetManager;

    public LevelSelectScreen(SpriteBatch batch, IsoGame game, AssetManager assetManager){
        this.assetManager = assetManager;
        this.batch = batch;
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        port = new FitViewport(1920 / 4f, 1080 / 4f, camera);
        port.apply();
        // Center the camera in the viewport
        camera.position.set(port.getWorldWidth() / 2f, port.getWorldHeight() / 2f, 0);
        camera.update();

        loadLevelBoxes();
    }

    private void loadLevelBoxes(){
        for(int i = 1; i < 3; i++) {
            levels.add(new LevelSelect(new Vector2(100 + (i*70), 100), i, assetManager));
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND); // Enable transparency
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        update(delta);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, port.getWorldWidth(), port.getWorldHeight());
        for(LevelSelect level : levels){
            level.render(batch, delta);
        }
        batch.end();
    }

    public Clickable getHovered() {
        // Get mouse position in screen coordinates
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        // Transform to world coordinates
        camera.unproject(unprojectVector.set(mouseX, mouseY, 0.0f));
        worldMousePosition.set(unprojectVector.x, unprojectVector.y);

        for(LevelSelect level : levels){
            if( level.isHovered(worldMousePosition, this)) {
                return level;
            }
        }

        return null;
    }

    public void startLevel(int level){
        game.startGame(level);
    }

    public void reset(){
        levelStarting = false;
    }

    public void clickTriggered() {

        if (hovered instanceof LevelSelect && !levelStarting) {
            hovered.onClick(this);
            levelStarting = true;
            return;
        }
    }

    public void update(float delta) {


        hovered = getHovered(); // Update hovered object

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clickTriggered();
        }

        camera.update();
    }

    public Vector2 getWorldMousePosition() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        // Transform to world coordinates
        camera.unproject(unprojectVector.set(mouseX, mouseY, 0.0f));
        worldMousePosition.set(unprojectVector.x, unprojectVector.y);

        return worldMousePosition;

    }

    @Override
    public void resize(int width, int height) {
        port.update(width, height, true); // Updates viewport and re-centers camera
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Viewport getViewport() {
        return port;
    }
}
