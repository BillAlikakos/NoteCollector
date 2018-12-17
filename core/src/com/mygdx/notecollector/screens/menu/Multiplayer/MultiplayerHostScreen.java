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
    //private BitmapFont font, fontH;
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
    private float[] size;
    private float sizeX;
    private float sizeY;
    private VerticalGroup verticalGroup;
    private ServerClass srv;


    public MultiplayerHostScreen(NoteCollector noteCollector,Stage stage)
    {
        this.stage=stage;
        this.noteCollector = noteCollector;
        assetsManager = noteCollector.getAssetsManager();
        LoadAssets();
    }
    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
        btn = new Table();
        btn.setFillParent(true);
        createTable();

        createLogo();
        size = assetsManager.setButtonSize(sizeX, sizeY);
        sizeX = size[0];
        sizeY = size[1];
        table.row();
        table.bottom().padBottom(50f*VIEWPORT_HEIGHT/1080);
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
        Image logo=noteCollector.getAssetsManager().scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        noteCollector.getAssetsManager().setLogoPosition(verticalGroup);
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
            assetsManager.disposeMenuAssets();
            stage.getRoot().removeActor(table);
            stage.getRoot().removeActor(btn);
            stage.getRoot().removeActor(verticalGroup);
            //font.dispose();
            //fontH.dispose();
        }
        //create a table to organize the buttons and the list of tracks
        private void createTable(){
            table = new Table();
            table.center();
            table.setFillParent(true);
            table.pad(200f*VIEWPORT_HEIGHT/1080,10f*VIEWPORT_WIDTH/1920,30f*VIEWPORT_HEIGHT/1080,10f*VIEWPORT_WIDTH/1920);
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
                                    noteCollector.setScreen(new MultiplayerModeScreen(noteCollector,stage));
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
            textButtonStyle.font = assetsManager.getFont();
            return textButtonStyle;
        }


        private void LoadAssets(){
            assetsManager.LoadAssets();
            /*fontH = assetsManager.createBimapFont(45*VIEWPORT_WIDTH/1920);
            font = assetsManager.createBitmapFont();*/
            selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
            selectionColor.setRightWidth(5f);
            selectionColor.setBottomHeight(2f);
            selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
            selectionColorPressed.setRightWidth(5f);
            selectionColorPressed.setBottomHeight(2f);


        }


        private Label createLabel(String text){
            Label.LabelStyle labelstyle = new Label.LabelStyle(assetsManager.getFontH(), Color.WHITE);
            Label fileLabel = new Label(text, labelstyle);
            fileLabel.setPosition(VIEWPORT_WIDTH/2-fileLabel.getWidth(),VIEWPORT_HEIGHT/2);
            return  fileLabel;

        }

    
    private void getRequest()
    {
            table.top().add(createLabel("Awaiting for connection..."));
            //table.add(createLabel("Awaiting for connection...")).padTop(70f*VIEWPORT_HEIGHT/1080);
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
                                    noteCollector.setScreen(new ModeSelect(noteCollector,srv,stage));
                            }
                        }, 0.2f);
                    }

                }
                });
    }
}


