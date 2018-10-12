package com.mygdx.notecollector.screens.menu.Options;

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
import com.mygdx.notecollector.screens.menu.MainMenuScreen;
import com.mygdx.notecollector.screens.menu.OptionsScreen;

public class SoundOptions implements Screen
{
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
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Preferences prefs;

    public SoundOptions(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector=noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        font = assetsManager.createBitmapFont();
        table = new Table();
        exitBtnTable=new Table();
        LoadAssets();
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
    }

    private void LoadAssets()
    {
        assetsManager.LoadAssets();
        font=assetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);
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
        createButtonMusic();
        createButtonSound();
        stage.addActor(table);
        stage.addActor(exitBtnTable);
        createBackButton("Menu");
        table.getColor().a=0;//Fade table in
        exitBtnTable.getColor().a=0;//Fade table in
        table.addAction(Actions.fadeIn(0.2f));
        exitBtnTable.addAction(Actions.fadeIn(0.2f));
    }

    private void createTable()
    {
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.padTop(10f*VIEWPORT_HEIGHT/1080);
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

    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        textButtonStyle.up = ButtonImage;
        textButtonStyle.down = selectionColorPressed;
        textButtonStyle.over = ButtonImage;
        textButtonStyle.font = font;
        return textButtonStyle;
    }

    private Label createLabel(String text)
    {
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;
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

    private ImageTextButton createButton(String text,ImageTextButton.ImageTextButtonStyle textButtonStyle )
    {
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        return MenuButton;
    }

    private void AddButtonListener(final ImageTextButton MenuButton)
    {
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
                            dispose();
                            noteCollector.setScreen(new OptionsScreen(noteCollector,stage));

                        }

                    }, 0.4f);

                }
                return true;
            }

        });
    }

    private void createButtonMusic()
    {
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

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width,height);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {
        font.dispose();
        //stage.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);
        table.clear();
    }
}
