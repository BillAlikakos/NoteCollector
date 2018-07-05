package com.mygdx.notecollector.screens.menu;

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
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.midilib.MidiManipulator;
import com.mygdx.notecollector.midilib.MidiNote;
import com.mygdx.notecollector.midilib.MidiTrack;
import com.mygdx.notecollector.screens.GameScreen;
import com.mygdx.notecollector.screens.menu.Multiplayer.MultiplayerPreMatchLobby;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bill on 7/19/16.
 */
public class LoadingScreen implements Screen {


    private NoteCollector noteCollector;
    private  Assets AssetsManager;
    private Stage stage;
    private Viewport viewport;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private int speed;
    private long delay;
    private Table table;
    private VerticalGroup verticalGroup;

    private MidiManipulator midiManipulator;
    private File f;

    private ArrayList<MidiNote> notes;

    private float TickPerMsec;

    private BitmapFont font;
    private Thread t = null;
    private Texture spinnerImageTexture;
    private Image spinnerImage;
    private String filepath;
    private MidiTrack track=null;
    private ClientClass c;
    private ServerClass srv;
    private boolean isHost;
    private boolean isGuest;
    private boolean mode;
    boolean flag ;

    public LoadingScreen(NoteCollector noteCollector,String filepath,int speed,long delay,boolean mode)
    {
        this.noteCollector = noteCollector;
        this.filepath = filepath;
        AssetsManager = noteCollector.getAssetsManager();
        f = new File(filepath);
        AssetsManager.LoadLoadingAssets(filepath);
        LoadAssets();
        this.delay = delay;
        this.speed = speed;
        this.mode=mode;
    }
    public LoadingScreen(NoteCollector noteCollector,String filepath,int speed,long delay,MidiTrack track,boolean mode)//Constructor for split track midi file
    {
        this.track=track;
        this.noteCollector = noteCollector;
        this.filepath = filepath;
        AssetsManager = noteCollector.getAssetsManager();
        f = new File(filepath);
        AssetsManager.LoadLoadingAssets(filepath);
        LoadAssets();
        this.delay = delay;
        this.speed = speed;
        this.mode=mode;
    }

    public LoadingScreen(NoteCollector noteCollector,String filepath,int speed,long delay,MidiTrack track,ClientClass c,boolean mode)//Constructor for multiplayer
    {
        this.track=track;
        this.noteCollector = noteCollector;
        this.c=c;
        this.isGuest=true;
        this.isHost=false;
        this.filepath = filepath;
        AssetsManager = noteCollector.getAssetsManager();
        f = new File(filepath);
        AssetsManager.LoadLoadingAssets(filepath);
        LoadAssets();
        this.delay = delay;
        this.speed = speed;
        this.mode=mode;
    }
    public LoadingScreen(NoteCollector noteCollector,String filepath,int speed,long delay,MidiTrack track ,ServerClass srv,boolean mode)//Constructor for multiplayer
    {
        this.flag=false;
        this.track=track;
        this.noteCollector = noteCollector;
        this.srv=srv;
        this.isHost=true;
        this.isGuest=false;
        this.filepath = filepath;
        AssetsManager = noteCollector.getAssetsManager();
        f = new File(filepath);
        AssetsManager.LoadLoadingAssets(filepath);
        LoadAssets();
        this.delay = delay;
        this.speed = speed;
        this.mode=mode;
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
        createLabel("Please Wait ....");
        createSpinner();
        if(isGuest || isHost)//Only add the listeners if the user is host/guest
        {
           // addListeners();
        }
        if(track==null)//Load all notes
        {
            setupMidiManipulator();
        }
        else//Load notes of passed track
        {
            initMidiManipulator();
        }
        stage.addActor(table);
        spinnerImage.getColor().a=0;
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in



    }

    private void createSpinner(){
        //create spinner image from spinner texture
        spinnerImage = new Image(spinnerImageTexture);
        //find the origin and start rotating the spinner
        spinnerImage.setPosition(stage.getWidth()/2-spinnerImage.getWidth()/2,stage.getHeight()/2-spinnerImage.getHeight()/2);
        spinnerImage.setOrigin(spinnerImage.getWidth()/2, spinnerImage.getHeight()/2);
        createActions();
        stage.addActor(spinnerImage);
        spinnerImage.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        spinnerImage.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in

    }
    private void createActions(){
        RotateByAction rotateAction = new RotateByAction();
        rotateAction.setAmount(90f);
        rotateAction.setDuration(1);
        RepeatAction repeatAction = new RepeatAction();
        repeatAction.setAction(rotateAction);
        repeatAction.setCount(RepeatAction.FOREVER);
        spinnerImage.addAction(repeatAction);
    }

    //method for running in a new thread the method setupmidi
    private void setupMidiManipulator(){
        t = new Thread() {
            @Override
            public void run() {
                try {
                    setupMidi();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private void initMidiManipulator()//Method to initiialize midiManipulator for only one track
    {
        t = new Thread() {
            @Override
            public void run() {
                try {
                    initMidi(track);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        try {
            showLoadProgress();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        font.dispose();
        AssetsManager.assetManager.unload(Constants.spinner);
       // t.interrupt();
    }


    //if the method setupmidi isn't running show the game screen  
    private void showLoadProgress() throws IOException, InterruptedException//
    {
        if ( !t.isAlive() && AssetsManager.assetManager.update() && AssetsManager.assetManagerFiles.update())
        {
            table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
            spinnerImage.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
            Timer.schedule(new Timer.Task()
            {
                @Override
                public void run()
                {
                    dispose();
                    if(isHost)
                    {
                        //dispose();
                        noteCollector.setScreen(new MultiplayerPreMatchLobby(noteCollector,TickPerMsec,notes,filepath,speed,delay,srv,mode));

                    }
                    else if(isGuest)
                    {
                       // dispose();
                        noteCollector.setScreen(new MultiplayerPreMatchLobby(noteCollector,TickPerMsec,notes,filepath,speed,delay,c,mode));
                    }
                    else//Single player
                    {
                        //dispose();
                        try
                        {
                            noteCollector.setScreen(new GameScreen(noteCollector,TickPerMsec,notes,filepath,speed,delay,mode));
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
                }
            },0.4f);



        }

    }
    private void LoadAssets(){
        font = AssetsManager.createBimapFont(45);
        spinnerImageTexture = AssetsManager.assetManager.get(Constants.spinner);

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

    private void initMidi(MidiTrack track) throws IOException //Method that gets only the notes of the passed track
    {
        midiManipulator = new MidiManipulator(f);
        TickPerMsec = midiManipulator.getTickPerMsec();
        ArrayList<MidiNote> trackNotes=midiManipulator.GetNotes(track);
        notes = trackNotes;
    }
    //In this method manipulate the midi file and get all midi notes in arraylist notes

    private void setupMidi() throws IOException
    {
        midiManipulator = new MidiManipulator(f);
        TickPerMsec = midiManipulator.getTickPerMsec();
        notes = midiManipulator.GetAllNotes();
        System.out.println("All notes are set");
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
