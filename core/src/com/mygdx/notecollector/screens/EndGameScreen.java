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

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.ScoreClass;
import com.mygdx.notecollector.screens.menu.DialogScore;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;
import com.mygdx.notecollector.screens.menu.UserArea.ScoresScreen;
import com.mygdx.notecollector.screens.menu.UserArea.SocialSplashScreen;

/**
 * Created by bill on 7/22/16.
 */
public class EndGameScreen implements Screen {


    private Assets AssetsManager;
    private NoteCollector notecollector;
    private Stage stage;
    private Viewport viewport;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Table table;
    private VerticalGroup verticalGroup;

    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private BitmapFont font;
    private String Score;
    private String Score2;
    private String Difficulty;
    private String gameMode;
    private float[] size;
    private float sizeX;
    private float sizeY;
    private boolean isHost;
    private boolean isGuest;
    private ServerClass srv;
    private ClientClass c;
    private Thread t;
    private ScoreClass score;

    public EndGameScreen(NoteCollector notecollector,String Score,String Difficulty,Stage stage,String gameMode) {
        this.notecollector = notecollector;
        this.stage=stage;
        this.Score = Score;
        this.Difficulty=Difficulty;
        this.gameMode=gameMode;
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();
    }

    public EndGameScreen(NoteCollector notecollector, String Score, String Difficulty, ServerClass srv,Stage stage,String gameMode)
    {
        this.notecollector = notecollector;
        this.stage=stage;
        this.Score = Score;
        this.Difficulty=Difficulty;
        this.gameMode=gameMode;
        this.srv=srv;
        isHost=true;
        isGuest=false;
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();
    }

    public EndGameScreen(NoteCollector notecollector, String Score, String Difficulty, ClientClass c,Stage stage,String gameMode)
    {
        this.notecollector = notecollector;
        this.stage=stage;
        this.Score = Score;
        this.Difficulty=Difficulty;
        this.gameMode=gameMode;
        this.c=c;
        AssetsManager = notecollector.getAssetsManager();
        isHost=false;
        isGuest=true;
        LoadAssets();
    }
    @Override
    public void show()
    {

        size=AssetsManager.setButtonSize(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];
        createTable();
        createLogo();
        createLabels();
        if(isGuest || isHost)
        {
            sendMessage();
            addScoreListener();
        }
        createButtons();
        /*createTable();
        createLogo();
        createLabels();
        createButtons();*/
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        table.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in
    }

    private void sendMessage()//Start threads to send the player's score to the second
    {
        if(isHost)
        {
            t=new Thread()
            {
                @Override
                public void run()
                {
                    while(!t.isInterrupted())
                    {
                        //System.out.println("Sending score");
                        srv.sendScore(Score);
                    }
                    if(Score2!=null)
                    {
                        interrupt();
                    }
                }
            };
            t.start();
        }
        else if (isGuest)
        {
            t=new Thread()
            {
                @Override
                public void run()
                {
                    while(!t.isInterrupted())
                    {
                        //System.out.println("Sending score");
                        c.sendScore(Score);
                        if(Score2!=null)
                        {
                            interrupt();
                        }
                    }

                }
            };
            t.start();
        }
    }

    private void addScoreListener()//Add listeners for host/guest to receive score from each other
    {
        if(isHost)
        {
            srv.getServer().addListener(new Listener() //Moved to serverClass
            {

                public void received (Connection connection, Object object)
                {
                 if (object instanceof ServerClass.scoreObj)
                    {
                     //System.out.println("Host received");
                        ServerClass.scoreObj request = (ServerClass.scoreObj)object;
                        System.out.println("Client score: "+request.score);
                        Score2=request.score;
                        System.out.println("Score2: "+Score2);
                        srv.getServer().removeListener(this);
                        float Yaxis=stage.getCamera().viewportHeight/2-30;
                        if(isGuest || isHost)
                        {
                            //createLabel("Opponent's Score:"+Score2,stage.getCamera().viewportHeight/2);
                            createLabel("Opponent's Score:"+Score2,Yaxis*VIEWPORT_HEIGHT/1080);
                        }
                        //t.interrupt();
                    }

                }
            }  );
        }
        else if(isGuest)
        {
         c.getClient().addListener(new Listener()
            {
             public void received (Connection connection, Object object)
             {
                 if (object instanceof ClientClass.scoreObj)
                 {
                    //System.out.println("Host received");
                    ClientClass.scoreObj request = (ClientClass.scoreObj)object;
                    System.out.println("Host score: "+request.score);
                    Score2=request.score;
                    c.getClient().removeListener(this);
                    float Yaxis=stage.getCamera().viewportHeight/2-30;
                     if(isGuest || isHost)
                     {
                         createLabel("Opponent's Score:"+Score2,Yaxis*VIEWPORT_HEIGHT/1080);
                     }
                    //t.interrupt();
                 }

            }
            });
    }

}

