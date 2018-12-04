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

public class GameOptions implements Screen
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
    private ImageTextButton NormalSize,BigSize,VBigSize;
    private ImageTextButton touch,pad;
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Preferences prefs;

    public GameOptions(NoteCollector noteCollector,Stage stage)
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
        createButtonSize();
        createControlTypes();
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
        return new Label(text, labelstyle);
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
        return new ImageTextButton(text, textButtonStyle);
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
        if (!prefs.getBoolean("big"))
        {
            BigStyle = createButtonStyle(selectionColor);
        }
        else
        {
            BigStyle = createButtonStyle(selectionColorPressed);
        }
        ImageTextButton.ImageTextButtonStyle VBigStyle;
        if (!prefs.getBoolean("vbig"))
        {
            VBigStyle = createButtonStyle(selectionColor);
        }
        else
        {
            VBigStyle = createButtonStyle(selectionColorPressed);
        }
        NormalSize = createButton("Normal",NormalStyle);
        BigSize = createButton("Big", BigStyle);
        VBigSize = createButton("Very Big",VBigStyle);

        NormalSize.addListener(new ClickListener() {


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(NormalSize.isPressed() && !prefs.getBoolean("normal"))
                {
                    if (prefs.getBoolean("sound"))
                    {
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
                if(BigSize.isPressed() && !prefs.getBoolean("big"))
                {
                    if (prefs.getBoolean("sound"))
                    {
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
                if (prefs.getBoolean("sound"))
                {
                    noteCollector.getClick().play();
                }
                if(VBigSize.isPressed() && !prefs.getBoolean("vbig"))
                {
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


        if (!prefs.getBoolean("touch"))
        {
            TouchStyle = createButtonStyle(selectionColor);
        }
        else
        {
            TouchStyle = createButtonStyle(selectionColorPressed);

        }
        if (!prefs.getBoolean("dpad"))
        {
            PadStyle = createButtonStyle(selectionColor);
        }
        else
        {
            PadStyle = createButtonStyle(selectionColorPressed);
        }
        touch = createButton("Touch",TouchStyle);
        pad = createButton("Dpad", PadStyle);

        touch.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                if(touch.isPressed() && !prefs.getBoolean("touch"))
                {
                    if (prefs.getBoolean("sound"))
                    {
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
                if(pad.isPressed() && !prefs.getBoolean("pad"))
                {
                    if (prefs.getBoolean("sound"))
                    {
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
        assetsManager.disposeMenuAssets();
        font.dispose();
        //stage.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);
        table.clear();
    }
}
