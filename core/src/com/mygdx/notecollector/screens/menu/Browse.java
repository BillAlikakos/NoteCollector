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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.apache.commons.io.FileUtils.readFileToByteArray;


/**
 * Created by bill on 7/7/16.
 */
public class Browse implements Screen {
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font,fontList,fontH;

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
    private int speed;
    private long delay;
    private VerticalGroup verticalGroupLogo;
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Table exitBtnTable;
    private ServerClass srv;
    private boolean multiplayer;
    private boolean mode;
    private String difficulty;

    public Browse(NoteCollector noteCollector,int speed,long delay,boolean mode)
    {
        this.delay = delay;
        this.speed = speed;
        this.noteCollector = noteCollector;
        filepath="";
        assetsManager = noteCollector.getAssetsManager();
        this.multiplayer=false;
        this.mode=mode;
        LoadAssets();
        ListStyle();
    }
    public Browse(NoteCollector noteCollector,int speed,long delay,boolean mode,Stage stage)
    {
        this.stage=stage;
        this.delay = delay;
        this.speed = speed;
        this.noteCollector = noteCollector;
        filepath="";
        assetsManager = noteCollector.getAssetsManager();
        this.multiplayer=false;
        this.mode=mode;
        LoadAssets();
        ListStyle();
    }

    public Browse(NoteCollector noteCollector, int speed, long delay, ServerClass srv,boolean mode,String difficulty,Stage stage)
    {
        this.stage=stage;
        this.delay = delay;
        this.speed = speed;
        this.noteCollector = noteCollector;
        this.srv=srv;
        this.multiplayer=true;
        this.mode=mode;
        this.difficulty=difficulty;
        filepath="";
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        ListStyle();
    }

    @Override
    public void show()
    {
        verticalGroupLogo=new VerticalGroup();
        Gdx.input.setInputProcessor(stage);
        exitBtnTable=new Table();
        size=assetsManager.setButtonSize(sizeX,sizeY);//Get the dimensions for the button
        sizeX=size[0];
        sizeY=size[1];
        createTable();
        createList();
        getDir(root);
        createLogo();
        table.add(createLabel("Select a track:")).padTop(VIEWPORT_HEIGHT*0.14f);
        table.row();
        exitBtnTable.toFront();
        createButton("Back",sizeX,sizeY);
        stage.addActor(exitBtnTable);
        createScrollPane();

        stage.addActor(table);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        exitBtnTable.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in
        exitBtnTable.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
    }
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
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


    private void createLogo()
    {
        Image logo=assetsManager.scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroupLogo  = new VerticalGroup();
        verticalGroupLogo.setFillParent(true);
        verticalGroupLogo.center();
        verticalGroupLogo.addActor(logo);
        assetsManager.setLogoPosition(verticalGroupLogo);
        stage.addActor(verticalGroupLogo);

    }
    private void createTable(){
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(VIEWPORT_HEIGHT*0.05f,VIEWPORT_WIDTH*0.05f,VIEWPORT_HEIGHT*0.1f,VIEWPORT_WIDTH*0.05f);
    }
    private void createButton(String text,float sizeX,float sizeY)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);
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
                    exitBtnTable.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out icon table
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
                                    noteCollector.setScreen(new Browse(noteCollector, speed, delay,mode,stage));//Useless ??
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
                                    noteCollector.setScreen(new Browse(noteCollector, speed, delay,mode,stage));
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
        //fontH = assetsManager.createBimapFont(VIEWPORT_WIDTH/30);
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
        fontList = assetsManager.createBitmapFont();
        selectionColorList = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.SelectionColor,Texture.class)));
        skin = assetsManager.assetManager.get(Constants.Skin,Skin.class);
    }

    private void createList()  {
        list = new List<Object>(skin);
        list.getStyle().selection = selectionColorList;
        list.getStyle().font = fontList;
        addListener(list);
    }

    //set a scrollable list
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

                changeDirectory(list.getSelectedIndex());
            }
        });
    }
    private Label createLabel(String text){
        Label.LabelStyle labelstyle = new Label.LabelStyle(fontH, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;

    }

    // Method to change directory in the file system
    private void changeDirectory(int position)  {
        final File file = new File(path.get(position));
        if (file.isDirectory())
        {

            if (file.canRead())
            {
                getDir(path.get(position));
            }
        }
        else
        {
            table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
            exitBtnTable.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
            filepath = file.getAbsolutePath();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run()
                {
                    if(!multiplayer)
                    {
                        dispose();
                        noteCollector.setScreen(new TrackSelect(noteCollector,speed,delay,file,mode,stage));
                    }
                    else
                    {
                        dispose();
                        noteCollector.setScreen(new TrackSelect(noteCollector,speed,delay,file,srv,mode,difficulty,stage));
                    }
                }
            },0.4f);


        }
    }

    private String method(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length()-1)=='/') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
    //set the list content
    private void getDir(String dirPath) {

        path = new ArrayList<String>();
        item = new ArrayList<String>();

        if (dirPath.equals(method(root)))
            dirPath = root;
        File f = new File(dirPath);
        File [] files = f.listFiles();

        if (!dirPath.equals(root)) {

            item.add("../");
            path.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if( (file.isDirectory()  &&  checkStartsWith(file.getName())) ||checkMidiTYpe(file.getName()))
                path.add(file.getPath());

            if (file.isDirectory() && checkStartsWith(file.getName()) )
                item.add(file.getName() + "/");
            else  if(checkMidiTYpe(file.getName())  )
            {
                item.add(file.getName());

            }
        }
        list.setItems(item.toArray());
    }

    //check if a file starts with "."
    private boolean checkStartsWith(String name){

        if(!name.toLowerCase().startsWith(".") )
            return true;
        return false;

    }
    private boolean checkMidiTYpe(String name){

        if(name.toLowerCase().endsWith(".mid") ||name.toLowerCase().endsWith(".midi"))
            return true;
        return false;

    }

    @Override
    public void dispose()
    {
        table.clear();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(exitBtnTable);
        stage.getRoot().removeActor(verticalGroupLogo);
        font.dispose();
        fontList.dispose();

    }

}
