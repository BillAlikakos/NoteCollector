package com.mygdx.notecollector.screens.menu.UserArea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.IAuthUser;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.Score;
import com.mygdx.notecollector.Utils.ScoreClass;
import com.mygdx.notecollector.Utils.scoreObj;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;

import java.util.ArrayList;

public class SignUpScreen implements Screen
{
    private Assets AssetsManager;
    private NoteCollector notecollector;
    private Stage stage;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;


    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private BitmapFont font;
    private Label error;
    private VerticalGroup verticalGroup;
    private VerticalGroup verticalGroupLogo;
    private Table exitBtnTable;
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Table table;
    private IAuthUser user;
    private TextField userName;
    private TextField email;
    private TextField password;
    private TextureRegionDrawable selectionColorList;
    private boolean touched;
    private ScoreClass score;
    private TextureRegionDrawable showPasswordBtn;
    private TextureRegionDrawable showPasswordBtnP;

    public SignUpScreen(NoteCollector notecollector, Stage stage)
    {
        this.notecollector = notecollector;
        user=notecollector.getAuth();
        this.stage=stage;
        table=new Table();
        table.setFillParent(true);
        this.score=new ScoreClass(false,"","","","","");
        //score = new Score();
        //scores = score.getScore();
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();
        error=createLabel("");
    }
    public SignUpScreen(NoteCollector notecollector, Stage stage, ScoreClass score)
    {
        this.notecollector = notecollector;
        user=notecollector.getAuth();
        this.stage=stage;
        table=new Table();
        table.setFillParent(true);
        this.score=score;
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();
        error=createLabel("");

    }
    @Override
    public void show()
    {
        createVerticalGroup();
        createLogo();
        //user.checkUser();
        exitBtnTable=new Table();
        size=AssetsManager.setButtonSize(sizeX,sizeY);//Get the dimensions for the button
        sizeX=size[0];
        sizeY=size[1];
        createForm();
        //printScoreList();
        createButton("Back",sizeX,sizeY);
        stage.addActor(verticalGroup);
        stage.addActor(exitBtnTable);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        table.getColor().a=0;
        exitBtnTable.getColor().a=0;
        table.addAction(Actions.fadeIn(0.2f));
        exitBtnTable.addAction(Actions.fadeIn(0.2f));
    }

