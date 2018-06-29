package com.mygdx.notecollector.screens;

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
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.Score;
import com.mygdx.notecollector.Utils.scoreObj;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;

import java.util.ArrayList;

/**
 * Created by bill on 7/22/16.
 */
public class ScoresScreen implements Screen {


    private Score score;
    private ArrayList<scoreObj> scores ;
    private Assets AssetsManager;
    private NoteCollector notecollector;
    private Stage stage;
    private Viewport viewport;
    private int toggle =0;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;


    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private BitmapFont font;
    private VerticalGroup verticalGroup;
    private VerticalGroup verticalGroupLogo;
    private Table exitBtnTable;
    private int sizeX;
    private int sizeY;
    private int[] size;

    public ScoresScreen(NoteCollector notecollector) {
        this.notecollector = notecollector;
        score = new Score();
        scores = score.getScore();
        AssetsManager = notecollector.getAssetsManager();
        LoadAssets();

    }

    @Override
    public void show()
    {
        setupCamera();
        createVerticalGroup();
        createBackground();
        createLogo();

        exitBtnTable=new Table();
        size=AssetsManager.setButtonDimensions(sizeX,sizeY);//Get the dimensions for the button
        sizeX=size[0];
        sizeY=size[1];
        printScoreList();
        createButton("Menu",sizeX,sizeY);
        stage.addActor(verticalGroup);
        stage.addActor(exitBtnTable);
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {

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


    }
    private void printScoreList()//Create and print the high score table
    {
        int fontSize;
        fontSize=15;
        //Skin uiSkin = new Skin(Gdx.files.internal("data/ui/skin/uiskin.json"));
        //uiSkin.getFont("default-font").getData().setScale(1.5f,1.5f);//Change font size
        //check if there are recorded scores. if exist get all scores with names and print it
        if (scores.size()==0 || scores ==null)
        {

            if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//Set appropriate sizes for title spacing according to resolution
            {
                fontSize=28;
            }
            if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
            {
                fontSize=28;
            }
            if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
            {
                fontSize=35;
            }
            Label score = createLabel("No recorded scores ",fontSize);
            addVerticalGroup(score);
        }
        else
        {
            int limit=scores.size();

            if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//Set appropriate sizes for title spacing according to resolution
            {
                fontSize=15;
            }
            if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
            {
                fontSize=20;
            }
            if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
            {
                fontSize=30;
            }
            Label title = createLabel("High Scores",45);
            Label scoreLabel = createLabel("Score", fontSize+5);
            Label nameLabel=createLabel("Name", fontSize+5);
            Label trackLabel=createLabel("Song",fontSize+5);
            Label diffLabel=createLabel("Difficulty",fontSize+5);
            Table table = new Table();
            table.setFillParent(true);
            stage.addActor(table);
            table.add(title).center().colspan(4);
            table.row();
            table.add(nameLabel).uniform();
            table.add(scoreLabel).uniform();
            table.add(trackLabel).uniform();
            table.add(diffLabel).uniform();
            if(scores.size()>=5)
            {
                limit=5;
            }
            for (int i = 0; i < limit; i++)
            {

                Label score = createLabel(scores.get(i).getScore().toString(), fontSize);
                Label name=createLabel(scores.get(i).getName(), fontSize);
                Label track=createLabel(scores.get(i).getTrackName(),fontSize);
                Label difficulty=createLabel(scores.get(i).getDifficulty(),fontSize);

                table.row();
                table.add(name).uniform();
                table.add(score).uniform();
                table.add(track).uniform();
                table.add(difficulty).uniform();
            }
        }
    }
    private Label createLabel(String text, int size)
    {
        BitmapFont font = AssetsManager.createBimapFont(size);
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelstyle);
        return label;

    }

    private void createVerticalGroup()
    {
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
       // verticalGroup.center().padTop(80f).space(5f);
        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//Set appropriate sizes for title spacing according to resolution
        {
            //background.setPosition((stage.getCamera().viewportWidth-background.getWidth())/2,(stage.getCamera().viewportHeight-background.getHeight()));
            verticalGroup.center().padTop(10f).space(10f);
        }
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            verticalGroup.center().padTop(200f).space(10f);
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            verticalGroup.center().padTop(300f).space(10f);
        }
    }

    private  void addVerticalGroup(Actor actor){
        verticalGroup.addActor(actor);

    }
    private void LoadAssets()
    {
        font=AssetsManager.createBitmapFont();
       // font = AssetsManager.createBimapFont(45);
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);


    }
    private void createLogo()
    {
        Texture img = AssetsManager.assetManager.get(Constants.logo);
        Image background = new Image(img);
        verticalGroupLogo  = new VerticalGroup();
        verticalGroupLogo.setFillParent(true);
        verticalGroupLogo.addActor(background);
       AssetsManager.setLogoPosition(verticalGroupLogo);

        stage.addActor(verticalGroupLogo);
        //stage.addActor(background);
        //addVerticalGroup(background);
    }
    private void createBackground()
    {
        System.out.println("Width:"+VIEWPORT_WIDTH+" Height:"+VIEWPORT_HEIGHT);
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background=AssetsManager.scaleBackground(file);
        stage.addActor(background);
    }

    private void createButton(String text,int sizeX,int sizeY)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton);
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);
    }

    private void AddButtonListener(final ImageTextButton MenuButton){
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        notecollector.getClick().play();
                    }
                    //notecollector.adsHandler.showAds(1);
                    Timer.schedule(new Timer.Task() {

                        @Override
                        public void run() {
                            dispose();
                            notecollector.setScreen(new MainMenuScreen(notecollector));

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

    private void setupCamera(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }
}
