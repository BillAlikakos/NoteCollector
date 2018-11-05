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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.apache.commons.io.FileUtils.readFileToByteArray;


public class SamplesTrack implements Screen {


    private Viewport viewport;
    private Stage stage;
    private BitmapFont font,fontList,fontH;
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
    private  String filepath;
    private String root = Constants.root;
    private Table btn;
    private int speed;
    private long delay;
    private float[] size;
    private float sizeX;
    private float sizeY;
    private  VerticalGroup verticalGroup;
    private boolean multiplayer;
    private ServerClass srv;
    private boolean mode;
    private String difficulty;

    public SamplesTrack(NoteCollector noteCollector,int speed,long delay,boolean mode,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        this.delay = delay;
        this.speed = speed;
        filepath="";
        LoadAssets();
        ListStyle();
        this.multiplayer=false;
        this.mode=mode;
    }

    public SamplesTrack(NoteCollector noteCollector, int speed, long delay, ServerClass srv,boolean mode,String difficulty,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        this.delay = delay;
        this.speed = speed;
        filepath="";
        LoadAssets();
        ListStyle();
        this.multiplayer=true;
        this.srv=srv;
        this.mode=mode;
        this.difficulty=difficulty;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        size=assetsManager.setButtonSize(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];
        btn=new Table();
        btn.setFillParent(true);
        createTable();
        createList();
        getDir(root+"/Note Collector/Sample Tracks");
        createLogo();
        table.add(createLabel("Select a track:")).padTop(VIEWPORT_HEIGHT*0.15f);
        table.row();
        createScrollPane();
        ImageTextButton back= createButton("Back");
        ImageTextButton browse=createButton("Browse");
        btn.left();
        btn.add(back).bottom().left().expand().size(sizeX,sizeY);
        btn.add(browse).bottom().right().expand().size(sizeX,sizeY);
        //btn.setDebug(true);
        stage.addActor(table);
        stage.addActor(btn);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        btn.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in
        btn.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
        System.out.println(0.11f*VIEWPORT_HEIGHT);


    }
    private void createLogo(){
        Image logo=assetsManager.scaleLogo(Gdx.files.internal(Constants.logo));
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
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(btn);
        font.dispose();
        fontList.dispose();

    }
    //create a table to organize buttons and the list of tracks
    private void createTable(){
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(VIEWPORT_HEIGHT*0.05f,VIEWPORT_WIDTH*0.05f,VIEWPORT_HEIGHT*0.1f,VIEWPORT_WIDTH*0.05f);
        table.setTouchable(Touchable.enabled);
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
                    table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
                    btn.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out icon table
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if(!multiplayer)
                            {
                                if (text.equals("Back"))
                                {
                                    dispose();
                                    noteCollector.setScreen(new DifficultyScreen(noteCollector,mode,stage));
                                }
                                else
                                {
                                    dispose();
                                    noteCollector.setScreen(new Browse(noteCollector, speed, delay,mode,stage));
                                }

                            }
                            else
                            {
                                if (text.equals("Back"))
                                {
                                    dispose();
                                    noteCollector.setScreen(new DifficultyScreen(noteCollector,srv,mode,stage));
                                }

                                else
                                {
                                    dispose();
                                    noteCollector.setScreen(new Browse(noteCollector, speed, delay,srv,mode,difficulty,stage));
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
       // fontH = assetsManager.createBimapFont(VIEWPORT_WIDTH*64/1920);
        fontH = assetsManager.createFontH();

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
        fontList = assetsManager.createBimapFont(VIEWPORT_WIDTH*55/1920);
        selectionColorList = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.SelectionColor,Texture.class)));
        skin = assetsManager.assetManager.get(Constants.Skin,Skin.class);
    }

    private void createList()  {
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


    private void addListener(Actor actor){
        actor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
                btn.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out icon table
                Timer.schedule(new Timer.Task()
                {
                    @Override
                    public void run()
                    {
                        dispose();
                        if (!multiplayer)
                        {
                            File file = new File(path.get(list.getSelectedIndex()));
                            filepath = file.getAbsolutePath();
                            noteCollector.setScreen(new TrackSelect(noteCollector, speed, delay, file, mode,stage));//
                        }
                        else
                        {
                            File file = new File(path.get(list.getSelectedIndex()));
                            /*filepath = file.getAbsolutePath();
                            byte[] arr = new byte[1250000];//1,25Mb buffer
                            try
                            {
                                arr = readFileToByteArray(file);
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            //srv.sendGameObj(arr,speed,delay,false,mode);//Old
                            srv.sendGameObj(arr, difficulty, false, mode);
                            System.out.println("Sent game obj to client");*/
                            //noteCollector.setScreen(new LoadingScreen(noteCollector,filepath,speed,delay));
                            noteCollector.setScreen(new TrackSelect(noteCollector, speed, delay, file, srv, mode, difficulty,stage));
                        }
                    }
                },0.4f);

            }
        });
    }

    private Label createLabel(String text){
        Label.LabelStyle labelstyle = new Label.LabelStyle(fontH, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;

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

}
