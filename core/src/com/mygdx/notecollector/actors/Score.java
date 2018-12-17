package com.mygdx.notecollector.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.screens.GameScreen;

import java.io.IOException;

/**
 * Created by bill on 6/28/16.
 */
public class Score extends Actor {

// class for drawing score
    private float score;

    private Rectangle bounds;
    private BitmapFont font;
    private boolean isHost;
    private boolean isGuest;
    private ServerClass srv;
    private ClientClass c;
    static int scoreV;

    public Score(Rectangle bounds, Assets AssetsManager)
    {
      this.isGuest=false;
      this.isHost=false;
      setHeight(bounds.height);
      setWidth(bounds.width);
      this.bounds = bounds;
      score = 0;
      //get font type from asset manager
        //font = AssetsManager.createBimapFont(28);
        //font = AssetsManager.createFont();
        font = AssetsManager.getFont();
        //font.setColor(Color.BLACK);
        font.setColor(Color.WHITE);

    }

    public Score(Rectangle bounds, Assets AssetsManager, ClientClass c)
    {
        this.c=c;
        this.isGuest=true;
        this.isHost=false;
        setHeight(bounds.height);
        setWidth(bounds.width);
        this.bounds = bounds;
        score = 0;
        //get font type from asset manager
        //font = AssetsManager.createBimapFont(28);
        font = AssetsManager.getFont();
        font.setColor(Color.WHITE);

    }

    public Score(Rectangle bounds, Assets AssetsManager, ServerClass srv)
    {
        this.srv=srv;
        this.isGuest=false;
        this.isHost=true;
        setHeight(bounds.height);
        setWidth(bounds.width);
        this.bounds = bounds;
        score = 0;
        //get font type from asset manager
        //font = AssetsManager.createBimapFont(28);
        font = AssetsManager.getFont();
        font.setColor(Color.WHITE);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (score == 0)
            return;
       // font.draw(batch, String.format("%d", getScore()), bounds.x,bounds.y);
        if(isGuest || isHost)
        {
            font.draw(batch, String.format("%s", "Opponent: "+getScore()), bounds.x,bounds.y);
        }
        else
        {
            font.draw(batch, String.format("%s", "Score: "+getScore()), bounds.x,bounds.y);
        }



    }

    public int getScore() {
        return (int) Math.floor(score);
    }


//method for getting score
    public void setScore(int score) {
            this.score = score ;
    }

    public void dispose()
    {
        font.dispose();
    }



}
