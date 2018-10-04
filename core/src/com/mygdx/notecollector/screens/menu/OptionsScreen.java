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
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.minlog.Log;
import com.mygdx.notecollector.IGallery;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToByteArray;


/**
 * Created by bill on 9/15/16.
 */
public class OptionsScreen implements Screen{


    private Stage stage;
    private BitmapFont font;
    private Assets assetsManager;
    private Viewport viewport;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private NoteCollector noteCollector;
    private VerticalGroup verticalGroup;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private Table table;
    private Table exitBtnTable;
    private ImageTextButton MusicOn,MusicOff;
    private ImageTextButton SoundOn,SoundOff;
    private ImageTextButton menuBg,gameBg;
    private ImageTextButton NormalSize,BigSize,VBigSize;
    private ImageTextButton touch,pad;
    private  boolean flag =false;
    private int sizeX;
    private int sizeY;
    private int[] size;
    private Preferences prefs;
    private IGallery gallery;
    private String res;

    public OptionsScreen(NoteCollector noteCollector) {
        this.noteCollector = noteCollector;
        assetsManager = noteCollector.getAssetsManager();
        font = assetsManager.createBimapFont(15);
        table = new Table();
        exitBtnTable=new Table();
        LoadAssets();
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
        size=assetsManager.setButtonDimensions(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];

    }
    public OptionsScreen(NoteCollector noteCollector,Stage stage) {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        font = assetsManager.createBimapFont(15);
        table = new Table();
        exitBtnTable=new Table();
        LoadAssets();
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
        size=assetsManager.setButtonDimensions(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];

    }

    private Label createLabel(String text){

        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;

    }


