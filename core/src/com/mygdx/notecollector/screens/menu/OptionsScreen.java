package com.mygdx.notecollector.screens.menu;

import com.badlogic.gdx.Game;
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
import com.mygdx.notecollector.screens.menu.Options.GameOptions;
import com.mygdx.notecollector.screens.menu.Options.MiscOptions;
import com.mygdx.notecollector.screens.menu.Options.SoundOptions;

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
    private ImageTextButton soundOptions,gameOptions,miscOptions;
    private  boolean flag =false;
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Preferences prefs;

    public OptionsScreen(NoteCollector noteCollector,Stage stage) {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        font = assetsManager.createBitmapFont();
        table = new Table();
        exitBtnTable=new Table();
        LoadAssets();
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
    }

    private Label createLabel(String text){

        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;

    }


    @Override
    public void show()
    {
        size=assetsManager.setButtonSize(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];
        Gdx.input.setInputProcessor(stage);
        createTable();
        createLogo();
        createOptions();
        stage.addActor(table);
        stage.addActor(exitBtnTable);
        createBackButton("Menu");
        table.getColor().a=0;//Fade table in
        exitBtnTable.getColor().a=0;//Fade table in
        table.addAction(Actions.fadeIn(0.2f));
        exitBtnTable.addAction(Actions.fadeIn(0.2f));
    }
    private void createOptions()
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        soundOptions = new ImageTextButton("Sound Options", textButtonStyle);
        gameOptions = new ImageTextButton("Game Options", textButtonStyle);
        miscOptions = new ImageTextButton("Misc Options", textButtonStyle);
        soundOptions.addListener(new ClickListener()
        {
           @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
           {
               table.addAction(Actions.fadeOut(0.4f));
               exitBtnTable.addAction(Actions.fadeOut(0.4f));
               Timer.schedule(new Timer.Task() {

                   @Override
                   public void run() {
                       dispose();
                       noteCollector.setScreen(new SoundOptions(noteCollector,stage));

                   }

               }, 0.4f);
               return true;
           }
        });
        gameOptions.addListener(new ClickListener()
        {
           @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
           {
               table.addAction(Actions.fadeOut(0.4f));
               exitBtnTable.addAction(Actions.fadeOut(0.4f));
               Timer.schedule(new Timer.Task()
               {
                   @Override
                   public void run()
                   {
                       dispose();
                       noteCollector.setScreen(new GameOptions(noteCollector,stage));
                   }
               }, 0.4f);
               return true;
           }
        });
        miscOptions.addListener(new ClickListener()
        {
           @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
           {
               table.addAction(Actions.fadeOut(0.4f));
               exitBtnTable.addAction(Actions.fadeOut(0.4f));
               Timer.schedule(new Timer.Task()
               {
                   @Override
                   public void run()
                   {
                       dispose();
                       noteCollector.setScreen(new MiscOptions(noteCollector,stage));
                   }
               }, 0.4f);
               return true;
           }
        });
        table.add(createLabel("Options")).padTop(VIEWPORT_HEIGHT*0.14f/1080);
        table.row();
        table.add(soundOptions).size(sizeX,sizeY);
        table.row();
        table.add(gameOptions).size(sizeX,sizeY);
        table.row();
        table.add(miscOptions).size(sizeX,sizeY);
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
        Image logo=assetsManager.scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        assetsManager.setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
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
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);
        table.clear();
    }
}
