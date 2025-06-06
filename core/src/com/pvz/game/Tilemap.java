package com.pvz.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.maps.tiled.TiledMap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.plants.*;
import com.pvz.game.screens.GameScreen;
import com.pvz.game.tiles.*;
import com.pvz.game.ui.SunManager;
import com.pvz.game.zombies.ZombieManager;
import com.pvz.game.mower.Mower;
import com.pvz.game.mower.MowerManager;

public class Tilemap {

    private Map<Vector2, PlantTile> plants = new HashMap<Vector2, PlantTile>();
    private Map<Vector2, TargetTile> targets = new HashMap<Vector2, TargetTile>();

    private Texture grass;
    private Texture houseBG;
    private Texture uiBG;
    private Texture uiSeeds;

    private TiledMap isoMap;

    private BackgroundTile background;
    private ArrayList<UiSeedTile> seedPackets;
    private ArrayList<Rectangle> packetHitboxes;

    private SunManager sunManager;
    private Camera camera;
    private AssetManager assetManager;

    public static final String[][] map = {{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
            {"0", "0", "0", "0", "0", "0", "0", "0", "0"}, {"0", "0", "0", "0", "0", "0", "0", "0", "0"},
            {"0", "0", "0", "0", "0", "0", "0", "0", "0"}, {"0", "0", "0", "0", "0", "0", "0", "0", "0"},
            {"0", "0", "0", "0", "0", "0", "0", "0", "0"},};

    private ArrayList<Plant> plantsStatic = new ArrayList<Plant>();
    private ArrayList<Plant> unlockedPlants = new ArrayList<Plant>();

    public static final float TILE_WIDTH = 24;
    public static final float TILE_HEIGHT = 12;

    // This is really horrible, but since im using an overlayed image these offsets
    // have to exist

    public static Vector2 horribleBackgroundOffset = new Vector2(-TILE_WIDTH * 14 + TILE_WIDTH / 2 + 1, -TILE_HEIGHT * 3 + TILE_HEIGHT / 4 - 1);

    private MowerManager mowers;
    private Vector2 corner = new Vector2(0, 0);
    private ZombieManager zombies;

    private GameScreen screen;

    public Tilemap(TiledMap iso, SunManager sm, Camera c, GameScreen s, AssetManager assetManager) {
        this.isoMap = iso;
        this.assetManager = assetManager;
        this.camera = c;
        this.screen = s;
        this.sunManager = sm;

        regenerateCornerPos();

        seedPackets = new ArrayList<UiSeedTile>();
        packetHitboxes = new ArrayList<Rectangle>();


        loadPlants();
        loadAssets();

        unlockedPlants.add(plantsStatic.get(0));

        fillMap();

        grass = assetManager.get("grass.png", Texture.class);
        grass.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        houseBG = assetManager.get("background6x9.png", Texture.class);
        uiBG = assetManager.get("ui.png", Texture.class);
        uiSeeds = assetManager.get("seed_packet.png", Texture.class);

    }

    private void loadUnlockedPlantsIntoPackets() {

    }

    public void regenerateCornerPos() {

        corner.x = isoMap.getLayers().get(0).getOffsetX();
        corner.y = isoMap.getLayers().get(0).getOffsetY();
    }

    private void loadAssets() {
        assetManager.load("grass.png", Texture.class);
        assetManager.load("background6x9.png", Texture.class);
        assetManager.load("ui.png", Texture.class);
        assetManager.load("seed_packet.png", Texture.class);

        // Add packets for plants
        for (Plant p : getPlantStatic()) {
            System.out.println("Loaded plant texture" + p);
            assetManager.load("packets/" + p.getName() + ".png", Texture.class);
        }

        assetManager.finishLoading();
    }


    public GameScreen getScreen() {
        return screen;
    }

    private void loadMowers() {
        Map<Vector2, AbstractTile> base = new HashMap<>(TileMapSingleton.getInstance().getMap());
        for (int row = 0; row < map.length; row++) {
            float x = base.get(new Vector2(row, map[0].length - 1)).getWorldPos().x;
            float y = base.get(new Vector2(row, map[0].length - 1)).getWorldPos().y;
            float endX = base.get(new Vector2(row, 0)).getWorldPos().x + TILE_WIDTH;
            float endY = base.get(new Vector2(row, 0)).getWorldPos().y;
            Mower mower = new Mower(x, y, endX, endY, row, assetManager);
            mowers.addMower(row, mower);
            mower.addMowerManager(mowers);
        }
    }

    private void loadTargetTiles() {
        int col = map[0].length - 1;
        Map<Vector2, AbstractTile> base = new HashMap<>(TileMapSingleton.getInstance().getMap());
        for (int row = 0; row < map.length; row++) {
            float x = base.get(new Vector2(row, col)).getWorldPos().x - TILE_WIDTH / 2;
            float y = base.get(new Vector2(row, col)).getWorldPos().y + TILE_HEIGHT * 2;
            targets.put(new Vector2(row, col), new TargetTile(new Vector2(row, col), new Vector2(x, y)));
        }
    }

    public ZombieManager getZombies() {
        return zombies;
    }

    public Map<Vector2, AbstractTile> getBase() {
        return TileMapSingleton.getInstance().getMap();
    }

    public ArrayList<UiSeedTile> getSeedPackets() {
        return seedPackets;
    }

    private void loadPlants() {
        plantsStatic.add(new Peashooter());
        plantsStatic.add(new Sunflower());
        plantsStatic.add(new CherryBomb());
        plantsStatic.add(new Walnut());
    }

    public void update(float delta) {

        mowers.update(delta);
        for (Map.Entry<Vector2, PlantTile> entry : plants.entrySet()) {
            entry.getValue().update(delta);
        }

        zombies.update(delta);

    }

    public void render(SpriteBatch batch, float delta) {

        background.render(batch);

        mowers.render(batch, delta);

        for (Map.Entry<Vector2, PlantTile> entry : plants.entrySet()) {
            entry.getValue().render(batch, delta);
        }

        for (Map.Entry<Vector2, AbstractTile> entry : TileMapSingleton.getInstance().getMap().entrySet()) {
            entry.getValue().render(batch);
        }

        zombies.render(batch, delta);

    }

    public void updateUI(float delta) {

        for (UiSeedTile tile : seedPackets) {
            tile.update(delta);
        }
    }

    public void renderUI(SpriteBatch batch, float delta) {
        for (UiSeedTile tile : seedPackets) {
            tile.render(batch);
            tile.renderRecharge(batch, delta, camera);
        }
    }

    public Vector2 getCorner() {
        return corner;
    }

    public void initPackets() {

//        corner = baseCorner.add(new Vector2(map[0].length * TILE_WIDTH, -((map[0].length - 1) * TILE_HEIGHT)));
        for (int i = 0; i < unlockedPlants.size(); i++) {
            Vector2 offset = new Vector2(-29 + i * 18, 203 - i * 9);
            String texturePath = "packets/" + unlockedPlants.get(i).getName() + ".png";
            Texture packetTex = assetManager.get(texturePath, Texture.class);
            UiSeedTile tile = new UiSeedTile(packetTex, new Vector2(0, 0),
                    new Vector2(corner.x + offset.x, corner.y + offset.y));
            tile.setHitbox(new Rectangle(corner.x + offset.x, corner.y + offset.y, 14, 24));
            tile.setPlant(unlockedPlants.get(i));
            seedPackets.add(tile);
        }
    }

    public void fillMap() {
        corner = (new Vector2(map[0].length * TILE_WIDTH, -((map[0].length - 1) * TILE_HEIGHT)));
        initPackets();
        background = new BackgroundTile(houseBG, new Vector2(0, 0),
                new Vector2(corner.x + horribleBackgroundOffset.x, corner.y + horribleBackgroundOffset.y));

        Map<Vector2, AbstractTile> base = new HashMap<Vector2, AbstractTile>();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                float x = corner.x + ((row - col) * TILE_WIDTH) - TILE_WIDTH;
                float y = corner.y + ((col + row) * TILE_HEIGHT) + TILE_HEIGHT;
                base.put(new Vector2(row, col), new HoverTile(new Vector2(row, col), new Vector2(x, y)));
            }
        }

