package com.mygdx.notecollector.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.notecollector.Utils.Assets;

/**
 * Created by bill on 7/1/16.
 */
public class Text extends Actor {


    //class for drawing text for bonus and failure    
    private Rectangle bounds;
    private BitmapFont font;
    private String message="";
    private Color color=Color.BLACK;


    public Text(Rectangle bounds, Assets AssetsManager) {
        setHeight(bounds.height);
        setWidth(bounds.width);
        this.bounds = bounds;
        //font = AssetsManager.createBimapFont(21);
        font = AssetsManager.createBitmapFont();
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        font.setColor(color);

        font.draw(batch, message, bounds.x,bounds.y);
    }

}
