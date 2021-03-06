package com.mygdx.notecollector.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.notecollector.GameStage.GameStage;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.WorldUtils;
import com.mygdx.notecollector.midilib.MidiNote;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bill on 6/18/16.
 */
public class GameScreen implements  Screen {

//class of the main screen of game
    private static int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;//Gdx.graphics.getHeight(); //
    private GameStage gameStage;
    private NoteCollector game;
    private String filepath;
    private int speed;
    private long delay;
    private boolean paused;
    private long pausetime;
    private ClientClass c;
    private ServerClass srv;
    private boolean isHost;
    private boolean isGuest;
    private boolean mode;
    private String gameMode;

    private Stage stage;

    public GameScreen(NoteCollector game, float TickPerMsec, ArrayList<MidiNote> notes, String filepath,int speed,long delay,boolean mode,Stage stage)
    {
        gameStage = new GameStage(game,TickPerMsec,notes,speed,delay,mode,stage);
        this.stage=stage;
        this.game = game;
        this.filepath = filepath;
        this.delay = delay;
        this.speed = speed;
        paused =false;
        this.mode=mode;
        gameStage.setGameState("running");
        Gdx.input.setInputProcessor(gameStage);
    }

    public GameScreen(NoteCollector game, float TickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ClientClass c,boolean mode,Stage stage,long StartTime)
    {
        gameStage = new GameStage(game,TickPerMsec,notes,speed,delay,c,mode,stage,StartTime);
        this.stage=stage;
        this.game = game;
        this.filepath = filepath;
        this.delay = delay;
        this.speed = speed;
        this.c=c;
        this.mode=mode;
        //paused =false;
        isHost=false;
        isGuest=true;
        gameStage.setGameState("running");
        Gdx.input.setInputProcessor(gameStage);

    }

    public GameScreen(NoteCollector game, float TickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ServerClass srv,boolean mode,Stage stage,long StartTime,long diff)
    {

        gameStage = new GameStage(game,TickPerMsec,notes,speed,delay,srv,mode,stage,StartTime,diff);
        this.stage=stage;
        this.game = game;
        this.filepath = filepath;
        this.delay = delay;
        this.speed = speed;
        this.srv=srv;
        this.mode=mode;
        //paused =false;
        isHost=true;
        isGuest=false;
        gameStage.setGameState("running");
        Gdx.input.setInputProcessor(gameStage);
    }
    @Override
    public void show()
    {

    }
    public void setupListeners()
    {

        gameStage.setGameState("running");
    }
    @Override
    public void render(float delta) {
        //Clear the screen

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Update the stage
        gameStage.act(Gdx.graphics.getDeltaTime());
        gameStage.draw();
        if (gameStage.getGameState().equals("finished"))
        {
            String difficulty;//Pass difficulty setting (For scoreboard)
            if(this.delay==200 && this.speed==130*VIEWPORT_HEIGHT/480)
            {
                difficulty="Easy";
            }
            else if(this.delay==160 && this.speed==150*VIEWPORT_HEIGHT/480)
            {
                difficulty="Normal";
            }
            else if(this.delay==120 && this.speed==170*VIEWPORT_HEIGHT/480)
            {
                difficulty="Hard";
            }
            else
            {
                difficulty="Very Hard";
            }
            if(mode)
            {
                gameMode="competitive";
            }
            else
            {
                gameMode="practice";
            }
            if(isHost)
            {
                //gameStage.fadeToMenu();//
                dispose();
                game.setScreen(new EndGameScreen(game,gameStage.getScore(),difficulty,srv,stage,gameMode));
            }
            else if(isGuest)
            {
                dispose();
                game.setScreen(new EndGameScreen(game,gameStage.getScore(),difficulty,c,stage,gameMode));//TODO possibly display a message on opponents score if he collected more than 5 red
            }
            else
            {

                dispose();
                game.setScreen(new EndGameScreen(game,gameStage.getScore(),difficulty,stage,gameMode));
            }

        }

        if(isGuest)//Terminate session if opponent leaves
        {
            if(!c.checkConn())
            {
                gameStage.setGameState("finished");
            }
        }
        else if(isHost)
        {
            if(!srv.getConnection().isConnected())
            {
                gameStage.setGameState("finished");
            }
        }

        if(!isHost && !isGuest)
        {
            if (gameStage.getGameState().equals("paused"))
                pause();

            if (gameStage.getGameState().equals("resume"))
                gameStage.resumeGame();
        }

        if (isGameOver())
        {
            if(isHost)
            {
                srv.sendGameOver(gameStage.getScore());//Notify the other player that the game is over
                dispose();
                game.setScreen(new GameOverScreen(game,gameStage.getScore(),filepath,speed,delay,srv,mode,stage));
            }
            else if(isGuest)
            {
                c.sendGameOver(gameStage.getScore());
                dispose();
                game.setScreen(new GameOverScreen(game,gameStage.getScore(),filepath,speed,delay,c,mode,stage));
            }
            else
            {
                dispose();
                game.setScreen(new GameOverScreen(game,gameStage.getScore(),filepath,speed,delay,mode,stage));
            }
        }
    }
    @Override
    public void resize(int width, int height) {

        gameStage.getViewport().update(width,height,true);
    }

    @Override
    public void resume()
    {
        if(!gameStage.isPauseEngaged())
        {
            gameStage.setGameState("resume");
            gameStage.pausetime = pausetime;
        }
    }

    @Override
    public void hide() {


    }

    @Override
    public void dispose()
    {
        gameStage.dispose();
        System.gc();
    }

    @Override
    public void pause()
    {
        //Pause must be disabled in multiplayer
        if(!isGuest && !isHost)
        {
            pausetime  =  System.currentTimeMillis();
            gameStage.pauseGame(pausetime);
        }
        else//Disconnect if focus is lost
        {
            if(isHost)
            {
                srv.sendGameOver(gameStage.getScore());//Notify the other player that the game is over
                dispose();
                game.setScreen(new GameOverScreen(game,gameStage.getScore(),filepath,speed,delay,srv,mode,stage));
            }
            else
            {
                c.sendGameOver(gameStage.getScore());
                dispose();
                game.setScreen(new GameOverScreen(game,gameStage.getScore(),filepath,speed,delay,c,mode,stage));
            }
        }
    }

    private boolean isGameOver(){
        if (gameStage.getRedcounts() == 5)
        {
            //Send message to server to terminate game
            return true;
        }
        return  false;
    }



}
