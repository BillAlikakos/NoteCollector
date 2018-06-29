package com.mygdx.notecollector.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.midilib.MidiFile;
import com.mygdx.notecollector.midilib.MidiTrack;
import com.mygdx.notecollector.midilib.event.MidiEvent;
import com.mygdx.notecollector.midilib.event.ProgramChange;
import com.mygdx.notecollector.midilib.event.meta.GenericMetaEvent;
import com.mygdx.notecollector.midilib.event.meta.MetaEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static org.apache.commons.io.FileUtils.readFileToByteArray;


public class TrackSelect implements Screen
{
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font,fontList,fontH,fontBtn;
    private Texture img;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private NoteCollector noteCollector;
    private Assets assetsManager;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private TextureRegionDrawable selectionColorList;
    private Table table;
    private ScrollPane scrollPane;
    private List<Object> list;
    private Skin skin;
    private ArrayList<String> item = null;
    private ArrayList<String> path = null;
    private  ArrayList<MidiTrack> track=null;
    private  String filepath;
    private String root = Constants.root;
    private Table btn;
    private int speed;
    private long delay;
    private int[] size;
    private int sizeX;
    private int sizeY;
    private VerticalGroup verticalGroup;
    private File file;
    private ServerClass srv;
    private ClientClass c;
    private boolean isHost;//Flags for client/server detection
    private boolean isGuest;
    private boolean mode;
    private String difficulty;

    public TrackSelect(NoteCollector noteCollector,int speed,long delay,File file,boolean mode)
    {
        this.file=file;
        this.noteCollector = noteCollector;
        assetsManager = noteCollector.getAssetsManager();
        this.delay = delay;
        this.speed = speed;
        this.isGuest=false;
        this.isHost=false;
        this.mode=mode;
        filepath="";
        LoadAssets();
        ListStyle();
    }

    public TrackSelect(NoteCollector noteCollector,int speed,long delay,File file,ServerClass srv,boolean mode,String difficulty)//Constructor for multiplayer host
    {
        this.file=file;
        this.noteCollector = noteCollector;
        assetsManager = noteCollector.getAssetsManager();
        this.delay = delay;
        this.speed = speed;
        this.srv=srv;
        this.isHost=true;
        this.isGuest=false;
        this.mode=mode;
        this.difficulty=difficulty;
        filepath="";
        LoadAssets();
        ListStyle();
    }

    public TrackSelect(NoteCollector noteCollector,int speed,long delay,File file, ClientClass c,boolean mode)//Constructor for multiplayer guest
    {
        this.file=file;
        this.noteCollector = noteCollector;
        assetsManager = noteCollector.getAssetsManager();
        this.delay = delay;
        this.speed = speed;
        this.c=c;
        this.isHost=false;
        this.isGuest=true;
        this.mode=mode;
        filepath="";
        LoadAssets();
        ListStyle();
    }

