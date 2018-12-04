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

public class LogInScreen implements Screen
{
    private Assets AssetsManager;
    private NoteCollector notecollector;
    private Stage stage;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private BitmapFont font;
    private VerticalGroup verticalGroup;
    private VerticalGroup verticalGroupLogo;
    private Table exitBtnTable;
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Table table;
    private IAuthUser user;
    private ScoreClass score;
    private Label error;
    private Label label;
    private TextureRegionDrawable showPasswordBtn;
    private TextureRegionDrawable showPasswordBtnP;

    public LogInScreen(NoteCollector notecollector, Stage stage)
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
    public LogInScreen(NoteCollector notecollector, Stage stage, ScoreClass score)
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
        table.add(createLabel("Log In with :"));
        table.row();
        //printScoreList();
        createButton("Email");
        createButton("Google Account");
        //createButton("Log in with Facebook");
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

    private void createButton(String text)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);

        AddButtonListener(MenuButton, text);
        table.add(MenuButton).size(sizeX, sizeY);

        table.row();
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
        AssetsManager.disposeMenuAssets();
        AssetsManager.disposeLogInAssets();
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
        Label label = new Label(text, labelstyle);
        return label;

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
       // AssetsManager.LoadListAssets();
        AssetsManager.LoadAssets();
        AssetsManager.LoadLogInAssets();
        font=AssetsManager.createBitmapFont();
        // font = AssetsManager.createBimapFont(45);
        showPasswordBtn=new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.showPasswordImage,Texture.class)));
        showPasswordBtnP=new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.showPasswordImageP,Texture.class)));
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
                    table.addAction(Actions.fadeOut(0.4f));
                    exitBtnTable.addAction(Actions.fadeOut(0.4f));
                    switch (text)
                    {
                        case "Back":
                            //notecollector.adsHandler.showAds(1);
                            Timer.schedule(new Timer.Task() {

                                @Override
                                public void run() {
                                    dispose();
                                    notecollector.setScreen(new SocialSplashScreen(notecollector,stage));

                                }

                            }, 0.4f);
                        break;
                        case "Email":
                            /*table.addAction(Actions.fadeOut(0.4f));
                            exitBtnTable.addAction(Actions.fadeOut(0.4f));*/
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    dispose();
                                    notecollector.setScreen(new LogInEmail(notecollector, stage, score));
                                }
                        }, 0.4f);
                        break;
                        case "Google Account":
                            //notecollector.getAuth().signInGoogleAccount();
                            notecollector.getGoogleLogin().login(notecollector,stage,score);
                            //dispose();
                        break;
                        case "Log in with Facebook":
                            dispose();
                        break;
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