        TileMapSingleton.getInstance().setMap(base);

        if (zombies == null) {
            zombies = new ZombieManager(this);
        }

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                float x = corner.x + ((row - col) * TILE_WIDTH) - TILE_WIDTH;
                float y = corner.y + ((col + row) * TILE_HEIGHT) + TILE_HEIGHT;
                plants.put(new Vector2(row, col),
                        new PlantTile(null, new Vector2(row, col), new Vector2(x, y), base, sunManager, zombies));
            }
        }
        if (mowers == null) {
            mowers = new MowerManager(zombies);
        }
        loadMowers();
        loadTargetTiles();
    }

    public ArrayList<UiSeedTile> getPackets() {
        return this.seedPackets;
    }

    public AbstractTile get(float mouseX, float mouseY) {
        // Convert screen coordinates to isometric map coordinates
        float isoX = mouseX - corner.x;
        float isoY = mouseY - corner.y;

        // Convert to tile coordinates
        int col = (int) Math.floor((-isoX / TILE_WIDTH + isoY / TILE_HEIGHT) / 2);
        int row = (int) Math.floor((isoY / TILE_HEIGHT + isoX / TILE_WIDTH) / 2);

        Vector2 tileKey = new Vector2(row, col);

        Map<Vector2, AbstractTile> base = new HashMap<>(TileMapSingleton.getInstance().getMap());

//		/ Check if base tile exists first
        if (base.containsKey(tileKey)) {
            return base.get(tileKey); // Always allow hover over HoverTile
        }

        // Then check for plants
        if (plants.containsKey(tileKey) && plants.get(tileKey).getPlant() != null) {
            return plants.get(tileKey);
        }

        return null; // No tile found
    }

    public void plant(AbstractTile target, Plant p) {

        PlantTile tile = plants.get(target.getTilemapPos());
        Plant plant;
        try {
            plant = p.cloneWithTile(tile);
            tile.setTexture(plant.getTexture());
            tile.setAnimation(plant.getAnimation(), plant.getAnimationFrames());
            tile.setPlant(plant);
            SoundManager.getInstance().play("plant");
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void set(AbstractTile target, Texture sprite) {
        TileMapSingleton.getInstance().getMap().get(target.getTilemapPos()).setTexture(sprite);
    }

    public void resetTileTexture(AbstractTile target) {
        TileMapSingleton.getInstance().getMap().get(target.getTilemapPos()).setTexture(null);
    }

    public Map<Vector2, PlantTile> getPlantLayer() {
        return this.plants;
    }

    public MowerManager getMowers() {
        return mowers;
    }

    public Map<Vector2, TargetTile> getTargetLayer() {
        return targets;
    }

    public ArrayList<Plant> getPlantStatic() {
        return plantsStatic;
    }

    public void addUnlockedPlant(Plant plant) {
        unlockedPlants.add(plant);
    }

    public void reset() {
        seedPackets.clear();
        plantsStatic.clear();
        regenerateCornerPos();

        loadUnlockedPlantsIntoPackets();
        fillMap();
        loadMowers();
        zombies.reset();
    }


}
