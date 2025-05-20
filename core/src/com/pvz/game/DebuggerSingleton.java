package com.pvz.game;

import com.pvz.game.tiles.AbstractTile;

import java.util.ArrayList;

public final class DebuggerSingleton {

    private static DebuggerSingleton INSTANCE;

    private ArrayList<AbstractTile> tiles = new ArrayList<>();

    private DebuggerSingleton() {
    }

    public static DebuggerSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DebuggerSingleton();
        }

        return INSTANCE;
    }

    public void setTiles(ArrayList<AbstractTile> tiles) {
        this.tiles = tiles;
    }


    public ArrayList<AbstractTile> getTiles() {
        return tiles;
    }
}
