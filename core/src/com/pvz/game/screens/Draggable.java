package com.pvz.game.screens;

import com.badlogic.gdx.math.Vector2;

public interface Draggable {

    boolean isHovered(Vector2 mousePosition, GameScreen gameScreen);
    void onClick(GameScreen gameScreen, boolean mouseReleased);
    void onDrag(Vector2 mousePosition, GameScreen screen);

}