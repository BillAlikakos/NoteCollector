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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.ScoresScreen;
import com.mygdx.notecollector.screens.menu.Multiplayer.MultiplayerModeScreen;
import com.mygdx.notecollector.screens.menu.TrackSearch.SearchTrack;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private BitmapFont font;
    private Assets assetsManager;
    private Viewport viewport;
    private static int VIEWPORT_WIDTH = Constants.APP_WIDTH;//Gdx.graphics.getWidth();//
    private static int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;//Gdx.graphics.getHeight(); //
    private NoteCollector noteCollector;
    private VerticalGroup verticalGroup;
    private TextureRegionDrawable selectionColor;//Textures for the buttons
    private TextureRegionDrawable selectionColorPressed;
    private TextureRegionDrawable scoreBtn;
    private TextureRegionDrawable helpBtn;
    private TextureRegionDrawable settingsBtn;
    private TextureRegionDrawable exitBtn;
    private TextureRegionDrawable scoreBtnP;
    private TextureRegionDrawable helpBtnP;
    private TextureRegionDrawable settingsBtnP;
    private TextureRegionDrawable exitBtnP;
    private Table table;//Table for the play button (For format purposes)
    private Table top;//Table for the icon menu



    public MainMenuScreen(NoteCollector noteCollector)
    {
        this.noteCollector = noteCollector;
        assetsManager = noteCollector.getAssetsManager();
        this.table=new Table();
        this.top=new Table();
        top.setFillParent(true);
        table.setFillParent(true);
        LoadAssets();

    }

    public MainMenuScreen(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        this.table=new Table();
        this.top=new Table();
        top.setFillParent(true);
        table.setFillParent(true);

        LoadAssets();

    }

    @Override
    public void show()
    {
        System.out.println("Height "+VIEWPORT_HEIGHT);
        System.out.println("Width "+VIEWPORT_WIDTH);
        //setupCamera();
        Gdx.input.setInputProcessor(stage);
        createVerticalGroup();
        createLogo();
        createButton("Play");
        createButton("Multiplayer");
        createButton("Search Tracks");
        createIcons();
        //createBackground();
        stage.addActor(verticalGroup);
        stage.addActor(table);
        stage.addActor(top);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        top.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
        top.addAction(Actions.sequence(Actions.fadeIn(0.2f)));

    }

    private void setupCamera(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
       // stage = new Stage(viewport);
        stage.getCamera().update();
    }
    private void LoadAssets(){

        assetsManager.LoadMenuAssets();

        font = assetsManager.createBitmapFont();
        scoreBtn=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.scoresImage,Texture.class)));
        helpBtn=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.helpImage,Texture.class)));
        settingsBtn=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.settingsImage,Texture.class)));
        exitBtn=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.exitImage,Texture.class)));
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;

        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        helpBtnP=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.helpImageP,Texture.class)));
        scoreBtnP=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.scoresImageP,Texture.class)));
        settingsBtnP=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.settingsImageP,Texture.class)));
        exitBtnP=new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.exitImageP,Texture.class)));
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);

    }

    private void createLogo()
    {
        Texture img = assetsManager.assetManager.get(Constants.logo);
        Image background = new Image(img);
        addVerticalGroup(background);
    }
    private void createBackground()
    {
        System.out.println("Width:"+VIEWPORT_WIDTH+" Height:"+VIEWPORT_HEIGHT);
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background=assetsManager.scaleBackground(file);
        stage.addActor(background);
        //stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(2f)));
        //background.getColor().a=0;
        //background.addAction(Actions.fadeIn(0.2f));
    }
    private void createVerticalGroup()
    {
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.setName("Logo Group");
        assetsManager.setLogoPosition(verticalGroup);
    }

    private  void addVerticalGroup(Actor actor){
        verticalGroup.addActor(actor);

    }

    private ImageTextButton createButton(String text)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        MenuButton.setColor(Color.WHITE);
        AddButtonListener(MenuButton,text);

        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)
        {
            table.add(MenuButton).size(200,100);
        }
        else if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            table.add(MenuButton).size(250,150);
        }
        else if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            table.add(MenuButton).size(300,200);
        }
        table.row();
        return MenuButton;

    }

    private void createIcons()//Set styles, event handlers etc.
    {
        System.out.println("Creating icons");
        final ImageButton scoreButton = new ImageButton(scoreBtn);
        scoreButton.getStyle().down=scoreBtnP;
        final ImageButton settingsButton = new ImageButton(settingsBtn);
        settingsButton.getStyle().down=settingsBtnP;
        final ImageButton helpButton = new ImageButton(helpBtn);
        helpButton.getStyle().down=helpBtnP;
        final ImageButton exitButton = new ImageButton(exitBtn);
        exitButton.getStyle().down=exitBtnP;
        scoreButton.addListener((new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
               if(scoreButton.isPressed())
               {
                   playSound(prefs);
                  // scoreButton.addAction(Actions.scaleTo(2, 2, 0.2f));
                   table.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out table
                   top.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out icon table
                   Timer.schedule(new Timer.Task()
                   {
                       @Override
                       public void run()
                       {
                           dispose();
                           noteCollector.setScreen(new ScoresScreen(noteCollector,stage));
                       }
                   }, 0.4f);
                   //Handle the input event.
               }

               return true;
            }
        }));
        helpButton.addListener((new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
                if(helpButton.isPressed())
                {
                    playSound(prefs);
                    table.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out table
                    top.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out icon table
                    Timer.schedule(new Timer.Task()
                    {
                        @Override
                        public void run()
                        {
                            dispose();
                            noteCollector.setScreen(new HelpScreen(noteCollector,stage));
                        }
                    }, 0.4f);
                }
                return true;
            }
        }));
        settingsButton.addListener((new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
                if(settingsButton.isPressed())
                {
                    playSound(prefs);
                    table.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out table
                    top.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out icon table
                    Timer.schedule(new Timer.Task()
                    {
                        @Override
                        public void run()
                        {
                            dispose();
                            noteCollector.setScreen(new OptionsScreen(noteCollector,stage));
                        }
                    }, 0.4f);
                }
                return true;
            }
        }));
        exitButton.addListener((new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
                if(exitButton.isPressed())
                {
                    playSound(prefs);
                    table.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out table
                    top.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out icon table
                    Timer.schedule(new Timer.Task()
                    {
                        @Override
                        public void run()
                        {
                            Gdx.app.exit();
                        }
                    }, 0.35f);
                }
                return true;
            }
        }));
        top.right().top();
        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)
        {
            top.add(scoreButton).expand().top().right().size(50,50);
            top.add(settingsButton).top().right().size(50,50);
            top.add(helpButton).top().right().size(50,50);
            top.add(exitButton).top().right().size(50,50);
        }
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            top.add(scoreButton).expand().top().right().size(75,75);
            top.add(settingsButton).top().right().size(75,75);
            top.add(helpButton).top().right().size(75,75);
            top.add(exitButton).top().right().size(75,75);
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            top.add(scoreButton).expand().top().right().size(95,95);
            top.add(settingsButton).top().right().size(95,95);
            top.add(helpButton).top().right().size(95,95);
            top.add(exitButton).top().right().size(95,95);
        }


    }
    private void playSound( Preferences prefs)
    {
        if (prefs.getBoolean("sound"))
        {
            noteCollector.getClick().play();
        }
    }
    private void AddButtonListener(final ImageTextButton MenuButton, final String text){
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

                    if (MenuButton.isPressed())
                    {


                         if (prefs.getBoolean("sound")) {
                          noteCollector.getClick().play();
                         }
                        table.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out table
                        top.addAction(Actions.sequence(Actions.fadeOut(0.35f)));//Fade out icon table
                        Timer.schedule(new Timer.Task() {
                         @Override
                         public void run() {

                                dispose();
                              switch (text) {
                                  case "Play":
                                      //noteCollector.setScreen(new DifficultyScreen(noteCollector));//new Browse(noteCollector));//Original
                                      noteCollector.setScreen(new ModeSelect(noteCollector,stage));//new Browse(noteCollector));
                                      break;
                                  case "Multiplayer":
                                      noteCollector.setScreen(new MultiplayerModeScreen(noteCollector,stage));
                                  break;
                                  case "Search Tracks":
                                      noteCollector.setScreen(new SearchTrack(noteCollector,stage));
                                  break;

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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose()//TODO Change the icon dispose functions (takes too much time on slower phones) reposition gameOver buttons, optimize network discovery , optimize track select
    {
        table.clear();
        //assetsManager.disposeMenuAssets();
        font.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(top);
        stage.getRoot().removeActor(verticalGroup);
        //stage.dispose();
    }
}
