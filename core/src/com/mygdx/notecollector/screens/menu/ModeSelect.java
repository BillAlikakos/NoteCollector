package com.mygdx.notecollector.screens.menu;

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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

public class ModeSelect implements Screen
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
    private int[] size;
    private int sizeX;
    private int sizeY;
    private Table table;
    private Table exitBtnTable;
    private boolean multiplayer;
    private ServerClass srv;


    public ModeSelect(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        this.multiplayer = false;
    }

    public ModeSelect(NoteCollector noteCollector, ServerClass srv,Stage stage)//Constructor for multiplayer (new)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        this.multiplayer = true;
        this.srv = srv;
    }

    @Override
    public void show()
    {
        System.out.println("WIDTH: "+VIEWPORT_WIDTH);
        System.out.println("HEIGHT: "+VIEWPORT_HEIGHT);
        //setupCamera();//Already set
        table = new Table();
        table.center().padBottom(10f);
        exitBtnTable = new Table();
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        table.setFillParent(true);

        Gdx.input.setInputProcessor(stage);
        //createBackground();//Background is already set for menu stage
        createLogo();
        //createVerticalGroup();
        size = assetsManager.setButtonDimensions(sizeX, sizeY);
        sizeX = size[0];
        sizeY = size[1];
        if (VIEWPORT_WIDTH == 800 && VIEWPORT_HEIGHT == 480)//TODO Test multiplayer for possible bugs
        {
            table.padBottom(100f);

        }
        if (VIEWPORT_WIDTH == 1080 && VIEWPORT_HEIGHT == 720) {

            table.padBottom(85f);
        }
        if (VIEWPORT_WIDTH > 1080 && VIEWPORT_HEIGHT > 720) {
            table.padBottom(135f);
        }

        createLabel("Select Game Mode:");
        createButton("Practice");//Original Game Mode
        createButton("Competitive");


        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton("Back", textButtonStyle);
        AddButtonListener(MenuButton, "Back");
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX, sizeY);
        //MenuButton.setPosition(5f,10f);
       // stage.addActor(verticalGroup);
        stage.addActor(table);
        stage.addActor(exitBtnTable);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        exitBtnTable.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in
        exitBtnTable.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
        //stage.addActor(MenuButton);

    }

    private void createLabel(String text)
    {
        int fontSize;
        fontSize = 45;
        BitmapFont font = assetsManager.createBimapFont(fontSize);

        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelstyle);
        //label.setPosition((stage.getCamera().viewportWidth-label.getWidth())/2,Yaxis);
        //stage.addActor(label);

        table.add(label);
        table.row();


    }

    private void setupCamera()
    {
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }

    private void LoadAssets()
    {
        assetsManager.LoadAssets();
        font = assetsManager.createBitmapFont();
        selectionColor = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage, Texture.class)));
        selectionColor.setRightWidth(9f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed, Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);

    }

    private void createLogo()
    {
        Texture img = assetsManager.assetManager.get(Constants.logo);
        Image logo = new Image(img);
        verticalGroup = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        assetsManager.setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
    }

    private void createBackground()
    {
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background = assetsManager.scaleBackground(file);
        stage.addActor(background);

    }


    private void createButton(String text)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);

        AddButtonListener(MenuButton, text);
        table.add(MenuButton).size(sizeX, sizeY);

        table.row();
    }

    private void AddButtonListener(final ImageTextButton MenuButton, final String text)
    {
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");


                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
                    exitBtnTable.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out icon table
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run()
                        {
                                dispose();
                                if (multiplayer == false) {
                                    switch (text) {
                                        case "Practice":
                                            noteCollector.setScreen(new DifficultyScreen(noteCollector,false,stage));
                                            break;
                                        case "Competitive":
                                            noteCollector.setScreen(new DifficultyScreen(noteCollector,true,stage));
                                            break;
                                        case "Back":
                                            noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));
                                            break;
                                    }
                                } else {
                                    switch (text)//Multiplayer host parameters
                                    {
                                        case "Practice":
                                            noteCollector.setScreen(new DifficultyScreen(noteCollector,srv,false,stage));
                                            break;
                                        case "Competitive":
                                            noteCollector.setScreen(new DifficultyScreen(noteCollector,srv,true,stage));
                                            break;
                                        case "Back":
                                            noteCollector.setScreen(new MainMenuScreen(noteCollector));
                                            break;
                                    }
                                }


                        }
                    }, 0.4f);
                }
                return true;
            }

        });

    }

    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage) {

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

        //stage.act();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();


    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        font.dispose();
        verticalGroup.clear();
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(exitBtnTable);
    }

}
