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


import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

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
    private String difficulty;
    private File file;
    private String filepath;
    private ArrayList<MidiNote> notes;

    public MultiplayerPreMatchLobby(NoteCollector noteCollector, float tickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ClientClass c, boolean mode, Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage = stage;
        this.TickPerMsec = tickPerMsec;
        this.notes = notes;
        this.filepath = filepath;
        this.speed = speed;
        this.delay = delay;
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        isGuest = true;
        isHost = false;
        this.mode = mode;
        this.c = c;
    }

    public MultiplayerPreMatchLobby(NoteCollector noteCollector, float tickPerMsec, ArrayList<MidiNote> notes, String filepath, int speed, long delay, ServerClass srv, boolean mode, Stage stage, File file, String difficulty)
    {
        this.noteCollector = noteCollector;
        this.file = file;
        this.difficulty = difficulty;
        this.stage = stage;
        this.TickPerMsec = tickPerMsec;
        this.notes = notes;
        this.filepath = filepath;
        this.speed = speed;
        this.delay = delay;
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        isHost = true;
        isGuest = false;
        this.mode = mode;
        this.srv = srv;
    }

    @Override
    public void show()
    {
        table = new Table();
        table.setFillParent(true);
        table.center();
        createVerticalGroup();
        createLogo();
        createLabel("Initializing ....");
        if(isHost)
        {
            listen();
        }
        else
        {
            send();
        }

        stage.addActor(table);
        table.getColor().a = 0;//Set actor's alpha value to 0(Transparent) to enable fading
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));

    }

    private void send()
    {
        /*if(isHost)
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
        }*/
        if (isGuest)
        {
            /* t=new Thread(){
                @Override
                public void run()
                {
                    while(!this.isInterrupted())
                    {
                        c.sendLoadedMsg();//Send message to host to inform that guest is ready
                    }

                }
            };
            t.start();*/
            System.out.println("Setting time server");
            String TIME_SERVER = "0.gr.pool.ntp.org";//NTP Server to ping
            NTPUDPClient timeClient = new NTPUDPClient();
            timeClient.setDefaultTimeout(2500);
            System.out.println("Sending request");
            InetAddress inetAddress = null;
            try
            {
                inetAddress = InetAddress.getByName(TIME_SERVER);
            }
            catch (UnknownHostException e)//TODO: Handle errors
            {
                e.printStackTrace();
            }
            TimeInfo timeInfo = null;
            try
            {
                timeInfo = timeClient.getTime(inetAddress);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            System.out.println("Got time ?");
            timeClient.close();
            //long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            final long time = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            final long startTime=time+4500;
            //System.out.println("NTP time: "+time);
            //System.out.println("DEV time: "+System.currentTimeMillis());
            //Date time = new Date(returnTime);
            //final long time=System.currentTimeMillis()+4500;
            c.sendLoadedMsg(startTime);
            Timer.schedule(new Timer.Task()
            {
                @Override
                public void run()
                {
                    dispose();
                    noteCollector.setScreen(new GameScreen(noteCollector, TickPerMsec, notes, filepath, speed, delay, c, mode, stage,startTime));
                }
            }, 0.4f);//Prev 0.2f


        }
    }

        private void listen()//Add listeners for messages
        {
            System.out.println("Readying file");

            //System.out.println("Readying Filepath");
            //filepath = file.getAbsolutePath();
            System.out.println("Setting obj params");
            byte[] arr=new byte[1250000];//1,25Mb buffer
            try
            {
                arr = readFileToByteArray(file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            srv.sendGameObj(arr,difficulty,true,mode);
            if(isHost)
            {
                srv.getServer().addListener(new Listener()//Wait for client to load
                {

                    public void received (Connection connection, Object object)
                    {
                        if (object instanceof ServerClass.fileLoaded)//When client responds start the game
                        {
                            System.out.println("Host received");
                            final ServerClass.fileLoaded request = (ServerClass.fileLoaded)object;
                            //System.out.println("Starting time is:"+request.loaded);
                            //System.out.println("Time now is :"+System.currentTimeMillis());
                            //long OG=request.loaded-4500;
                            //System.out.println("Clients OG time is  :"+OG);
                            final long diff=System.currentTimeMillis();
                            //System.out.println("Diff in time  :"+diff);
                            srv.getServer().removeListener(this);
                            table.addAction(Actions.fadeOut(0.4f));
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    // t.interrupt();
                                    dispose();
                                    noteCollector.setScreen(new GameScreen(noteCollector,TickPerMsec,notes,filepath,speed,delay,srv,mode,stage,request.loaded,diff));//TODO : Sync better between slow and fast phones (Pause state until second player joins ? Fix label ordering in game end screen)
                                    //t.interrupt();
                                    //this.wait(1000);

                                }
                            }, 0.4f);//Prev 0.2f
                        }

                    }
                });
            }
            /*else if(isGuest)
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
                            table.addAction(Actions.fadeOut(0.4f));
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        t.interrupt();
                                        dispose();
                                        noteCollector.setScreen(new GameScreen(noteCollector,TickPerMsec,notes,filepath,speed,delay,c,mode,stage));
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
            }*/
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
            //AssetsManager.disposeMenuAssets();
            AssetsManager.assetManager.unload(Constants.logo);
            stage.getRoot().removeActor(table);
            stage.getRoot().removeActor(verticalGroup);
            font.dispose();
            //AssetsManager.assetManager.unload(Constants.spinner);
        }

        private void LoadAssets()
        {
            font = AssetsManager.createBimapFont(45*VIEWPORT_WIDTH/1920);
            //AssetsManager.LoadAssets();
            AssetsManager.assetManager.load(Constants.logo,Texture.class);
            AssetsManager.assetManager.finishLoading();
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

        private void createVerticalGroup()
        {
            verticalGroup  = new VerticalGroup();
            verticalGroup.setFillParent(true);
            AssetsManager.setLogoPosition(verticalGroup);
        }

        private void createLabel(String text){
            Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
            Label label = new Label(text, labelstyle);
            table.add(label).colspan(2).padTop(250f*VIEWPORT_HEIGHT/1080).padLeft(50f*VIEWPORT_WIDTH/1920);
            //stage.addActor(label);
        }
        private void setupCamera(){
            viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
            stage = new Stage(viewport);
            stage.getCamera().update();
        }

}