    private void createButtons(){
        float Xaxis =(stage.getCamera().viewportWidth/2)-160f;
        float Yaxis=(stage.getCamera().viewportHeight-52)/2;
        createButton("Continue",Xaxis*VIEWPORT_WIDTH/480,Yaxis*VIEWPORT_HEIGHT/480);
        Xaxis =Xaxis+195f*VIEWPORT_WIDTH/480;
        createButton("Submit",Xaxis,Yaxis*VIEWPORT_HEIGHT/480);
    }
    private void createLabels(){
        //createLabel("Game finished!",stage.getCamera().viewportHeight/2+100);
        float Yaxis1=stage.getCamera().viewportHeight/2+75;
        float Yaxis2=stage.getCamera().viewportHeight/2+30;
        createLabel("Game finished!",Yaxis1*VIEWPORT_HEIGHT/1080);
        createLabel("Your Score:"+Score,Yaxis2*VIEWPORT_HEIGHT/1080);
        /*if(isGuest || isHost)
        {
            createLabel("Opponent's Score:"+Score2,stage.getCamera().viewportHeight/2);
        }*/

    }
    @Override
    public void render(float delta)
    {
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
    public void dispose()
    {
        AssetsManager.disposeMenuAssets();
        if(isHost || isGuest)
        {
            if(t.isAlive())
            {
                t.interrupt();
            }
        }
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        font.dispose();
    }



    private void createLabel(String text,float Yaxis){
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelstyle);
        //label.setPosition((stage.getCamera().viewportWidth-label.getWidth())/2,Yaxis);
        label.setPosition((stage.getCamera().viewportWidth)/2-label.getWidth(),Yaxis);
        //stage.addActor(label);
       // table.center();
        table.center().add(label);
        table.row();
        label.addAction(Actions.fadeIn(0.2f));
    }
    //create a table to organize buttons and labels
    private void createTable(){
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(10f,10f,30f,10f);

        //table.setTouchable(Touchable.enabled);
    }
    private void LoadAssets(){
        //font = AssetsManager.createBimapFont(45);
        AssetsManager.LoadAssets();
        font = AssetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(7f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(7f);
        selectionColorPressed.setBottomHeight(2f);


    }
    private void createLogo()
    {
        Image logo=notecollector.getAssetsManager().scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        notecollector.getAssetsManager().setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
    }

    private void createButton(String text,float Xaxis,float Yaxis){

        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        System.out.println(MenuButton.getWidth());
        MenuButton.setPosition(Xaxis,Yaxis);
        //MenuButton.setSize(sizeX,sizeY);
        table.add(MenuButton).size(sizeX,sizeY);

        //stage.addActor(MenuButton);
        MenuButton.addAction(Actions.fadeIn(0.2f));
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
                    table.addAction(Actions.fadeOut(0.4f));
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if(isHost)
                            {
                                srv.closeServer();
                            }
                            else if(isGuest)
                            {
                                c.closeClient();
                            }
                            if (text.equals("Submit"))
                            {
                                dispose();
                                System.out.println(notecollector.getAuth().isConnected());
                                if(!notecollector.getAuth().isConnected())
                                {
                                    //Redirect to scores screen
                                    notecollector.setScreen(new SocialSplashScreen(notecollector, stage,Score,AssetsManager.MusicName,Difficulty,gameMode));
                                }
                                else
                                {
                                    //Submit score to db and redirect to scores screen
                                    System.out.println(AssetsManager.MusicName+" "+Score+" "+Difficulty+" "+notecollector.getAuth().getUserName());
                                    score=new ScoreClass(AssetsManager.MusicName,Score,Difficulty,notecollector.getAuth().getUserName(),gameMode);
                                    notecollector.getDb().write(notecollector.getAuth(),score);
                                    notecollector.setScreen(new ScoresScreen(notecollector,stage));
                                }
                                //notecollector.setScreen(new DialogScore(Score, notecollector,AssetsManager.MusicName,Difficulty,stage,gameMode));//Old
                            }
                            else
                            {
                               dispose();
                               System.out.println("Exiting to main menu");
                               notecollector.setScreen(new MainMenuScreen(notecollector,stage));
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

}
