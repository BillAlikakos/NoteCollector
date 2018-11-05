package com.mygdx.notecollector.screens.menu.UserArea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.ScoreClass;
import com.mygdx.notecollector.screens.menu.UserArea.ResultScreen;

import java.util.ArrayList;

/**
 * Created by bill on 7/22/16.
 */
public class ScoresScreen implements Screen {


    //private Score score;
    private ScoreClass score;
    //private ArrayList<scoreObj> scores ;
    private ArrayList<ScoreClass> scores ;
    private Assets AssetsManager;
    private NoteCollector noteCollector;
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
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Table table;
    private boolean selectMode;
    private boolean competitive;

    public ScoresScreen(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        this.score=new ScoreClass("","","","","");
        this.selectMode=true;
        //score = new Score();
        //scores = score.getScore();
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
    }
    public ScoresScreen(NoteCollector noteCollector,Stage stage, ScoreClass score) {
        this.noteCollector = noteCollector;
        this.stage=stage;
        this.score=score;
        this.selectMode=false;
        //score = new Score();
        //scores = score.getScore();
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
    }
    public ScoresScreen (NoteCollector noteCollector, Stage stage,ArrayList<ScoreClass> scores,boolean competitive)
    {
        this.noteCollector = noteCollector;
        this.stage=stage;
        this.score=new ScoreClass("","","","","");
        this.scores=scores;
        this.selectMode=false;
        this.competitive=competitive;
        //score = new Score();
        //scores = score.getScore();
        AssetsManager = noteCollector.getAssetsManager();
        LoadAssets();
    }

    @Override
    public void show()
    {
        table = new Table();
        table.setFillParent(true);
        if(selectMode)
        {
            createVerticalGroup();
            createLogo();
        }
        /*if(!score.getScore().equals(""))
        {
            Label title=createLabel("Score Submitted",43*VIEWPORT_WIDTH/1920);
            table.center().addActor(title);
        }*/
        exitBtnTable=new Table();
        size=AssetsManager.setButtonSize(sizeX,sizeY);//Get the dimensions for the button
        sizeX=size[0];
        sizeY=size[1];
        //printScoreList();
        if(selectMode)
        {
            Label errorLabel = createLabel("Select High Score Category", 55*VIEWPORT_WIDTH/1920);
            table.add(errorLabel).center();
            table.row();
            createMenuButton("Practice",sizeX,sizeY);
            table.row();
            createMenuButton("Competitive",sizeX,sizeY);
            stage.addActor(table);
            table.getColor().a=0;
            table.addAction(Actions.fadeIn(0.2f));
        }
        else
        {
            getScores();
            stage.addActor(table);
            table.getColor().a=0;
            table.addAction(Actions.fadeIn(0.2f));
        }

        createButton("Back",sizeX,sizeY);
        if(selectMode)
        stage.addActor(verticalGroup);
        stage.addActor(exitBtnTable);

        Gdx.input.setInputProcessor(stage);
        exitBtnTable.getColor().a=0;
        exitBtnTable.addAction(Actions.fadeIn(0.2f));
    }

