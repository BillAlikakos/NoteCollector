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
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.notecollector.IAuthUser;
import com.mygdx.notecollector.IGoogleLogin;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;


public class ResultScreen implements Screen
{
    private Assets AssetsManager;
    private NoteCollector notecollector;
    private Stage stage;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;


    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private ImageTextButton HighScores;
    private ImageTextButton LogOut;
   //private BitmapFont font;
    private VerticalGroup verticalGroup;
    private Table exitBtnTable;
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Table table;
    private IAuthUser user;
    private TextureRegionDrawable selectionColorList;
    private boolean touched;
    private Label label;
    private String name;

    public ResultScreen(NoteCollector notecollector, Stage stage)
    {
        this.notecollector = notecollector;
        user=notecollector.getAuth();
        this.stage=stage;
        table=new Table();
        table.setFillParent(true);
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();
    }

    @Override
    public void show()
    {
        createVerticalGroup();
        createLogo();
        exitBtnTable = new Table();
        size = AssetsManager.setButtonSize(sizeX, sizeY);//Get the dimensions for the button
        sizeX = size[0];
        sizeY = size[1];
        createLabel("Welcome, "+user.getUserName());
        name=user.getUserName();
        HighScores=createMenuButton("High Scores",sizeX,sizeY);
        LogOut=createMenuButton("Log Out",sizeX,sizeY);

        createButton("Menu",sizeX,sizeY);
        stage.addActor(verticalGroup);
        stage.addActor(exitBtnTable);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        table.getColor().a=0;
        exitBtnTable.getColor().a=0;
        table.addAction(Actions.fadeIn(0.2f));
        exitBtnTable.addAction(Actions.fadeIn(0.2f));

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
    private ImageTextButton createMenuButton(String text, float sizeX, float sizeY)
    {
        /*ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton, text);
        MenuButton.setSize(sizeX, sizeY);

        //table.add(MenuButton).size(sizeX, sizeY);
        table.center().addActor(MenuButton);
        table.row();*/
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        MenuButton.setColor(Color.WHITE);
        AddButtonListener(MenuButton,text);
        table.center().add(MenuButton).size(sizeX,sizeY);
        table.row();
        return MenuButton;
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
        textFieldStyle.font = AssetsManager.getFont();
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.messageFont = AssetsManager.getFont();
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
        //font.dispose();
        AssetsManager.disposeMenuAssets();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);


    }

    private void createLabel(String text)
    {
        //BitmapFont font = AssetsManager.createBimapFont(size);
        /*BitmapFont font = AssetsManager.createFontH();
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelstyle);
        label.setPosition(VIEWPORT_WIDTH/2-label.getWidth()/2,VIEWPORT_HEIGHT/2);*/
        //BitmapFont font = AssetsManager.createFontH();
        Label.LabelStyle labelstyle = new Label.LabelStyle(AssetsManager.getFontH(), Color.WHITE);
        label = new Label(text, labelstyle);
        //label.setPosition((stage.getCamera().viewportWidth-label.getWidth())/2,Yaxis);
        //stage.addActor(label);

        table.add(label);
        table.row();
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
        //AssetsManager.LoadListAssets();
        AssetsManager.LoadAssets();
        //font=AssetsManager.createBitmapFont();
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
                    MenuButton.removeListener(this);
                    switch (text)
                    {
                        case "Menu":
                            //notecollector.adsHandler.showAds(1);
                            table.addAction(Actions.fadeOut(0.4f));
                            exitBtnTable.addAction(Actions.fadeOut(0.4f));
                            Timer.schedule(new Timer.Task() {

                                @Override
                                public void run() {
                                    dispose();
                                    notecollector.setScreen(new MainMenuScreen(notecollector,stage));

                                }

                            }, 0.4f);
                            break;
                        case "Log Out":
                            MenuButton.addAction(Actions.fadeOut(0.2f));
                            HighScores.addAction(Actions.fadeOut(0.2f));
                            //label.addAction(Actions.fadeOut(0.2f));
                            user.logOut();
                            label.setText(("User "+name+" signed out successfully !"));
                            label.setPosition(VIEWPORT_WIDTH/2-label.getWidth(),VIEWPORT_HEIGHT/2);
                            label.addAction(Actions.fadeIn(0.2f));
                            Timer.schedule(new Timer.Task() {

                                @Override
                                public void run() {
                                    dispose();
                                    notecollector.setScreen(new SocialSplashScreen(notecollector,stage));

                                }

                            }, 0.7f);
                            break;
                        case "High Scores":
                            /*table.addAction(Actions.fadeOut(0.4f));
                            exitBtnTable.addAction(Actions.fadeOut(0.4f));
                            notecollector.getDb().read(notecollector.getAuth(),notecollector,stage);*/

                            table.addAction(Actions.fadeOut(0.4f));
                            exitBtnTable.addAction(Actions.fadeOut(0.4f));
                            //user.logOut();
                            Timer.schedule(new Timer.Task() {

                                @Override
                                public void run() {
                                    dispose();
                                    notecollector.setScreen(new ScoresScreen(notecollector,stage));

                                }

                            }, 0.4f);
                            break;
                            //user.logOut();
                            //Screen changes on callback (from android DataBase class
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
        textButtonStyle.font = AssetsManager.getFont();
        return textButtonStyle;
    }
}