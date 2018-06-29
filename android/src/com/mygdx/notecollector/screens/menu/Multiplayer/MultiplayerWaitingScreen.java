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
    private Viewport viewport;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Table table;
    private VerticalGroup verticalGroup;
    private BitmapFont font;
    private Thread t = null;
    private ClientClass c;
    private String root = Constants.root;
    private boolean mode;
    private int speed;
    private long delay;

    public MultiplayerWaitingScreen(NoteCollector noteCollector, ClientClass c)
    {
        this.noteCollector = noteCollector;
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        this.c = c;
    }

    @Override
    public void show()
    {
        table = new Table();
        table.setFillParent(true);
        table.center();
        createVerticalGroup();
        setupCamera();
        createBackground();
        createLogo();
        listen();
        createLabel("Waiting Host ....");
        stage.addActor(table);

    }

    private void setDifficultyParams(String difficulty)
    {
        if (VIEWPORT_WIDTH == 800 && VIEWPORT_HEIGHT == 480)//Different speeds and delay for each resolution
        {
            switch (difficulty)
            {
                case "Easy":
                    speed=130;
                    delay=200;
                    break;
                case "Normal":
                    speed=150;
                    delay=160;
                    break;
                case "Hard":
                    speed=170;
                    delay=120;
                    break;
                case "Very Hard":
                    speed=180;
                    delay=100;
                    break;
            }
        }
        else if(VIEWPORT_HEIGHT==720&&VIEWPORT_WIDTH==1080)
        {
            switch (difficulty)
            {
                case "Easy":
                    speed=195;
                    delay=300;
                    break;
                case "Normal":
                    speed=225;
                    delay=240;
                    break;
                case "Hard":
                    speed=255;
                    delay=180;
                    break;
                case "Very Hard":
                    speed=270;
                    delay=150;
                    break;
            }
        }
        else if(VIEWPORT_HEIGHT>720&&VIEWPORT_WIDTH>1080)
        {
            switch (difficulty)//Listeners for multiplayer difficulty screen TODO check speeds (Possibly OK)
            {
                case "Easy":
                    speed=260;
                    delay=300;
                    break;
                case "Normal":
                    speed=300;
                    delay=240;
                    break;
                case "Hard":
                    speed=340;
                    delay=180;
                    break;
                case "Very Hard":
                    speed=360;
                    delay=160;
                    break;
            }
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
                    Timer.schedule(new Timer.Task()
                    {
                        @Override
                        public void run()
                        {
                            if (response.multiTrack == false)
                            {
                                dispose();
                                noteCollector.setScreen(new LoadingScreen(noteCollector, track.getAbsolutePath(), speed, delay, response.mode));//
                            }
                            if (response.multiTrack == true)
                            {
                                dispose();
                                noteCollector.setScreen(new TrackSelect(noteCollector, speed, delay, track, c, response.mode));//
                            }
                        }
                    }, 0.2f);
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
        font.dispose();
//        stage.dispose();
    }


    private void LoadAssets()
    {
        font = AssetsManager.createBimapFont(45);

    }

    private void createLogo()
    {
        Texture img = AssetsManager.assetManager.get(Constants.logo);
        Image background = new Image(img);
        addVerticalGroup(background);
    }

    private void createBackground()
    {
        System.out.println("Width:" + VIEWPORT_WIDTH + " Height:" + VIEWPORT_HEIGHT);
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background = AssetsManager.scaleBackground(file);
        stage.addActor(background);
    }

    private void createVerticalGroup()
    {
        verticalGroup = new VerticalGroup();
        verticalGroup.setFillParent(true);
        AssetsManager.setLogoPosition(verticalGroup);
    }

    private void addVerticalGroup(Actor actor)
    {
        verticalGroup.addActor(actor);

    }


    private void createLabel(String text)
    {
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelstyle);
        table.add(label).colspan(2).padTop(100f);
        if (VIEWPORT_WIDTH == 800 && VIEWPORT_HEIGHT == 480)//Set appropriate sizes for title spacing according to resolution
        {
            table.add(label).colspan(2).padTop(220f).padLeft(50f);
            ;
        }
        if (VIEWPORT_WIDTH == 1080 && VIEWPORT_HEIGHT == 720)
        {
            table.add(label).colspan(2).padTop(220f).padLeft(100f);
        }
        if (VIEWPORT_WIDTH > 1080 && VIEWPORT_HEIGHT > 720)
        {
            table.add(label).colspan(2).padTop(250f).padLeft(50f);
        }

    }

    private void setupCamera()
    {
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }


}
