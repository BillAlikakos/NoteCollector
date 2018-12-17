package com.mygdx.notecollector.screens.menu.Multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.menu.DifficultyScreen;
import com.mygdx.notecollector.screens.menu.LoadingScreen;
import com.mygdx.notecollector.screens.menu.ModeSelect;
import com.mygdx.notecollector.screens.menu.SamplesTrack;
import com.mygdx.notecollector.screens.menu.TrackSelect;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MultiplayerWaitingScreen implements Screen
{
    private NoteCollector noteCollector;
    private Assets AssetsManager;
    private Stage stage;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Table table;
    private VerticalGroup verticalGroup;
    //private BitmapFont font;
    private Thread t = null;
    private ClientClass c;
    private String root = Constants.root;
    private boolean mode;
    private int speed;
    private long delay;

    public MultiplayerWaitingScreen(NoteCollector noteCollector, ClientClass c,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        this.c = c;
    }

    @Override
    public void show()
    {
        System.out.println("Show (Waiting screen)");
        table = new Table();
        table.setFillParent(true);
        table.center();
        createLogo();
        listen();
        createLabel("Waiting Host ....");
        stage.addActor(table);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
    }

    private void setDifficultyParams(String difficulty)//TODO : Remove
    {
        switch (difficulty)
        {
            case "Easy":
                speed=130*VIEWPORT_HEIGHT/480;
                delay=200;
                break;
            case "Normal":
                speed=150*VIEWPORT_HEIGHT/480;
                delay=160;
                break;
            case "Hard":
                speed=170*VIEWPORT_HEIGHT/480;
                delay=120;
                break;
            case "Very Hard":
                speed=180*VIEWPORT_HEIGHT/480;
                delay=100;
                break;
        }
    }

    private void listen()//When client receives the serialized object from host
    {
        c.getClient().addListener(new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof ClientClass.GameParamObject)
                {
                    final ClientClass.GameParamObject response = (ClientClass.GameParamObject) object;
                    System.out.println("Response acquired");
                    final File track = new File(root + "/Note Collector/Sample Tracks/track.midi");
                    try
                    {
                        FileUtils.writeByteArrayToFile(track, response.file);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println(response.multiTrack);
                    setDifficultyParams(response.difficulty);
                    table.addAction(Actions.fadeOut(0.4f));
                    Timer.schedule(new Timer.Task()
                    {
                        @Override
                        public void run()
                        {
                            dispose();
                           /* if (response.multiTrack == false)
                            {
                                noteCollector.setScreen(new LoadingScreen(noteCollector, track.getAbsolutePath(), speed, delay, response.mode,stage));//
                            }*/
                            //if (response.multiTrack == true)
                            {
                                //dispose();
                                noteCollector.setScreen(new TrackSelect(noteCollector, speed, delay, track, c, response.mode,stage));//
                            }
                        }
                    }, 0.4f);
                }

            }
        });
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height);

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {
        AssetsManager.disposeMenuAssets();
        //font.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
    }


    private void LoadAssets()
    {
        //font = AssetsManager.createBimapFont(45*VIEWPORT_WIDTH/1920);
        AssetsManager.LoadAssets();
    }

    private void createLogo()
    {
        Image logo=noteCollector.getAssetsManager().scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        noteCollector.getAssetsManager().setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
    }


    private void createLabel(String text)
    {
        Label.LabelStyle labelstyle = new Label.LabelStyle(AssetsManager.getFont(), Color.WHITE);
        Label label = new Label(text, labelstyle);
        table.add(label).colspan(2).padTop(250f*VIEWPORT_HEIGHT/1080).padLeft(50f*VIEWPORT_WIDTH/1920);

    }



}
