package com.mygdx.notecollector.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.notecollector.GameStage.GameStage;
import com.mygdx.notecollector.GameStage.TestStage;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.midilib.MidiNote;

import java.util.ArrayList;

public class DemoScreen implements Screen
{

    //class of the main screen of game
    private static int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;//Gdx.graphics.getHeight(); //
    private TestStage gameStage;
    private NoteCollector game;
    private String filepath;
    private int speed;
    private long delay;
    private boolean paused;
    private long pausetime;
    private boolean mode;
    private String gameMode;

    private Stage stage;

    public DemoScreen(NoteCollector game, float TickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, boolean mode, Stage stage)
    {
        gameStage = new TestStage(game,TickPerMsec,notes,speed,delay,mode,stage);
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
            dispose();
            game.setScreen(new EndGameScreen(game,gameStage.getScore(),difficulty,stage,gameMode));
        }

        if (isGameOver())
        {
            dispose();
            game.setScreen(new GameOverScreen(game,gameStage.getScore(),filepath,speed,delay,mode,stage));
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
        pausetime  =  System.currentTimeMillis();
        gameStage.pauseGame(pausetime);
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
