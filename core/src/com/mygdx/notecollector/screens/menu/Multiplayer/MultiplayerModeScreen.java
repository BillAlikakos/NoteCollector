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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;

public class MultiplayerModeScreen implements Screen
{
    private Stage stage;
    private BitmapFont font;
    private Assets assetsManager;
    private Viewport viewport;
    private Texture img;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;//Gdx.graphics.getWidth();//
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;//Gdx.graphics.getHeight(); //
    private NoteCollector noteCollector;
    private VerticalGroup verticalGroup;
    private TextureRegionDrawable selectionColor;//Textures for the buttons
    private TextureRegionDrawable selectionColorPressed;
    private Table table;//Table for the play button (For format purposes)
    private Table btn;
    private Image icon;

    private float[] size;
    private float sizeX;
    private float sizeY;
    private boolean networkAccess;

    public MultiplayerModeScreen(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        this.table=new Table();
        table.setFillParent(true);

        LoadAssets();
    }


    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
        createVerticalGroup();
        createLogo();
        size = assetsManager.setButtonSize(sizeX, sizeY);
        sizeX = size[0];
        sizeY = size[1];
        ImageTextButton back = createBackButton("Back");
        btn=new Table();
        btn.left();
        btn.add(back).bottom().left().expand().size(sizeX, sizeY);
        stage.addActor(btn);
        stage.addActor(verticalGroup);
        stage.addActor(table);
        networkAccess=noteCollector.getWifiCtx();
        if(networkAccess)
        {
            createButton("Host Game");
            createButton("Join Game");
        }
        else
        {
            createImg();
            createLabel("Network Connection Unavailable", (stage.getCamera().viewportHeight / 2 + 100*VIEWPORT_HEIGHT/1080));
            createLabel("Please Connect to a Network", (stage.getCamera().viewportHeight / 2 + 65*VIEWPORT_HEIGHT/1080));
        }
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        btn.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
        btn.addAction(Actions.sequence(Actions.fadeIn(0.2f)));


    }

    private void createLabel(String text, float Yaxis) //Method for label creation
    {
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelstyle);
        label.setPosition((stage.getCamera().viewportWidth - label.getWidth()) / 2, Yaxis + 50*VIEWPORT_HEIGHT/1080);
        stage.addActor(label);

    }

    private ImageTextButton createBackButton(String text)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);

        AddButtonListener(MenuButton,text);
        return MenuButton;
    }

    private void setupCamera()
    {
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }

    private void LoadAssets()
    {
        assetsManager.LoadWiFiAssets();
        font = assetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;

        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);
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

    private void createImg()
    {
        img = assetsManager.assetManager.get(Constants.noConn);
        icon = new Image(img);
        float Xaxis=(stage.getCamera().viewportWidth - 375*VIEWPORT_WIDTH/1920)/2;
        float Yaxis=stage.getCamera().viewportHeight/2-325*VIEWPORT_HEIGHT/1080;
        icon.setScale(.5f*VIEWPORT_WIDTH/1920,.5f*VIEWPORT_HEIGHT/1080);
        icon.setPosition(Xaxis,Yaxis);
        stage.addActor(icon);
        icon.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        icon.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
    }

    private void createBackground()
    {
        System.out.println("Width:"+VIEWPORT_WIDTH+" Height:"+VIEWPORT_HEIGHT);
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background=assetsManager.scaleBackground(file);
        stage.addActor(background);
    }
    private void createVerticalGroup()
    {
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        assetsManager.setLogoPosition(verticalGroup);
    }

    private void createButton(String text)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);

        table.add(MenuButton).size(sizeX,sizeY);
        table.row();

    }


    private void AddButtonListener(final ImageTextButton MenuButton, final String text)
    {
        MenuButton.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
                if (MenuButton.isPressed())
                {
                    if (prefs.getBoolean("sound"))
                    {
                        noteCollector.getClick().play();

                    }
                    table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                    btn.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                    if(!networkAccess)
                    {
                        icon.addAction(Actions.fadeOut(0.4f));
                    }
                    Timer.schedule(new Timer.Task()
                    {
                        @Override
                        public void run()
                        {
                            dispose();
                            switch (text)
                            {
                                case "Host Game": noteCollector.setScreen(new MultiplayerHostScreen(noteCollector,stage));
                                break;
                                case "Join Game": noteCollector.setScreen(new MultiplayerClientScreen(noteCollector,stage));
                                break;
                                case "Back": noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));
                                break;

                                }
                            }
                        }, 0.4f);//prev 0.2f

                    }
                    return true;
                }
            });

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


    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //stage.act();
        stage.act(Gdx.graphics.getDeltaTime());
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
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(btn);
        stage.getRoot().removeActor(verticalGroup);
    }
}

