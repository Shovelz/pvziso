package com.pvz.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pvz.game.IsoGame;
import com.pvz.game.plants.Plant;
import com.pvz.game.ui.LevelSelect;
import com.pvz.game.ui.UnlockPlantNextButton;

import java.util.ArrayList;

public class UnlockPlantScreen implements Screen {

    private Camera camera;
    private Viewport port;
    private Texture background = new Texture("unlockPlantBackground.png");
    private SpriteBatch batch;
    private Clickable hovered;
    private Vector3 unprojectVector = new Vector3();
    private Vector2 worldMousePosition = new Vector2();
    private IsoGame game;
    private boolean levelStarting = false;
    private AssetManager assetManager;
    private BitmapFont font, fontSmall;
    private Plant plant;
    private UnlockPlantNextButton nextButton;
    private float fadeAlpha = 1f; // Start fully opaque white
    private float fadeDuration = 1.0f; // 1 second fade



    public UnlockPlantScreen(SpriteBatch batch, IsoGame game, AssetManager assetManager){
        this.assetManager = assetManager;
        this.batch = batch;
        this.game = game;
        camera = new OrthographicCamera();
        port = new FitViewport(1920, 1080, camera);
        port.apply(true); // true centers the camera
        camera.position.set(480 / 2f, 270 / 2f, 0);
        camera.update();

        font = new BitmapFont(Gdx.files.internal("fonts/houseofterror.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        font.setColor(Color.valueOf("D09740"));

        fontSmall = new BitmapFont(Gdx.files.internal("fonts/brianneshand.fnt"));
        fontSmall.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        nextButton = new UnlockPlantNextButton(new Vector2(240*4f, 25*4f));


    }


    @Override
    public void show() {
        fadeAlpha = fadeDuration;
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

        String plantName = plant.getName();

        GlyphLayout layout = new GlyphLayout(font, plantName);
        float textWidth = layout.width;
        float x = 244f*4 - textWidth / 2f;
        float y = 117f*4 + layout.height / 2f;

        font.draw(batch, plantName, x, y);

        batch.draw(plant.getTexture(), 222*4, 140*4, plant.getTexture().getWidth()*4f, plant.getTexture().getHeight()*4f);


        String plantDesc = plant.getDescription();

        float wrapWidth = 120f*4; // width of your rectangle box
        GlyphLayout wrappedLayout = new GlyphLayout();
        wrappedLayout.setText(fontSmall, plantDesc, Color.valueOf("2f2041"), wrapWidth, Align.center, true);
        float rectX = 244f*4 - wrapWidth / 2f; // center it at x=244
        float boxCenterY = 82f * 4;
        float rectY = boxCenterY + wrappedLayout.height / 2f;


        fontSmall.draw(batch, wrappedLayout, rectX, rectY);

        nextButton.render(batch);

        batch.end();

        if (fadeAlpha > 0) {
            fadeAlpha -= delta / fadeDuration;
            fadeAlpha = Math.max(fadeAlpha, 0f);

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, fadeAlpha);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            shapeRenderer.dispose();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public Clickable getHovered() {
        // Get mouse position in screen coordinates
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        // Transform to world coordinates
        camera.unproject(unprojectVector.set(mouseX, mouseY, 0.0f));
        worldMousePosition.set(unprojectVector.x, unprojectVector.y);


        if(nextButton.isHovered(worldMousePosition, this)){
            return nextButton;
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

        if (hovered instanceof UnlockPlantNextButton) {
            hovered.onClick(this);
            return;
        }
    }

    public void update(float delta) {


        hovered = getHovered(); // Update hovered object

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clickTriggered();
        }
        nextButton.setHovered(hovered != null && hovered.equals(nextButton) ? true : false);

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
    public void toLevelScreen(){
        game.backToLevelSelect();
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

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Viewport getViewport() {
        return port;
    }
}
