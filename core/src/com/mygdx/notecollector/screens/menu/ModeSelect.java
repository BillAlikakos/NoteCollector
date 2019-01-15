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
    //private BitmapFont font;
    private Assets assetsManager;
    //private Viewport viewport;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private NoteCollector noteCollector;
    private VerticalGroup verticalGroup;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private float[] size;
    private float sizeX;
    private float sizeY;
    private Table table;
    private Table exitBtnTable;
    private boolean multiplayer;
    private ServerClass srv;
    private boolean buttonPressed=false;


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
        table = new Table();
        table.center().padBottom(10f);
        exitBtnTable = new Table();
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        table.setFillParent(true);

        Gdx.input.setInputProcessor(stage);
        createLogo();
        size = assetsManager.setButtonSize(sizeX, sizeY);
        sizeX = size[0];
        sizeY = size[1];

        createLabel("Select Game Mode:");
        createButton("Practice");//Original Game Mode
        createButton("Competitive");


        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton("Back", textButtonStyle);
        AddButtonListener(MenuButton, "Back");
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX, sizeY);
        stage.addActor(table);
        stage.addActor(exitBtnTable);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        exitBtnTable.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in
        exitBtnTable.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
    }

    private void createLabel(String text)
    {
        int fontSize=VIEWPORT_WIDTH/30;
        //fontSize = 45;
        //BitmapFont font = assetsManager.createBimapFont(fontSize);
        //BitmapFont font = assetsManager.createFontH();

        Label.LabelStyle labelstyle = new Label.LabelStyle(assetsManager.getFontH(), Color.WHITE);
        Label label = new Label(text, labelstyle);
        //label.setPosition((stage.getCamera().viewportWidth-label.getWidth())/2,Yaxis);
        //stage.addActor(label);
        table.add(label);
        table.row();


    }

    private void LoadAssets()
    {
        assetsManager.LoadAssets();
        //font = assetsManager.createBitmapFont();
        selectionColor = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage, Texture.class)));
        selectionColor.setRightWidth(9f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed, Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);

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


                if (MenuButton.isPressed() && !buttonPressed)
                {
                    buttonPressed=true;
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    MenuButton.removeListener(this);
                    table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
                    exitBtnTable.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out icon table
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run()
                        {
                                dispose();
                                if (!multiplayer)
                                {
                                    switch (text)
                                    {
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
                                }
                                else
                                {
                                    switch (text)//Multiplayer host parameters
                                    {
                                        case "Practice":
                                            noteCollector.setScreen(new DifficultyScreen(noteCollector,srv,false,stage));
                                            break;
                                        case "Competitive":
                                            noteCollector.setScreen(new DifficultyScreen(noteCollector,srv,true,stage));
                                            break;
                                        case "Back":
                                            srv.closeServer();
                                            noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));
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
        textButtonStyle.font = assetsManager.getFont();
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
        assetsManager.disposeMenuAssets();
        //font.dispose();
        verticalGroup.clear();
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(exitBtnTable);
    }

}