    @Override
    public void render(float delta) {

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
        font.dispose();
        stage.getRoot().removeActor(table);
        if(!selectMode)
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);
    }

    private void getScores()
    {
        int fontSize=45*VIEWPORT_WIDTH/1920;
        table.top().padTop(275f*VIEWPORT_HEIGHT/1080);
        Label title;
        if(this.competitive)
        {
            title = createLabel("Competitive High Scores",VIEWPORT_WIDTH*55/1920);
        }
        else
        {
            title = createLabel("Practice High Scores",VIEWPORT_WIDTH*55/1920);
        }
        if(scores.size()==0)
        {
            Label errorLabel = createLabel("No recorded scores.", fontSize+5);
            table.add(errorLabel).center().colspan(4);
        }
        else
        {
            Label scoreLabel = createLabel("Score", fontSize+5);
            Label nameLabel=createLabel("Name", fontSize+5);
            Label trackLabel=createLabel("Song",fontSize+5);
            Label diffLabel=createLabel("Difficulty",fontSize+5);
            //table = new Table();
            table.setFillParent(true);
            //stage.addActor(table);
            table.add(title).center().colspan(4);
            table.row();
            table.add(nameLabel).width(250f*VIEWPORT_WIDTH/1920).uniform();
            table.add(scoreLabel).width(250f*VIEWPORT_WIDTH/1920).uniform();
            table.add(trackLabel).width(250f*VIEWPORT_WIDTH/1920).uniform();
            table.add(diffLabel).width(250f*VIEWPORT_WIDTH/1920).uniform();
            for(int i=0;i<scores.size();i++)
            {
                Label score = createLabel(scores.get(i).getScore(), fontSize);
                Label name=createLabel(scores.get(i).getUserName(), fontSize);
                Label track=createLabel(scores.get(i).getSongName(),fontSize);
                Label difficulty=createLabel(scores.get(i).getDifficulty(),fontSize);

                table.row();
                table.add(name).width(250f*VIEWPORT_WIDTH/1920).uniform();
                table.add(score).width(250f*VIEWPORT_WIDTH/1920).uniform();
                table.add(track).width(250f*VIEWPORT_WIDTH/1920).uniform();
                table.add(difficulty).width(250f*VIEWPORT_WIDTH/1920).uniform();
            }
        }




        /*for (int i = 0; i < limit; i++)
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
        }*/
    }
    private void printScoreList()//Create and print the high score table
    {
        int fontSize;
        //check if there are recorded scores. if exist get all scores with names and print it
        if (scores.size()==0 || scores ==null)
        {
            //table = new Table();
            /*if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//Set appropriate sizes for title spacing according to resolution
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
            }*/
            fontSize=45*VIEWPORT_WIDTH/1920;
            Label score = createLabel("No recorded scores ",fontSize);
            addVerticalGroup(score);
        }
        else
        {
            int limit=scores.size();

            fontSize=43*VIEWPORT_WIDTH/1920;
            Label title = createLabel("High Scores",VIEWPORT_WIDTH*48/1920);
            Label scoreLabel = createLabel("Score", fontSize+5);
            Label nameLabel=createLabel("Name", fontSize+5);
            Label trackLabel=createLabel("Song",fontSize+5);
            Label diffLabel=createLabel("Difficulty",fontSize+5);
            //table = new Table();
            table.setFillParent(true);

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

                /*Label score = createLabel(scores.get(i).getScore().toString(), fontSize);
                Label name=createLabel(scores.get(i).getName(), fontSize);
                Label track=createLabel(scores.get(i).getTrackName(),fontSize);
                Label difficulty=createLabel(scores.get(i).getDifficulty(),fontSize);

                table.row();
                table.add(name).uniform();
                table.add(score).uniform();
                table.add(track).uniform();
                table.add(difficulty).uniform();*/
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
        verticalGroup.center().padTop(300f*VIEWPORT_HEIGHT/1080).space(10f);
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
        Image logo=noteCollector.getAssetsManager().scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        noteCollector.getAssetsManager().setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
    }
    private void createButton(String text,float sizeX,float sizeY)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);
    }

    private void createMenuButton(String text,float sizeX,float sizeY)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        table.center();

        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton,text);
        table.add(MenuButton).size(sizeX,sizeY);
    }

    /*private void AddButtonListener(final ImageTextButton MenuButton){
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    //noteCollector.adsHandler.showAds(1);
                    table.addAction(Actions.fadeOut(0.4f));
                    exitBtnTable.addAction(Actions.fadeOut(0.4f));
                    Timer.schedule(new Timer.Task() {

                        @Override
                        public void run() {
                            dispose();
                            noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));

                        }

                    }, 0.4f);
                }
                return true;
            }

        });

    }*/

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
                    //table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));//Fade out table
                    exitBtnTable.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                    table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run()
                        {
                            dispose();
                            switch (text)
                            {
                                case "Practice":
                                    noteCollector.getDb().read(noteCollector.getAuth(),noteCollector,stage,false);
                                    break;
                                case "Competitive":
                                    noteCollector.getDb().read(noteCollector.getAuth(),noteCollector,stage,true);
                                    break;
                                case "Back":
                                    if(!selectMode)
                                    {
                                        noteCollector.setScreen(new ScoresScreen(noteCollector,stage));
                                    }
                                    else
                                    {
                                        noteCollector.setScreen(new ResultScreen(noteCollector,stage));
                                    }

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
}
