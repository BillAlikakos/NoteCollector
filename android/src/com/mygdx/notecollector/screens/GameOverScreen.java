package com.mygdx.notecollector.screens;

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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.menu.LoadingScreen;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;

/**
 * Created by bill on 7/4/16.
 */
public class GameOverScreen implements Screen {


    private Assets AssetsManager;
    private NoteCollector notecollector;
    private Stage stage;
    private Viewport viewport;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Table table;


    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private BitmapFont font;
    private String Score;
    private String filepath;
    private VerticalGroup verticalGroup;
    private int[] size;
    private int sizeX;
    private int sizeY;

    private long delay;
    private int speed;
    private ClientClass c;
    private ServerClass srv;
    private boolean isHost;
    private boolean isGuest;
    private boolean mode;

    public GameOverScreen(NoteCollector notecollector,String Score,String filepath,int speed,long delay,boolean mode) {
        this.notecollector = notecollector;
        this.Score = Score;
        this.filepath = filepath;
        this.speed = speed;
        this.delay = delay;
        this.mode=mode;
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();
    
    }

    public GameOverScreen(NoteCollector notecollector,String Score,String filepath,int speed,long delay,ServerClass srv,boolean mode)
    {
        this.isHost=true;
        this.isGuest=false;
        this.srv=srv;
        this.notecollector = notecollector;
        this.Score = Score;
        this.filepath = filepath;
        this.speed = speed;
        this.delay = delay;
        this.mode=mode;
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();

    }

    public GameOverScreen(NoteCollector notecollector,String Score,String filepath,int speed,long delay,ClientClass c,boolean mode)
    {
        this.isHost=false;
        this.isGuest=true;
        this.c=c;
        this.notecollector = notecollector;
        this.Score = Score;
        this.filepath = filepath;
        this.speed = speed;
        this.delay = delay;
        this.mode=mode;
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();

    }

    @Override
    public void show()
    {
        size=AssetsManager.setButtonDimensions(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];
        sendGameOverMsg();
        setupCamera();
        createTable();
        createBackground();
        createLogo();
        createButtons();
        createLabel("Your Score:"+Score);
        Gdx.input.setInputProcessor(stage);
    }

    private void sendGameOverMsg()//Notify the other player that host/guest has lost
    {
        if(isHost)
        {
            srv.sendGameOver(Score);
        }
        else if(isGuest)
        {
            c.sendGameOver(Score);
        }
    }

    private void createButtons()
    {
        if (isHost || isGuest)//Multiplayer doesn't need restart button
        {
           /* float Xaxis =(stage.getCamera().viewportWidth/2)-200f;

            if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
            {
                Xaxis =Xaxis-50f;
            }
            if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
            {
                Xaxis =Xaxis-100f;
            }
            createButton("Menu",Xaxis);*/
            float Xaxis =300f;//TODO Check new dimensions
            if (VIEWPORT_WIDTH == 800 && VIEWPORT_HEIGHT == 480)//old dimensions
            {
                Xaxis=305f;
            }
            if (VIEWPORT_WIDTH == 1080 && VIEWPORT_HEIGHT == 720)
            {
                Xaxis=410f;
            }
            if (VIEWPORT_WIDTH > 1080 && VIEWPORT_HEIGHT > 720)
            {
                Xaxis=(viewport.getScreenWidth() / 2) - sizeX / 2;
            }
            createButton("Menu",Xaxis);

        }
        else
        {
            float Xaxis =(stage.getCamera().viewportWidth/2)-200f;
            if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
            {
                Xaxis =Xaxis-50f;
            }
            if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
            {
                Xaxis =Xaxis-100f;
            }
            createButton("Restart",Xaxis);
            if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//old dimensions
            {
                Xaxis =Xaxis+200f;

            }
            if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
            {
                Xaxis =Xaxis+250f;
            }
            if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
            {
                Xaxis =Xaxis+310f;
            }

            createButton("Menu",Xaxis);
        }

    }

    @Override
    public void render(float delta) {


        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

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

    }

    private void createLabel(String text){
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        fileLabel.setPosition((stage.getCamera().viewportWidth-fileLabel.getWidth())/2,stage.getCamera().viewportHeight/2);
        stage.addActor(fileLabel);

    }
    private void createTable(){
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(10f,10f,30f,10f);
        table.setTouchable(Touchable.enabled);
    }
    private void LoadAssets(){
        AssetsManager .assetManager.load(Constants.GameOver, Texture.class);
        AssetsManager .assetManager.finishLoading();
        font = AssetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);


    }
    private void createLogo()
    {
        Texture img = AssetsManager.assetManager.get(Constants.GameOver);
        Image logo = new Image(img);
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        AssetsManager.setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
    }
    private void createBackground()
    {
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background=AssetsManager.scaleBackground(file);
        stage.addActor(background);

    }

    private void createButton(String text,float Xaxis)
    {


        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//old dimensions
        {
            MenuButton.setPosition(Xaxis,(stage.getCamera().viewportHeight-52)/2-15);

        }
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            MenuButton.setPosition(Xaxis,(stage.getCamera().viewportHeight-52)/2-60);
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            MenuButton.setPosition(Xaxis,(stage.getCamera().viewportHeight-52)/2-105);
        }
        System.out.println(MenuButton.getWidth());
        MenuButton.setSize(sizeX,sizeY);
        //MenuButton.setPosition(Xaxis,(stage.getCamera().viewportHeight -MenuButton.getHeight() )/4);

        stage.addActor(MenuButton);
    }

    private void AddButtonListener(final ImageTextButton MenuButton, final String text){
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        notecollector.getClick().play();
                    }
                    Timer.schedule(new Timer.Task() {


                        @Override
                        public void run() {
                            if(isHost)
                            {
                                srv.getServer().close();
                            }
                            else if(isGuest)
                            {
                                c.getClient().close();
                            }
                            if (text.equals("Menu"))
                                notecollector.setScreen(new MainMenuScreen(notecollector));
                            else
                                notecollector.setScreen(new LoadingScreen(notecollector, filepath, speed, delay,mode));


                        }

                    }, 0.4f);
                }
                return true;
            }

        });

    }

    //create the style of button
    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage){

        ImageTextButton.ImageTextButtonStyle textButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        textButtonStyle.up = ButtonImage;
        textButtonStyle.down = selectionColorPressed;
        textButtonStyle.over = ButtonImage;
        textButtonStyle.font = font;
        return textButtonStyle;
    }

    private void setupCamera(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }
}
