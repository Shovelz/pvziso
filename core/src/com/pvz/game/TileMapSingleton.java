package com.pvz.game;

import com.badlogic.gdx.math.Vector2;
import com.pvz.game.tiles.AbstractTile;

import java.util.HashMap;
import java.util.Map;

public final class TileMapSingleton{

    private static TileMapSingleton INSTANCE;

    private Map<Vector2, AbstractTile> map = new HashMap<Vector2, AbstractTile>();

    private TileMapSingleton() {
    }

    public static TileMapSingleton getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TileMapSingleton();
        }

        return INSTANCE;
    }

    public void setMap(Map<Vector2, AbstractTile> baseMap){
        map = baseMap;
    }

    public Map<Vector2, AbstractTile> getMap(){
        return map;
    }
}
