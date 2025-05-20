package com.pvz.game.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.audio.MusicManager;
import com.pvz.game.audio.SoundManager;

import java.util.ArrayList;

public class Menu {

    private Texture menuSprite;
    private Camera camera;
    private MenuButton menuButton;
    private ArrayList<Slider> sliders = new ArrayList<>();
    private Rectangle hitbox;


    public Menu(Camera cam){
        camera = cam;
        menuSprite = new Texture("pauseMenu.png");
        menuButton = new MenuButton(cam);

        Vector2 newDimensions = new Vector2(menuSprite.getWidth(), menuSprite.getHeight());
        hitbox = new Rectangle( camera.position.x - newDimensions.x/2, camera.position.y - newDimensions.y/2, newDimensions.x, newDimensions.y);

        sliders.add(new Slider(new Vector2(camera.position.x, camera.position.y), 70,  value -> MusicManager.getInstance().setVolume(value), new Texture("icons/musicIcon.png")));
        sliders.add(new Slider(new Vector2(camera.position.x, camera.position.y+10), 70, value -> SoundManager.getInstance().setVolume(value), new Texture("icons/soundIcon.png")));
    }

    public void render(SpriteBatch batch){
        batch.draw(menuSprite, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        menuButton.render(batch);
        for(Slider slider : sliders){
            slider.render(batch);
        }
    }


    public MenuButton getMenuButton(){
        return menuButton;
    }

    public void update(Vector2 mousePosition) {
        for(Slider slider : sliders) {
            slider.update(mousePosition);
        }
    }

    public ArrayList<SliderButton> getSliderButtons() {
        ArrayList<SliderButton> sliderButtons = new ArrayList<SliderButton>();
        for(Slider slider : sliders) {
            sliderButtons.add(slider.getButton());
        }
        return sliderButtons;
    }
}
