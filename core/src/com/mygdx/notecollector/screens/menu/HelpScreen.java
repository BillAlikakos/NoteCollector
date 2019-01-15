package com.mygdx.notecollector.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;


/**
 * Created by bill on 7/22/16.
 */
public class HelpScreen implements Screen {

    private NoteCollector noteCollector;
    private Assets assetsManager;
    private Stage stage;
    private VerticalGroup verticalGroup;
    private Viewport viewport;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    //private BitmapFont font;
    private Texture tutorialImage;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private Table exitBtnTable;
    private float[] size;
    private float sizeX;
    private float sizeY;
    private Table table;
    private boolean isTutorial=false;//Flag for tutorial image
    private boolean buttonPressed=false;//Flag for tutorial image
    private Image img;

    public HelpScreen(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        this.table=new Table();
        table.setFillParent(true);
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        //font = assetsManager.createBimapFont(25);


    }
    @Override
    public void show()
    {
        exitBtnTable=new Table();
        createVerticalGroup();
        createLogo();
        //img = assetsManager.assetManager.get(Constants.text);
        //background = new Image(img);
        size=assetsManager.setButtonSize(sizeX,sizeY);//Get the dimensions for the button
        sizeX=size[0];
        sizeY=size[1];
        table.padTop(100*VIEWPORT_HEIGHT/720);
        createButton("How To Play");
        createButton("Multiplayer");
        createButton("Track Search");
        createButton("User Accounts");
        stage.addActor(table);
        stage.addActor(verticalGroup);
        stage.addActor(exitBtnTable);
        createExitButton("Back");
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {

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
        //stage.getRoot().removeActor(background);
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);
        exitBtnTable.clear();
    }
    public void LoadAssets()
    {
        assetsManager.LoadAssets();
    }
    private void createButton(String text)
    {
        table.center();
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        table.add(MenuButton).size(sizeX,sizeY);
        table.row();
    }

    private void createExitButton(String text)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,"Back");
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);
    }
    private void AddButtonListener(final ImageTextButton MenuButton, final String text){
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
                if (MenuButton.isPressed() && !buttonPressed)
                {
                    buttonPressed=true;
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    MenuButton.removeListener(this);
                    switch (text)
                    {
                        case "How To Play"://TODO : Create the rest of the tutorials
                            isTutorial=true;
                            Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                            //table.addAction(Actions.fadeOut(0.4f));
                            displayImage(Constants.text);
                        }

                    }, 0.4f);
                        break;
                        case "Multiplayer":
                            isTutorial=true;
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    displayImage(Constants.multiplayerText);
                                }
                            }, 0.4f);
                        break;
                        case "Track Search":
                            isTutorial=true;
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    displayImage(Constants.trackSearchText);
                                }
                            }, 0.4f);
                            break;
                        case "User Accounts":
                            isTutorial=true;
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    //addAction(Actions.moveTo(posX, posY, 10)));
                                    /*table.addAction(Actions.fadeOut(0.4f));
                                    img = scaleImage(Constants.logInText);
                                    img.getColor().a = 0;
                                    img.addAction(Actions.fadeIn(0.4f));
                                    img.setPosition(VIEWPORT_WIDTH / 2 - img.getWidth() / 2, VIEWPORT_HEIGHT / 2 - img.getHeight() / 2);
                                    stage.addActor(img);*/
                                    displayImage(Constants.logInText);
                                }
                            }, 0.4f);
                            break;
                        case "Back":
                            if(isTutorial)
                            {
                                img.addAction(Actions.fadeOut(0.4f));
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        img.addAction(Actions.fadeOut(0.4f));
                                        stage.getRoot().removeActor(img);
                                        tutorialImage.dispose();
                                        //assetsManager.assetManager.unload(Constants.text);
                                        dispose();
                                        noteCollector.setScreen(new HelpScreen(noteCollector,stage));
                                    }

                                }, 0.4f);
                            }
                            else
                            {
                                table.addAction(Actions.fadeOut(0.4f));
                                exitBtnTable.addAction(Actions.fadeOut(0.4f));
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run(){
                                        dispose();
                                        noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));
                                    }

                                }, 0.4f);
                            }
                            break;
                    }
                }
                return true;
            }

        });
    }

    private void displayImage(String file)
    {
        table.addAction(Actions.fadeOut(0.4f));
        Pixmap pixmap200 = new Pixmap(Gdx.files.internal(file));
        Pixmap pixmap100 = new Pixmap(740*VIEWPORT_WIDTH/800, 234*VIEWPORT_HEIGHT/480, pixmap200.getFormat());//Scale background image to the device's resolution
        pixmap100.drawPixmap(pixmap200,
                0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
                0, 0, pixmap100.getWidth(), pixmap100.getHeight());
        tutorialImage = new Texture(pixmap100);
        pixmap200.dispose();
        pixmap100.dispose();
        img=new Image(tutorialImage);
        img.getColor().a = 0;
        img.addAction(Actions.fadeIn(0.4f));
        img.setPosition(VIEWPORT_WIDTH / 2 - img.getWidth() / 2, VIEWPORT_HEIGHT / 2 - img.getHeight() / 2);
        stage.addActor(img);
        buttonPressed=false;
    }

    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage){

        //BitmapFont  font = assetsManager.createBitmapFont();
        //BitmapFont  font = assetsManager.createBimapFont(45);

        ImageTextButton.ImageTextButtonStyle textButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        textButtonStyle.up = ButtonImage;
        textButtonStyle.down = selectionColorPressed;
        textButtonStyle.over = ButtonImage;
        textButtonStyle.font = assetsManager.getFont();
        return textButtonStyle;
    }
    private void createLogo()
    {
        Image logo=assetsManager.scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        assetsManager.setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
    }
    private  void addVerticalGroup(Actor actor)
    {
        verticalGroup.addActor(actor);

    }
    private void createVerticalGroup()
    {
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        assetsManager.setLogoPosition(verticalGroup);
    }

    private Label createLabel(String text){

        Label.LabelStyle labelstyle = new Label.LabelStyle(assetsManager.getFont(), Color.WHITE);
        return new Label(text, labelstyle);

    }
}
