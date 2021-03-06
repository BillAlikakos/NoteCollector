package com.mygdx.notecollector.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.ScoreClass;
import com.mygdx.notecollector.screens.EndGameScreen;
import com.mygdx.notecollector.screens.menu.UserArea.ScoresScreen;
import com.mygdx.notecollector.screens.menu.UserArea.SocialSplashScreen;

/**
 * Created by bill on 7/25/16.
 */
public class DialogScore implements Screen {

    private Assets AssetsManager;
    private Stage stage;
    private Viewport viewport;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Table table;
    private  TextField textField;

    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private Label label;
    //private BitmapFont font;
    //private Score score;
    private String ScoreNumber;
    private String Difficulty;
    private String gameMode;
    private NoteCollector noteCollector;
    private TextureRegionDrawable selectionColorList;
    private boolean touched;
    private VerticalGroup verticalGroup;
    private String trackName;//String to hold the name of the song
    private float[] size;
    private float sizeX;
    private float sizeY;
    private ScoreClass score;

    public DialogScore(String ScoreNumber, NoteCollector noteCollector, String trackName,String Difficulty,Stage stage,String gameMode)
    {
        this.stage=stage;
        this.ScoreNumber = ScoreNumber;
        this.noteCollector = noteCollector;
        this.trackName=trackName;
        this.Difficulty=Difficulty;
        this.gameMode=gameMode;
        //score = new Score();
        touched=false;
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
    }
    @Override
    public void show()
    {
        createTable();
        createLogo();
        size=AssetsManager.setButtonSize(sizeX,sizeY);//Get the dimensions for the button
        sizeX=size[0];
        sizeY=size[1];
        createButton("Back",5f,10f,sizeX,sizeY);
        if(noteCollector.getAuth().isConnected())
        {
            /*createButton("Submit",VIEWPORT_WIDTH/2-sizeX/2,VIEWPORT_HEIGHT/2-sizeY,sizeX,sizeY);//Old score submitting method
            createLabel("Submit Score",stage.getCamera().viewportHeight/2+100);
            createTextField();*/
            createLabel("Score Submitted",stage.getCamera().viewportHeight/2+sizeY);
            try
            {
                wait(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            table.addAction(Actions.fadeOut(0.4f));
            label.addAction(Actions.fadeOut(0.4f));
            Timer.schedule(new Timer.Task() {
                @Override
                public void run()
                {
                    dispose();
                    //ASSEMBLE SCORE OBJ HERE
                    System.out.println(trackName+" "+ScoreNumber+" "+Difficulty+" "+noteCollector.getAuth().getUserName());
                    score=new ScoreClass(trackName,ScoreNumber,Difficulty,noteCollector.getAuth().getUserName(),gameMode);
                    noteCollector.getDb().write(noteCollector.getAuth(),score);
                    noteCollector.setScreen(new ScoresScreen(noteCollector,stage));
                    /*String name = textField.getText();
                    score.WriteScore(name, Integer.parseInt(ScoreNumber), trackName, Difficulty);
                    noteCollector.setScreen(new MainMenuScreen(noteCollector, stage));*/
            }  }, 0.4f);
        }
        else
        {
            createLabel("Please Log In to submit your high score",stage.getCamera().viewportHeight/2+sizeY);
            createButton("Log In",VIEWPORT_WIDTH/2-sizeX/2,VIEWPORT_HEIGHT/2-sizeY,sizeX,sizeY);
        }
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        label.getColor().a=0;
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in
    }

    private void addListener(){
        //if the textbox is clicked show the keyboard
        textField.addListener(new ClickListener(){
            public void clicked(InputEvent e, float x, float y) {
                if (!touched)
                    touched =true;
                Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });
    }
    private void createTextField(){
        textField = new TextField("",createTextFieldStyle());
        textField.setMessageText("Write a name");
        textField.setWidth(360*VIEWPORT_WIDTH/1920);
        textField.setPosition((stage.getCamera().viewportWidth-360*VIEWPORT_WIDTH/1920)/2,(stage.getCamera().viewportHeight)/2+50*VIEWPORT_WIDTH/1920);
        addListener();
        table.addActor(textField);
    }

    private TextField.TextFieldStyle createTextFieldStyle(){
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        selectionColorList = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.SelectionColor,Texture.class)));
        textFieldStyle.background = selectionColorList;
        textFieldStyle.font=AssetsManager.getFont();
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.messageFont =AssetsManager.getFont();
        textFieldStyle.messageFontColor = Color.WHITE;

        return textFieldStyle;
    }
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        // if touch screen is touched, hide keyboard
        if(Gdx.input.isTouched() && touched) {
            Gdx.input.setOnscreenKeyboardVisible(false);
            stage.unfocus(textField);
            touched =false;
        }

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
        AssetsManager.disposeListMenuAssets();
        //font.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
    }

    private void createLabel(String text,float Yaxis){
        Label.LabelStyle labelstyle = new Label.LabelStyle(AssetsManager.getFont(), Color.WHITE);
        label = new Label(text, labelstyle);
        label.setPosition((stage.getCamera().viewportWidth-label.getWidth())/2,Yaxis+50*VIEWPORT_HEIGHT/1080);
        stage.addActor(label);

    }
    private void createTable(){
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(10f,10f,30f,10f);//TODO : Change padding
        table.setTouchable(Touchable.enabled);
    }
    private void LoadAssets(){
        AssetsManager.LoadAssets();
        AssetsManager.LoadListAssets();
        //font = AssetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(7f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(7f);
        selectionColorPressed.setBottomHeight(2f);


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

    private void createButton(String text,float Xaxis,float y,float sizeX,float sizeY)
    {

        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        MenuButton.setPosition(Xaxis,y);
        MenuButton.setSize(sizeX,sizeY);
        table.add(MenuButton).size(sizeX,sizeY);
        table.addActor(MenuButton);
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
                    MenuButton.removeListener(this);
                    table.addAction(Actions.fadeOut(0.4f));
                    label.addAction(Actions.fadeOut(0.4f));
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run()
                        {
                            dispose();
                            switch (text)
                            {
                                case "Submit":
                                    //ASSEMBLE SCORE OBJ HERE
                                    System.out.println(trackName + " " + ScoreNumber + " " + Difficulty + " " + noteCollector.getAuth().getUserName());
                                    score = new ScoreClass(trackName, ScoreNumber, Difficulty, noteCollector.getAuth().getUserName(), gameMode);
                                    noteCollector.getDb().write(noteCollector.getAuth(), score);
                                    noteCollector.setScreen(new ScoresScreen(noteCollector, stage));
                                /*String name = textField.getText();
                                score.WriteScore(name, Integer.parseInt(ScoreNumber), trackName, Difficulty);
                                noteCollector.setScreen(new MainMenuScreen(noteCollector, stage));*/

                                    break;
                                case "Log In":
                                    noteCollector.setScreen(new SocialSplashScreen(noteCollector, stage, ScoreNumber, trackName, Difficulty, gameMode));
                                    break;
                                default:
                                    noteCollector.setScreen(new EndGameScreen(noteCollector, ScoreNumber, Difficulty, stage, gameMode));
                                    break;
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
        textButtonStyle.font = AssetsManager.getFont();
        return textButtonStyle;
    }
}
