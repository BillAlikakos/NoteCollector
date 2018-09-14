package com.mygdx.notecollector.screens.menu.Multiplayer;


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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.menu.DifficultyScreen;
import com.mygdx.notecollector.screens.menu.LoadingScreen;
import com.mygdx.notecollector.screens.menu.ModeSelect;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class MultiplayerHostScreen implements  Screen
{
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font, fontH;
    private Texture img;
    private Image logo;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private NoteCollector noteCollector;
    private Assets assetsManager;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private Table table;
    private ScrollPane scrollPane;
    private Table btn;
    private int[] size;
    private int sizeX;
    private int sizeY;
    private VerticalGroup verticalGroup;
    private ServerClass srv;

    public MultiplayerHostScreen(NoteCollector noteCollector) {
        this.noteCollector = noteCollector;
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
    }

    @Override
    public void show()
    {
        setupCamera();
        Gdx.input.setInputProcessor(stage);
        createBackground();
        btn = new Table();
        btn.setFillParent(true);
        createTable();

        createLogo();
        size = assetsManager.setButtonDimensions(sizeX, sizeY);
        sizeX = size[0];
        sizeY = size[1];
        table.row();
        table.bottom().padBottom(50f);
        ImageTextButton back = createButton("Back");
        btn.left();
        btn.add(back).bottom().left().expand().size(sizeX, sizeY);

        stage.addActor(verticalGroup);
        stage.addActor(table);
        stage.addActor(btn);
        getRequest();
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        btn.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
        btn.addAction(Actions.sequence(Actions.fadeIn(0.2f)));

    }


    private void createLogo()
    {
        img = assetsManager.assetManager.get(Constants.logo);
        logo = new Image(img);
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        assetsManager.setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
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
            stage.dispose();
            font.dispose();
            fontH.dispose();
            //img.dispose();
        }
        //create a table for organize buttons and list of tracks
        private void createTable(){
            table = new Table();
            table.center();
            table.setFillParent(true);
            table.pad(60f,10f,30f,10f);
            if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//old dimensions
            {
                table.pad(60f,10f,30f,10f);

            }
            if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
            {

                table.pad(120f,10f,30f,10f);
            }
            if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
            {
                table.pad(200f,10f,30f,10f);
            }

            table.setTouchable(Touchable.enabled);
            //table.setDebug(true);
        }
        private ImageTextButton createButton(String text)
        {
            ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
            ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);

            AddButtonListener(MenuButton,text);
            return MenuButton;
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
                        table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                        btn.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                if (text.equals("Back"))
                                {
                                    System.out.println("Finishing connection...");
                                    dispose();
                                    srv.getServer().close();
                                    noteCollector.setScreen(new MultiplayerModeScreen(noteCollector));
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


        private void LoadAssets(){
            fontH = assetsManager.createBimapFont(45);
            font = assetsManager.createBitmapFont();
            selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
            selectionColor.setRightWidth(5f);
            selectionColor.setBottomHeight(2f);
            selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
            selectionColorPressed.setRightWidth(5f);
            selectionColorPressed.setBottomHeight(2f);


        }


        private Label createLabel(String text){
            Label.LabelStyle labelstyle = new Label.LabelStyle(fontH, Color.WHITE);
            Label fileLabel = new Label(text, labelstyle);
            return  fileLabel;

        }

    
    private void getRequest()
    {
            table.add(createLabel("Awaiting for connection...")).padTop(70f);
            srv = new ServerClass();
            srv.getServer().addListener(new Listener() {
                public void received (Connection connection, Object object)
                {
                    if (object instanceof ServerClass.SomeRequest)
                    {
                        ServerClass.SomeRequest request = (ServerClass.SomeRequest)object;
                        System.out.println(request.text);
                        ServerClass.SomeResponse response = new ServerClass.SomeResponse();
                        response.text = "Preparing match";
                        connection.sendTCP(response);
                        table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                        btn.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                        Timer.schedule(new Timer.Task()
                        {
                            @Override
                            public void run()
                            {
                                    dispose();
                                    //noteCollector.setScreen(new DifficultyScreen(noteCollector,srv));
                                    noteCollector.setScreen(new ModeSelect(noteCollector,srv));
                            }
                        }, 0.2f);
                    }

                }
                });
    }

        private void createBackground(){
            FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
            Image background=assetsManager.scaleBackground(file);
            stage.addActor(background);

        }

        private void setupCamera(){
            viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
            stage = new Stage(viewport);
            stage.getCamera().update();
        }

    }


