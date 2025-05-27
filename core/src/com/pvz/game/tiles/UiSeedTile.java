package com.pvz.game.tiles;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.plants.Plant;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;

public class UiSeedTile extends AbstractTile implements Clickable {

    private Rectangle hitbox;
    private Plant plant;
    private Texture rechargeBarTopTexture = new Texture("recharge.png");
    private Texture rechargeBarBottomTexture = new Texture("rechargeBottom.png");
    private float rechargeProgress = 100f;
    private float rechargeMax = 0;
    private float time = 0f;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    ShaderProgram grayscaleShader = new ShaderProgram(Gdx.files.internal("default.vert"), Gdx.files.internal("grayscale.frag"));
    private boolean dark = false;
    private boolean selected = false;

    public UiSeedTile(Texture t, Vector2 tileMapPos, Vector2 worldPos) {
        super(t, tileMapPos, worldPos);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (rechargeProgress < 100f || (dark || selected)) {
            batch.setShader(grayscaleShader);
        }

        System.out.println("HERERERERE3");
        System.out.println(worldPos);
        System.out.println("Texture: " + texture);

        batch.draw(texture, worldPos.x, worldPos.y);
//        batch.draw(texture, 100, 100);
        batch.setShader(null); // Reset to default shader
    }

    @Override
    public void setTexture(Texture tex) {
        this.texture = tex;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
        rechargeMax = plant.getRecharge();
    }

    public void update(float deltaTime) {
        if (rechargeProgress < 100f) {
            time += deltaTime;
            rechargeProgress += (100f / rechargeMax) * deltaTime;
            if (rechargeProgress > 100f) rechargeProgress = 100f;
        }
        if (rechargeProgress < 0f) {
            rechargeProgress = 0;
        }
    }

    public void renderRecharge(SpriteBatch batch, float delta, Camera camera) {

        if (rechargeProgress < 100f) {

            float rechargePercentage = (hitbox.height - 7.5f) * (rechargeProgress / 100f);
            batch.end();

            // Adjust Y position to match cropped part
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Color gray = Color.valueOf("5f5f5f");
            gray.a = 0.5f;
            shapeRenderer.setColor(gray);


            // Define the four vertices of the polygon
            float x1 = hitbox.x, y1 = hitbox.y + 6.65f;  // Bottom-left
            float x2 = hitbox.x + hitbox.width, y2 = hitbox.y - 0.3f; // Bottom-right
            float x3 = hitbox.x + hitbox.width, y3 = hitbox.y + hitbox.height - 7.5f - rechargePercentage; // Top-right
            float x4 = hitbox.x, y4 = hitbox.y + hitbox.height - rechargePercentage - 0.5f; // Top-left

            // Split the quadrilateral into two triangles and draw them
            shapeRenderer.triangle(x1, y1, x2, y2, x3, y3);
            shapeRenderer.triangle(x1, y1, x3, y3, x4, y4);

            shapeRenderer.end();
            batch.begin();
            batch.draw(rechargeBarTopTexture, hitbox.x, hitbox.y - rechargePercentage - 0.35f, hitbox.width, hitbox.height); // Draw cropped region
            batch.draw(rechargeBarBottomTexture, hitbox.x, hitbox.y, hitbox.width, hitbox.height); // Draw cropped region


        }

    }

    public float getRecharge() {
        return rechargeProgress;
    }

    public void rechargeSeedPacket() {

        if (rechargeProgress >= 100f) {
            rechargeProgress = 0f; // Reset recharge when the seed is used
        }
    }

    public void setDark(boolean b) {
        dark = b;
    }

    public void deselect() {
        selected = false;
    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return hitbox.contains(mousePosition);
    }

    @Override
    public void onClick(Screen gameScreen) {
        if (rechargeProgress == 100f) {
            ((GameScreen) gameScreen).setCurrentPacket(this);
            selected = true;
            SoundManager.getInstance().play("seed_lift");
        }

    }
}