    @Override
    public void show() {
        //setupCamera();
        Gdx.input.setInputProcessor(stage);
        createTable();
        //createBackground();
        createLogo();
        createButtonMusic();
        createButtonSound();
        createButtonSize();
        createControlTypes();
        createCustomBg();
        stage.addActor(table);
        stage.addActor(exitBtnTable);
        createBackButton("Menu");
        table.getColor().a=0;//Fade table in
        exitBtnTable.getColor().a=0;//Fade table in
        table.addAction(Actions.fadeIn(0.2f));
        exitBtnTable.addAction(Actions.fadeIn(0.2f));
    }
    private void createButtonMusic(){
        Label title = createLabel("Music:");
        table.row();
        table.add(title).padRight(5f);

            ImageTextButton.ImageTextButtonStyle MusicOnStyle;
        if (!prefs.getBoolean("music")){
            MusicOnStyle = createButtonStyle(selectionColor);

        }else {
            MusicOnStyle = createButtonStyle(selectionColorPressed);

        }

            ImageTextButton.ImageTextButtonStyle MusicOffStyle;
        if (prefs.getBoolean("music")) {
            MusicOffStyle = createButtonStyle(selectionColor);
        }else {
            MusicOffStyle = createButtonStyle(selectionColorPressed);
        }
            MusicOn = createButton("On", MusicOnStyle);
            MusicOff = createButton("Off", MusicOffStyle);

        MusicOn.addListener(new ClickListener() {


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(MusicOn.isPressed() && !prefs.getBoolean("music")){
                    MusicOn.setStyle(createButtonStyle(selectionColorPressed));
                    MusicOff.setStyle(createButtonStyle(selectionColor));

                    if (prefs.getBoolean("music")) {
                        noteCollector.getClick().play();
                    }
                    prefs.putBoolean("music", true);
                }
                prefs.flush();
                return true;
            }


        });
        MusicOff.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(MusicOff.isPressed() && prefs.getBoolean("music")){
                    MusicOn.setStyle(createButtonStyle(selectionColor));
                    MusicOff.setStyle(createButtonStyle(selectionColorPressed));
                    if (prefs.getBoolean("music")) {
                        noteCollector.getClick().play();
                    }
                    prefs.putBoolean("music", false);
                }
                prefs.flush();
                return true;
            }

        });
        table.add(MusicOn).colspan( 2 ).left().padRight(10f).size(sizeX,sizeY);;
        table.add(MusicOff).colspan( 2 ).left().padRight(5f).size(sizeX,sizeY);;
    }

    private void createButtonSound(){
        Label title = createLabel("Sound:");
        table.row().padTop(20f);
        table.add(title).padRight(5f);

        ImageTextButton.ImageTextButtonStyle SoundOnStyle;
        if (!prefs.getBoolean("sound")){
            SoundOnStyle = createButtonStyle(selectionColor);

        }else {
            SoundOnStyle = createButtonStyle(selectionColorPressed);

        }

        ImageTextButton.ImageTextButtonStyle SoundOffStyle;
        if (prefs.getBoolean("sound")) {
            SoundOffStyle = createButtonStyle(selectionColor);
        }else {
            SoundOffStyle = createButtonStyle(selectionColorPressed);
        }
        SoundOn = createButton("On", SoundOnStyle);
        SoundOff = createButton("Off", SoundOffStyle);

        SoundOn.addListener(new ClickListener() {


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(SoundOn.isPressed() && !prefs.getBoolean("sound")){
                    SoundOn.setStyle(createButtonStyle(selectionColorPressed));
                    SoundOff.setStyle(createButtonStyle(selectionColor));

                    prefs.putBoolean("sound", true);
                }
                prefs.flush();
                return true;
            }


        });

        SoundOff.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(SoundOff.isPressed() && prefs.getBoolean("sound")){
                    SoundOn.setStyle(createButtonStyle(selectionColor));
                    SoundOff.setStyle(createButtonStyle(selectionColorPressed));
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }

                    prefs.putBoolean("sound", false);
                }
                prefs.flush();
                return true;
            }

        });
        table.add(SoundOn).colspan( 2 ).left().padRight(2f).size(sizeX,sizeY);
        table.add(SoundOff).colspan( 2 ).left().size(sizeX,sizeY);
    }

    private void createButtonSize(){
        Label title = createLabel("Collector Size:");
        table.row().padTop(20f);
        table.add(title).padRight(5f);


        ImageTextButton.ImageTextButtonStyle NormalStyle;


        if (!prefs.getBoolean("normal")){
            NormalStyle = createButtonStyle(selectionColor);

        }else {
            NormalStyle = createButtonStyle(selectionColorPressed);

        }

        ImageTextButton.ImageTextButtonStyle BigStyle;
        if (!prefs.getBoolean("big")) {
            BigStyle = createButtonStyle(selectionColor);
        }else {
            BigStyle = createButtonStyle(selectionColorPressed);
        }
        ImageTextButton.ImageTextButtonStyle VBigStyle;
        if (!prefs.getBoolean("vbig")) {
            VBigStyle = createButtonStyle(selectionColor);
        }else {
            VBigStyle = createButtonStyle(selectionColorPressed);
        }
        NormalSize = createButton("Normal",NormalStyle);
        BigSize = createButton("Big", BigStyle);
        VBigSize = createButton("Very Big",VBigStyle);

        NormalSize.addListener(new ClickListener() {


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(NormalSize.isPressed() && !prefs.getBoolean("normal")){
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    NormalSize.setStyle(createButtonStyle(selectionColorPressed));
                    BigSize.setStyle(createButtonStyle(selectionColor));
                    VBigSize.setStyle(createButtonStyle(selectionColor));


                    prefs.putBoolean("normal", true);
                    prefs.putBoolean("big", false);
                    prefs.putBoolean("vbig", false);
                }
                prefs.flush();
                return true;
            }


        });

        BigSize.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(BigSize.isPressed() && !prefs.getBoolean("big")){
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    NormalSize.setStyle(createButtonStyle(selectionColor));
                    BigSize.setStyle(createButtonStyle(selectionColorPressed));
                    VBigSize.setStyle(createButtonStyle(selectionColor));


                    prefs.putBoolean("normal", false);
                    prefs.putBoolean("big", true);
                    prefs.putBoolean("vbig", false);
                }
                prefs.flush();
                return true;
            }

        });
        VBigSize.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (prefs.getBoolean("sound")) {
                    noteCollector.getClick().play();
                }
                if(VBigSize.isPressed() && !prefs.getBoolean("vbig")){
                    NormalSize.setStyle(createButtonStyle(selectionColor));
                    BigSize.setStyle(createButtonStyle(selectionColor));
                    VBigSize.setStyle(createButtonStyle(selectionColorPressed));


                    prefs.putBoolean("normal", false);
                    prefs.putBoolean("big", false);
                    prefs.putBoolean("vbig", true);
                }
                prefs.flush();
                return true;
            }

        });
        table.add(NormalSize).padRight(0.5f).size(sizeX,sizeY);
        table.add(BigSize).padRight(0.7f).size(sizeX,sizeY);
        table.add(VBigSize).size(sizeX,sizeY);
    }

    private void createControlTypes()//Creates the buttons for controller option (Touch/Dpad)
    {
        Label title = createLabel("Control Type:");
        table.row().padTop(20f);
        table.add(title).padRight(5f);


        ImageTextButton.ImageTextButtonStyle TouchStyle;
        ImageTextButton.ImageTextButtonStyle PadStyle;


        if (!prefs.getBoolean("touch")){
            TouchStyle = createButtonStyle(selectionColor);
        }
        else
         {
            TouchStyle = createButtonStyle(selectionColorPressed);

        }
        if (!prefs.getBoolean("dpad")) {
            PadStyle = createButtonStyle(selectionColor);
        }else {
            PadStyle = createButtonStyle(selectionColorPressed);
        }
        touch = createButton("Touch",TouchStyle);
        pad = createButton("Dpad", PadStyle);

        touch.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                if(touch.isPressed() && !prefs.getBoolean("touch")){
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    touch.setStyle(createButtonStyle(selectionColorPressed));
                    pad.setStyle(createButtonStyle(selectionColor));


                    prefs.putBoolean("touch", true);
                    prefs.putBoolean("pad", false);
                }
                prefs.flush();
                return true;
            }
        });

        pad.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                if(pad.isPressed() && !prefs.getBoolean("pad")){
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    pad.setStyle(createButtonStyle(selectionColorPressed));
                    touch.setStyle(createButtonStyle(selectionColor));


                    prefs.putBoolean("touch", false);
                    prefs.putBoolean("pad", true);
                }
                prefs.flush();
                return true;
            }
        });
        table.add(touch).padRight(0.5f).size(sizeX,sizeY);
        table.add(pad).padRight(0.7f).size(sizeX,sizeY);
    }
    private void createCustomBg()//Creates the buttons for the custom backgrounds
    {
        Label title = createLabel("Custom Backgrounds:");
        table.row().padTop(20f);
        table.add(title).padRight(5f);
        ImageTextButton.ImageTextButtonStyle NormalStyle;
        NormalStyle = createButtonStyle(selectionColor);
        menuBg = createButton("Menu", NormalStyle);
        gameBg = createButton("In-Game", NormalStyle);

        menuBg.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                System.out.println("Calling interface");
                Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        System.out.println(this.isInterrupted());
                        gallery=noteCollector.getGallery();
                        gallery.getImagePath();
                        res="";
                        while (!interrupted())
                        {
                            res=gallery.getSelectedFilePath();
                            if(res!=null)
                            {
                                System.out.println("Selected img: "+res);
                                //FileHandle from = Gdx.files.external(res);//Get the path
                                FileHandle from = Gdx.files.external(res);//Get the path
                                File f= new File(res);//Get the file from supplied uri
                                //System.out.println("Got the Path 2 "+f.getPath());

                                byte[] data = new byte[625000];//5MByte array for image

                                try
                                {
                                    data = readFileToByteArray(f);//Read image as bytes
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                final File img = new File(Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").file().getAbsolutePath());//Destination file
                                String a=Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").file().getAbsolutePath();
                                String b=Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").file().toString();
                                String c=Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").toString();
                                System.out.println("The abspath is "+a);
                                System.out.println("path is "+b);
                                System.out.println("str is "+c);
                                try
                                {
                                    FileUtils.writeByteArrayToFile(img,data);//Write file from byte array
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                System.out.println("Setting Background Image");
                                Constants.setBackgroundMenu("Note Collector/Custom Images/menu_image.jpg");
                                System.out.println(Constants.getBackgroundMenu());
                                gallery.clearSelectedPath();
                                this.interrupt();
                            }
                        }
                    }
                };

                t.start();

                return true;
            }


        });
        gameBg.addListener(new ClickListener()
        {


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                System.out.println("Calling interface");
                Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        gallery=noteCollector.getGallery();
                        gallery.getImagePath();
                        String res;
                        while (!interrupted())
                        {
                            res=gallery.getSelectedFilePath();

                            if(res!=null)
                            {
                                System.out.println("Selected img: "+res);
                                //FileHandle from = Gdx.files.external(res);//Get the path
                                FileHandle from = Gdx.files.external(res);//Get the path
                                File f= new File(res);//Get the file from supplied uri
                                //System.out.println("Got the Path 2 "+f.getPath());

                                byte[] data = new byte[625000];//5MByte array for image
                                try
                                {
                                    data = readFileToByteArray(f);//Read image as bytes
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                final File img = new File(Gdx.files.external("Note Collector/Custom Images/game_image.jpg").file().getAbsolutePath());//Destination file
                                String a=Gdx.files.external("Note Collector/Custom Images/game_image.jpg").file().getAbsolutePath();
                                String b=Gdx.files.external("Note Collector/Custom Images/game_image.jpg").file().toString();
                                String c=Gdx.files.external("Note Collector/Custom Images/game_image.pg").toString();
                                System.out.println("The abspath is "+a);
                                System.out.println("path is "+b);
                                System.out.println("str is "+c);
                                try
                                {
                                    FileUtils.writeByteArrayToFile(img,data);//Write file from byte array
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                System.out.println("Setting Background Image");
                                Constants.setBackgroundGame("Note Collector/Custom Images/game_image.jpg");
                                System.out.println(Constants.getBackgroundGame());
                                gallery.clearSelectedPath();
                                this.interrupt();
                            }
                        }
                    }
                };

                t.start();

                return true;
            }


        });
        table.add(menuBg).padRight(0.5f).size(sizeX,sizeY);
        table.add(gameBg).padRight(0.7f).size(sizeX,sizeY);
    }

    private void AddButtonListener(final ImageTextButton MenuButton) {
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    table.addAction(Actions.fadeOut(0.4f));
                    exitBtnTable.addAction(Actions.fadeOut(0.4f));
                    Timer.schedule(new Timer.Task() {

                        @Override
                        public void run() {
                            noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));

                        }

                    }, 0.4f);

                }
                return true;
            }

        });
    }
    private void createBackButton(String text)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton);
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);
    }

    private void createTable(){
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.padTop(10f);
        // table.pad(0f,100f,0f,0f);*/
        //table.setTouchable(Touchable.enabled);
    }
    private void setupCamera(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }
    private void LoadAssets(){
        assetsManager.LoadAssets();
        font=assetsManager.createBitmapFont();
        //font = assetsManager.createBimapFont(45);
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);

    }

    private void createLogo(){
        Texture img = assetsManager.assetManager.get(Constants.logo);
        Image logo = new Image(img);
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        assetsManager.setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
       // addVerticalGroup(background);
    }
    private void createBackground()
    {
        System.out.println("Width:"+VIEWPORT_WIDTH+" Height:"+VIEWPORT_HEIGHT);
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background=assetsManager.scaleBackground(file);
        stage.addActor(background);
    }


    private ImageTextButton createButton(String text,ImageTextButton.ImageTextButtonStyle textButtonStyle )
    {
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        return MenuButton;

    }


    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage){

        ImageTextButton.ImageTextButtonStyle textButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        textButtonStyle.up = ButtonImage;
        textButtonStyle.down = selectionColorPressed;
        textButtonStyle.over = ButtonImage;
        textButtonStyle.font = font;
        return textButtonStyle;
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
    public void dispose() {
        font.dispose();
        //stage.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);
        table.clear();
    }
}
