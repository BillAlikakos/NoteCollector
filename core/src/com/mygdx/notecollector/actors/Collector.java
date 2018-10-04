package com.mygdx.notecollector.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;

/**
 * Created by bill on 6/21/16.
 */
public class Collector extends GameActor {

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Texture img;
    private Sprite sprite;
    private float Xaxis;
    private float Yaxis;
    private boolean paused;
    private Preferences prefs;


    public Collector(Body body, Assets AssetsManager) {
        super(body,AssetsManager);
        //load user preferences
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");


        //Check size of gray square    
        if(prefs.getBoolean("normal")) {
            img = AssetsManager.assetManager.get(Constants.Collector);
        }else if (prefs.getBoolean("big")){
            img = AssetsManager.assetManager.get(Constants.BigCollector);
        }else if (prefs.getBoolean("vbig")){
            img = AssetsManager.assetManager.get(Constants.VeryBigCollector);
        }
        sprite = new Sprite(img);
        this.Xaxis = Constants.CollectorStartX480;
        this.Yaxis = Constants.CollectorStartY480;
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            this.Xaxis = Constants.CollectorStartX720;
            this.Yaxis = Constants.CollectorStartY720;
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            this.Xaxis = Constants.CollectorStartX1080;
            this.Yaxis = Constants.CollectorStartY1080;
        }
        sprite.setColor(Color.GRAY);
       paused=false;

    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //draw square
        sprite.setPosition(Xaxis,Yaxis);
        sprite.draw(batch);
    }

    public void changeposition(float Xaxis, float Yaxis)
    {
        //if game not paused
        if(paused==false) {
            //change position
            this.Xaxis = Xaxis;
            this.Yaxis = Yaxis;
        }

    }
    public void moveUp()
    {
        if(paused==false)
        {
            //System.out.println("move up");
            //System.out.println("Y axis:"+this.Yaxis);
            this.Yaxis += 200 * Gdx.graphics.getDeltaTime();
            //System.out.println("Y axis:"+this.Yaxis);
        }
    }
    public void moveDown()
    {
        if(paused==false)
        {
            System.out.println("move down");
            this.Yaxis -= 200 * Gdx.graphics.getDeltaTime();
        }
    }
    public void moveRight()
    {
        if(paused==false)
        {
            this.Xaxis += 200 * Gdx.graphics.getDeltaTime();
        }
    }
    public void moveLeft()
    {
        if(paused==false)
        {
            this.Xaxis -= 200 * Gdx.graphics.getDeltaTime();
        }
    }

    public float getXaxis()
    {
        return Xaxis;
    }

    public float getYaxis()
    {
        return Yaxis;
    }
}