    @Override
    public void show()
    {
        setupCamera();
        Gdx.input.setInputProcessor(stage);
        createBackground();
        btn=new Table();
        btn.setFillParent(true);
        createTable();
        createList();
        getInstruments(file);
        createLogo();
        size=assetsManager.setButtonDimensions(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];
        table.add(createLabel("Select a track:")).padTop(70f);
        table.row();
        table.bottom().padBottom(50f);
        createScrollPane();
        if(!isGuest)//Client mustn't go back as the host is responsible for the settings of the game session.
        {
            ImageTextButton back= createButton("Back");
            btn.left();
            btn.add(back).bottom().left().expand().size(sizeX,sizeY);
            //btn.setDebug(true);

            stage.addActor(btn);
        }
        stage.addActor(verticalGroup);
        stage.addActor(table);

    }
    private void createLogo()
    {
        Texture img = assetsManager.assetManager.get(Constants.logo);
        Image logo = new Image(img);
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        assetsManager.setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);

    }
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        table.clear();
        stage.dispose();
        font.dispose();
        fontList.dispose();
        //img.dispose();


    }
    //create a table for organize buttons and list of tracks
    private void createTable(){
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(60f,10f,30f,10f);
        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//old dimensions
        {
            table.pad(60f,10f,30f,10f);

        }
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {

            table.pad(120f,10f,30f,10f);
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            table.pad(200f,10f,30f,10f);
        }

        //table.setTouchable(Touchable.enabled);
        //table.setDebug(true);
    }
    private ImageTextButton createButton(String text)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);

        AddButtonListener(MenuButton,text);
        return MenuButton;
    }

    private void AddButtonListener(final ImageTextButton MenuButton, final String text){
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if (text.equals("Back"))
                            {
                                if(isHost)
                                {
                                    noteCollector.setScreen(new DifficultyScreen(noteCollector,srv,mode));
                                }
                                else
                                {
                                    noteCollector.setScreen(new DifficultyScreen(noteCollector,mode));
                                }

                            }



                        }
                    }, 0.4f);
                }
                return true;
            }

        });

    }

    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage){

        ImageTextButton.ImageTextButtonStyle textButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        textButtonStyle.up = ButtonImage;
        textButtonStyle.down = selectionColorPressed;
        textButtonStyle.over = ButtonImage;
        textButtonStyle.font = font;
        return textButtonStyle;
    }


    private void LoadAssets(){
        fontH = assetsManager.createBimapFont(45);
        font = assetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);


    }

    private void ListStyle(){
        assetsManager.LoadListAssets();
        fontList = assetsManager.createBitmapFont();
        selectionColorList = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.SelectionColor,Texture.class)));
        skin = assetsManager.assetManager.get(Constants.Skin,Skin.class);
    }

    private void createList()
    {
        list = new List<Object>(skin);
        list.getStyle().selection = selectionColorList;
        list.getStyle().font = fontList;
        addListener(list);
    }

    private void createScrollPane(){
        scrollPane = new ScrollPane(list);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setScrollingDisabled(true,false);
        table.add(scrollPane).expand().fill().padBottom(50f);
        table.row();
    }


    private void addListener(Actor actor)
    {
        actor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                MidiTrack t=track.get(list.getSelectedIndex());//Get the selected track
                String program=item.get(list.getSelectedIndex());//Get the selected instrument
                System.out.println("Selected Track: "+t.toString());
                System.out.println("Selected Instrument: "+program);
                filepath = file.getAbsolutePath();
                if(isHost)
                {
                    System.out.println("Host");
                    System.out.println("Readying file");
                    //File file = new File(path.get(list.getSelectedIndex()));
                    System.out.println("Readying Filepath");
                    filepath = file.getAbsolutePath();
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
                    srv.sendGameObj(arr,difficulty,true,mode);//Send file to client
                    dispose();
                    noteCollector.setScreen((new LoadingScreen(noteCollector,filepath,speed,delay,t,srv,mode)));
                }
                else if(isGuest)
                {
                    System.out.println("Guest");
                    dispose();
                    noteCollector.setScreen((new LoadingScreen(noteCollector,filepath,speed,delay,t,c,mode)));
                }
                else
                {
                    dispose();
                    noteCollector.setScreen(new LoadingScreen(noteCollector,filepath,speed,delay,t,mode));
                }
            }
        });
    }
    private Label createLabel(String text){
        Label.LabelStyle labelstyle = new Label.LabelStyle(fontH, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;

    }

    private void getInstr(File file)
    {



    }

    private void getInstruments(File file)//Method that finds all the instruments that are used in each track
    {
        item = new ArrayList<String>();
        track = new ArrayList<MidiTrack>();
        MidiFile midi = null;
        try {
            midi = new MidiFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<MidiTrack> filetracks = midi.getTracks();
        //ArrayList<MetaEvent> metaEvents=midi.
        Iterator<MidiTrack> it = filetracks.iterator();
        ArrayList<MidiEvent> midiEvents=new ArrayList<>();
        /*If midi file has tracks continue*/
        for (int i=0;i<filetracks.size();i++)
        {
            Iterator<MidiEvent> midiEventIterator = it.next().getEvents().iterator();
            //Iterator<MetaEvent> metaEventIterator =
            /*If midi file track has event continue */
            while (midiEventIterator.hasNext())
            {
                MidiEvent E = midiEventIterator.next();
                if (E.getClass().equals(ProgramChange.class))//Find instrument changes
                {
                    System.out.println("Track # :" + i);//Get used instruments for each track
                    ProgramChange change = (ProgramChange) E;
                    int pg = change.getProgramNumber();
                    ProgramChange.MidiProgram[] arr = ProgramChange.MidiProgram.values();//Array to hold all used instruments
                    System.out.println("Instrument :" + arr[pg]);//Get used instrument of track i
                    String name="Track: "+i+" Instument: "+findInstrument(arr[pg]);//Separate tracks with similar instruments by number
                    System.out.println(name);
                    item.add(name);
                    track.add(filetracks.get(i));
                    //System.out.println(E.getClass().toString());
                    midiEvents.add(E);
                }
            }
        }
        if(midi.getTracks().size()==1)
        {
            String name="Track: 1 ";//If file has one track only
            System.out.println(name);
            item.add(name);
            track.add(midi.getTracks().get(0));
        }
        list.setItems(item.toArray());
    }

    private String findInstrument(ProgramChange.MidiProgram program)
    {
        String name="";
        name=program.toString();
        return name;
    }
    //get the dir of sample tracks
    private void getDir(String dirPath) {

        path = new ArrayList<String>();
        item = new ArrayList<String>();


        File f = new File(dirPath);
        File [] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if( (checkMidiTYpe(file.getName()) ) )
                path.add(file.getPath());


            if(file.isDirectory() || checkMidiTYpe(file.getName()) )
            {
                item.add(file.getName());

            }
        }
        list.setItems(item.toArray());
    }
    //if the type of files is midi return true else return false.
    private boolean checkMidiTYpe(String name){

        if(name.toLowerCase().endsWith(".mid") ||name.toLowerCase().endsWith(".midi"))
            return true;
        return false;

    }

    private void createBackground()
    {
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background=assetsManager.scaleBackground(file);
        stage.addActor(background);

    }

    private void setupCamera(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }

}