    private void createForm()
    {
        userName = new TextField("", createTextFieldStyle());
        userName.setMessageText("Username");
        userName.setHeight(100*VIEWPORT_HEIGHT/1080);
        userName.setWidth(360*VIEWPORT_WIDTH/1920);
        float Xaxis=(stage.getCamera().viewportWidth - 360*VIEWPORT_WIDTH/1920) / 2;
        float Yaxis=(stage.getCamera().viewportHeight)/2+ 200*VIEWPORT_HEIGHT/1080;
        userName.setPosition(Xaxis, Yaxis);

        addListener(userName);
        table.addActor(userName);
        email = new TextField("", createTextFieldStyle());
        email.setMessageText("Email");
        email.setHeight(100*VIEWPORT_HEIGHT/1080);
        email.setWidth(360*VIEWPORT_WIDTH/1920);
        //Xaxis=(stage.getCamera().viewportWidth - 360*VIEWPORT_WIDTH/1920) / 2;
        Yaxis=(stage.getCamera().viewportHeight)/2+ 100*VIEWPORT_HEIGHT/1080;
        email.setPosition(Xaxis, Yaxis);

        addListener(email);
        table.addActor(email);
        password = new TextField("", createTextFieldStyle());
        password.setMessageText("Password");
        password.setPasswordMode(true);
        password.setHeight(100*VIEWPORT_HEIGHT/1080);
        password.setWidth(360*VIEWPORT_WIDTH/1920);
        Yaxis=(stage.getCamera().viewportHeight)/2-VIEWPORT_HEIGHT/1080;
        password.setPosition(Xaxis, Yaxis);
        final ImageButton showPassword = new ImageButton(showPasswordBtn);
        showPassword.getStyle().down=showPasswordBtnP;
        showPassword.addListener((new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
                if(showPassword.isPressed())
                {
                    if (prefs.getBoolean("sound"))
                    {
                        notecollector.getClick().play();
                    }
                    if(password.isPasswordMode())
                    {
                        password.setPasswordMode(false);
                    }
                    else
                    {
                        password.setPasswordMode(true);
                    }
                }
                return true;
            }
        }));

        showPassword.setSize(VIEWPORT_WIDTH*0.06f,VIEWPORT_HEIGHT*0.10f);
        showPassword.setPosition(password.getX()+password.getWidth()/2+(1.75f*showPassword.getWidth()), password.getY());
        table.addActor(showPassword);
        addListener(password);
        table.addActor(password);
        table.row();
        Yaxis=(stage.getCamera().viewportHeight)/2-(sizeY);
        createButton("Submit",Xaxis,Yaxis,sizeX,sizeY);
    }

    private void createButton(String text, float Xaxis, float y, float sizeX, float sizeY) {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton, text);
        MenuButton.setPosition(Xaxis, y);
        MenuButton.setSize(sizeX, sizeY);
        //table.add(MenuButton).size(sizeX, sizeY);
        table.addActor(MenuButton);
    }
    private void addListener(TextField textField) {
        //if textbox is clicked, show the keyboard
        textField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                if (!touched)
                    touched = true;
                Gdx.input.setOnscreenKeyboardVisible(true);


            }
        });
    }
    private TextField.TextFieldStyle createTextFieldStyle()//Method to create the style for the submission text field
    {
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        selectionColorList = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.SelectionColor, Texture.class)));
        textFieldStyle.background = selectionColorList;
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.messageFont = font;
        textFieldStyle.messageFontColor = Color.WHITE;

        return textFieldStyle;
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
        AssetsManager.disposeLogInAssets();
        AssetsManager.disposeListMenuAssets();
        font.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);


    }

    private Label createLabel(String text)
    {
        //BitmapFont font = AssetsManager.createBimapFont(size);
        BitmapFont font = AssetsManager.createFontH();
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        return new Label(text, labelstyle);

    }

    private void createVerticalGroup()
    {
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center().padTop(300f*VIEWPORT_HEIGHT/1080).space(10f);
    }

    private  void addVerticalGroup(Actor actor){
        verticalGroup.addActor(actor);

    }
    private void LoadAssets()
    {
        AssetsManager.LoadAssets();
        AssetsManager.LoadListAssets();
        font=AssetsManager.createBitmapFont();
        AssetsManager.LoadLogInAssets();
        showPasswordBtn=new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.showPasswordImage,Texture.class)));
        showPasswordBtnP=new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.showPasswordImageP,Texture.class)));
        // font = AssetsManager.createBimapFont(45);
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
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
    private void createButton(String text,float sizeX,float sizeY)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);
    }

    private void AddButtonListener(final ImageTextButton MenuButton,final String text){
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        notecollector.getClick().play();
                    }
                    switch (text)
                    {
                        case "Back":
                            //notecollector.adsHandler.showAds(1);
                            table.addAction(Actions.fadeOut(0.4f));
                            exitBtnTable.addAction(Actions.fadeOut(0.4f));
                            if(!error.getText().equals(""))
                            {
                                error.addAction(Actions.fadeOut(0.4f));
                            }
                            Timer.schedule(new Timer.Task() {

                                @Override
                                public void run() {
                                    dispose();
                                    notecollector.setScreen(new SocialSplashScreen(notecollector,stage));

                                }

                            }, 0.4f);
                        break;
                        case "Submit":

                            if(userName.getText().equals("") || email.getText().equals("") || password.getText().equals("") || !email.getText().contains("@") )
                            {
                                error.setText("Please fill out the form");
                                table.add(error).bottom().padTop(sizeY+225f*VIEWPORT_HEIGHT/1080);
                                error.getColor().a=0;
                                error.addAction(Actions.fadeIn(0.2f));

                                //error.addAction(Actions.fadeOut(0.4f));
                            }
                            else if( password.getText().length()<6 )
                            {
                                error.setText("Password must be longer than 6 characters");
                                table.add(error).bottom().padTop(sizeY+225f*VIEWPORT_HEIGHT/1080);
                                error.addAction(Actions.fadeIn(0.2f));
                                //error.addAction(Actions.fadeOut(0.4f));
                            }
                            else
                            {
                                if(!error.getText().equals(""))
                                {
                                    error.addAction(Actions.fadeOut(0.4f));
                                }
                                user.createAccount(userName.getText(),email.getText().trim(),password.getText(),notecollector,stage,score);
                            }

                    }

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
