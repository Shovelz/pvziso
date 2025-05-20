package com.pvz.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;

public interface Clickable {

    boolean isHovered(Vector2 mousePosition, Screen gameScreen);
    void onClick(Screen gameScreen);
}