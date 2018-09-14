package com.mygdx.notecollector.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.notecollector.GameStage.GameStage;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.midilib.MidiNote;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bill on 6/18/16.
 */
public class GameScreen implements  Screen {

//class of the main screen of game
    public GameStage stage;
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

    public GameScreen(NoteCollector game, float TickPerMsec, ArrayList<MidiNote> notes, String filepath,int speed,long delay,boolean mode) throws IOException, InterruptedException {

        stage = new GameStage(game,TickPerMsec,notes,speed,delay,mode);
        this.game = game;
        this.filepath = filepath;
        this.delay = delay;
        this.speed = speed;
        paused =false;
        this.mode=mode;
        stage.setGameState("running");
        Gdx.input.setInputProcessor(stage);

    }

    public GameScreen(NoteCollector game, float TickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ClientClass c,boolean mode) throws IOException, InterruptedException {
        stage = new GameStage(game,TickPerMsec,notes,speed,delay,c,mode);
        this.game = game;
        this.filepath = filepath;
        this.delay = delay;
        this.speed = speed;
        this.c=c;
        this.mode=mode;
        //paused =false;
        isHost=false;
        isGuest=true;
        stage.setGameState("running");
        Gdx.input.setInputProcessor(stage);

    }

    public GameScreen(NoteCollector game, float TickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ServerClass srv,boolean mode) throws IOException, InterruptedException {

        stage = new GameStage(game,TickPerMsec,notes,speed,delay,srv,mode);
        this.game = game;
        this.filepath = filepath;
        this.delay = delay;
        this.speed = speed;
        this.srv=srv;
        this.mode=mode;
        //paused =false;
        isHost=true;
        isGuest=false;
        stage.setGameState("running");
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Clear the screen

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Update the stage
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (stage.getGameState() == "finished")
        {
            String difficulty;//Pass difficulty setting (For scoreboard)
            if(this.delay==200 && this.speed==130)
            {
                difficulty="Easy";
            }
            else if(this.delay==160 && this.speed==150)
            {
                difficulty="Normal";
            }
            else if(this.delay==120 && this.speed==170)
            {
                difficulty="Hard";
            }
            else
            {
                difficulty="Very Hard";
            }
            if(isHost)
            {
                dispose();
                game.setScreen(new EndGameScreen(game,stage.getScore(),difficulty,srv));
            }
            else if(isGuest)
            {
                dispose();
                game.setScreen(new EndGameScreen(game,stage.getScore(),difficulty,c));//TODO possibly display a message on opponents score if he collected more than 5 red
            }
            else
            {
                dispose();
                game.setScreen(new EndGameScreen(game,stage.getScore(),difficulty));
            }

        }

        if(isGuest)//Terminate session if opponent leaves
        {
            if(!c.checkConn())
            {
                stage.setGameState("finished");
            }
        }
        else if(isHost)
        {
            if(!srv.getConnection().isConnected())
            {
                stage.setGameState("finished");
            }
        }


        if (stage.getGameState() == "paused")
            pause();

        if (stage.getGameState() == "resume")
            stage.resumeGame();

        if (isGameOver())
        {
            if(isHost)
            {
                srv.sendGameOver(stage.getScore());//Notify the other player that the game is over
                dispose();
                game.setScreen(new GameOverScreen(game,stage.getScore(),filepath,speed,delay,srv,mode));
            }
            else if(isGuest)
            {
                c.sendGameOver(stage.getScore());
                dispose();
                game.setScreen(new GameOverScreen(game,stage.getScore(),filepath,speed,delay,c,mode));
            }
            else
            {
                dispose();
                game.setScreen(new GameOverScreen(game,stage.getScore(),filepath,speed,delay,mode));
            }
        }
    }
    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width,height,true);
    }

    @Override
    public void resume() {
        stage.setGameState("paused");
        stage.pausetime = pausetime;
    }

    @Override
    public void hide() {


    }

    @Override
    public void dispose() {
        stage.dispose();

    }

    @Override
    public void pause()
    {
        //Pause must be disabled in multiplayer
        pausetime  =  System.currentTimeMillis();
        stage.pauseGame(pausetime);

    }

    private boolean isGameOver(){
        if (stage.getRedcounts() == 5)
        {
            //Send message to server to terminate game
            return true;
        }
        return  false;
    }



}