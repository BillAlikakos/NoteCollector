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
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.midilib.MidiNote;
import com.mygdx.notecollector.screens.GameScreen;
import com.mygdx.notecollector.screens.menu.LoadingScreen;
import com.mygdx.notecollector.screens.menu.TrackSelect;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MultiplayerPreMatchLobby implements Screen
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
    private ServerClass srv;
    private boolean isHost;
    private boolean isGuest;
    private boolean mode;
    private float TickPerMsec;
    private int speed;
    private long delay;
    private File f;
    private String filepath;
    private ArrayList<MidiNote> notes;

    public MultiplayerPreMatchLobby(NoteCollector noteCollector, float tickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ClientClass c,boolean mode)
    {
        this.noteCollector = noteCollector;
        this.TickPerMsec=tickPerMsec;
        this.notes=notes;
        this.filepath=filepath;
        this.speed=speed;
        this.delay=delay;
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        isGuest=true;
        isHost=false;
        this.mode=mode;
        this.c=c;
    }

    public MultiplayerPreMatchLobby(NoteCollector noteCollector, float tickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ServerClass srv,boolean mode)
    {
        this.noteCollector = noteCollector;
        this.TickPerMsec=tickPerMsec;
        this.notes=notes;
        this.filepath=filepath;
        this.speed=speed;
        this.delay=delay;
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        isHost=true;
        isGuest=false;
        this.mode=mode;
        this.srv=srv;
    }

    @Override
    public void show()
    {
        table=new Table();
        table.setFillParent(true);
        table.center();
        createVerticalGroup();
        setupCamera();
        createBackground();
        createLogo();
        listen();
        createLabel("Initializing ....");
        send();
        stage.addActor(table);

    }
    private void send()
    {
        if(isHost)
        {
             t=new Thread(){
                @Override
                public void run()
                {
                    while(!this.isInterrupted())
                    {
                        srv.sendLoadedMsg();//Send message to guest to inform that host is ready
                    }


                }
            };
            t.start();
        }
        else if (isGuest)
        {
             t=new Thread(){
                @Override
                public void run()
                {
                    while(!this.isInterrupted())
                    {
                        c.sendLoadedMsg();//Send message to host to inform that guest is ready
                    }

                }
            };
            t.start();
        }
    }
        private void listen()//Add listeners for messages
        {
            if(isHost)
            {
                srv.getServer().addListener(new Listener()//Wait for client to load
                {

                    public void received (Connection connection, Object object)
                    {
                        if (object instanceof ServerClass.fileLoaded)//When client responds start the game
                        {
                            System.out.println("Host received");
                            ServerClass.fileLoaded request = (ServerClass.fileLoaded)object;
                            System.out.println(request.loaded);
                            srv.getServer().removeListener(this);
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        t.interrupt();
                                        dispose();
                                        noteCollector.setScreen(new GameScreen(noteCollector,TickPerMsec,notes,filepath,speed,delay,srv,mode));
                                        //t.interrupt();
                                        //this.wait(1000);

                                    }
                                    catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }, 0.2f);
                        }

                    }
                });
            }
            else if(isGuest)
            {
                c.getClient().addListener(new Listener()//Wait for client to load
                {
                    public void received (Connection connection, Object object)
                    {
                        if (object instanceof ClientClass.fileLoaded)//When client responds start the game
                        {
                            System.out.println("Host received");
                            ClientClass.fileLoaded request = (ClientClass.fileLoaded)object;
                            System.out.println(request.loaded);
                            c.getClient().removeListener(this);
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        t.interrupt();
                                        dispose();
                                        noteCollector.setScreen(new GameScreen(noteCollector,TickPerMsec,notes,filepath,speed,delay,c,mode));
                                       // t.interrupt();

                                    }
                                    catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }, 0.4f);
                        }

                    }
                });
            }
        }


        @Override
        public void render(float delta) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.act();
            stage.draw();
        }

        @Override
        public void resize(int width, int height) {
            stage.getViewport().update(width,height);

        }

        @Override
        public void pause() {

        }

        @Override
        public void resume() {

        }

        @Override
        public void hide() {

        }

        @Override
        public void dispose()
        {
            stage.dispose();
            font.dispose();
            //AssetsManager.assetManager.unload(Constants.spinner);
        }

        private void LoadAssets()
        {
            font = AssetsManager.createBimapFont(45);

        }
        private void createLogo()
        {
            Texture img =  AssetsManager.assetManager.get(Constants.logo);
            Image background = new Image(img);
            addVerticalGroup(background);
        }
        private void createBackground()
        {
            System.out.println("Width:"+VIEWPORT_WIDTH+" Height:"+VIEWPORT_HEIGHT);
            FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
            Image background=AssetsManager.scaleBackground(file);
            stage.addActor(background);
        }
        private void createVerticalGroup()
        {
            verticalGroup  = new VerticalGroup();
            verticalGroup.setFillParent(true);
            AssetsManager.setLogoPosition(verticalGroup);
        }

        private  void addVerticalGroup(Actor actor){
            verticalGroup.addActor(actor);

        }


        private void createLabel(String text){
            Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
            Label label = new Label(text, labelstyle);
            table.add(label).colspan(2).padTop(100f);
            //label.setPosition((stage.getCamera().viewportWidth-label.getWidth())/2,100f);
            if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//Set appropriate sizes for title spacing according to resolution
            {
                table.add(label).colspan(2).padTop(220f).padLeft(50f);;
            }
            if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
            {
                table.add(label).colspan(2).padTop(220f).padLeft(100f);
            }
            if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
            {
                table.add(label).colspan(2).padTop(250f).padLeft(50f);
            }

            //stage.addActor(label);
        }
        private void setupCamera(){
            viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
            stage = new Stage(viewport);
            stage.getCamera().update();
        }

}
