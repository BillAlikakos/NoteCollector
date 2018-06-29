package com.mygdx.notecollector.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
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

/**
 * Created by bill on 7/26/16.
 */
public class Pause extends Stage  {

    public  VerticalGroup verticalGroup;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private BitmapFont font;
    private ImageTextButton resume;
    private ImageTextButton menu;
    private ImageTextButton exit;
    private Assets AssetsManager;
    private boolean paused;
    private Stage stage;
    private Viewport viewport;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    public Pause(NoteCollector noteCollector) {
        //constructor of pause screen
        this.AssetsManager = noteCollector.getAssetsManager();
        font = AssetsManager.createBimapFont(45);
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);
        createVerticalGroup();
        resume = createButton("Resume");
        resume.setPosition(200,200);

        setupCamera();
        stage.addActor(resume);
      

    }

    private void createVerticalGroup(){
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center().padTop(150f).space(10f);

    }

    private  void addVerticalGroup(Actor actor){
        verticalGroup.addActor(actor);

    }

    private ImageTextButton createButton(String text){
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        return MenuButton;
    }

    private void AddButtonListener(final ImageTextButton MenuButton, final String text){
        // selection when user push a button resume or 
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {


                if (MenuButton.isPressed())
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            switch (text) {
                                case "Resume":
                                    break;
                                case "Exit":
                                    Gdx.app.exit();
                                    break;
                                case "Scores":
                                    break;

                            }
                        } },0.4f);
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
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private void setupCamera(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }
}
