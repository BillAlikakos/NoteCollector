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
import com.esotericsoftware.kryonet.Server;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;

/**
 * Created by bill on 7/25/16.
 */
public class DifficultyScreen implements Screen
{
    private Stage stage;
    private BitmapFont font;
    private Assets assetsManager;
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
    private boolean mode=false;//Flag for game mode (True= Competitive , False= Practice )

    public DifficultyScreen(NoteCollector noteCollector,boolean mode,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        this.multiplayer=false;
        this.mode=mode;
    }

    public DifficultyScreen(NoteCollector noteCollector, ServerClass srv, boolean mode,Stage stage)//Constructor for multiplayer
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
        this.multiplayer=true;
        this.srv=srv;
        this.mode=mode;
    }
    @Override
    public void show()
    {
        table =new Table();
        table.bottom();
        table.padBottom(VIEWPORT_HEIGHT*0.1f);
        exitBtnTable=new Table();
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        table.setFillParent(true);

        Gdx.input.setInputProcessor(stage);
        createLogo();
        size=assetsManager.setButtonSize(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];

        createLabel("Select Difficulty:");
        createButton("Easy");
        createButton("Normal");
        createButton("Hard");
        createButton("Very Hard");



        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton("Back", textButtonStyle);
        AddButtonListener(MenuButton,"Back");
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);

        stage.addActor(table);
        stage.addActor(exitBtnTable);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        exitBtnTable.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));//Fade button table in
        exitBtnTable.addAction(Actions.sequence(Actions.fadeIn(0.2f)));



    }

    private void createLabel(String text)
    {
        int fontSize;
        fontSize=VIEWPORT_WIDTH/30;
        BitmapFont font = assetsManager.createBimapFont(fontSize);

        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelstyle);

        table.add(label);
        table.row();
    }

    private void LoadAssets()
    {
        assetsManager.LoadAssets();
        font=assetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(9f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
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

        AddButtonListener(MenuButton,text);
        table.add(MenuButton).size(sizeX,sizeY);

        table.row();
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
                    table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
                    exitBtnTable.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out icon table
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            dispose();
                            if(!multiplayer)
                            {
                                switch (text)
                                {
                                    case "Easy":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 130*VIEWPORT_HEIGHT/480, 200,mode,stage));
                                        break;
                                    case "Normal":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 150*VIEWPORT_HEIGHT/480, 160,mode,stage));
                                        break;
                                    case "Hard":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 170*VIEWPORT_HEIGHT/480, 120,mode,stage));
                                        break;
                                    case "Very Hard":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 180*VIEWPORT_HEIGHT/480, 100,mode,stage));
                                        break;
                                    case "Back":
                                        noteCollector.setScreen(new ModeSelect(noteCollector,stage));
                                        break;
                                }
                            }
                            else
                            {
                                switch (text)//Multiplayer host parameters
                                {
                                    case "Easy":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 130*VIEWPORT_HEIGHT/480, 200,srv,mode,"Easy",stage));
                                        break;
                                    case "Normal":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 150*VIEWPORT_HEIGHT/480, 160,srv,mode,"Normal",stage));
                                        break;
                                    case "Hard":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 170*VIEWPORT_HEIGHT/480, 120,srv,mode,"Hard",stage));
                                        break;
                                    case "Very Hard":
                                        noteCollector.setScreen(new SamplesTrack(noteCollector, 180*VIEWPORT_HEIGHT/480, 100,srv,mode,"Very Hard",stage));
                                        break;
                                    case "Back":
                                        System.out.println("Closing server");
                                        //srv.getServer().close();
                                        noteCollector.setScreen(new ModeSelect(noteCollector,srv,stage));
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

        stage.act(Gdx.graphics.getDeltaTime());
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
        font.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(exitBtnTable);
        stage.getRoot().removeActor(verticalGroup);
    }
}
