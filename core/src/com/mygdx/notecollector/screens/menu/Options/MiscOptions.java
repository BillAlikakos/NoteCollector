package com.mygdx.notecollector.screens.menu.Options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.mygdx.notecollector.IGallery;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;
import com.mygdx.notecollector.screens.menu.OptionsScreen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class MiscOptions implements Screen
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
    private Table table;
    private Table exitBtnTable;
    private ImageTextButton menuBg,gameBg;
    private float sizeX;
    private float sizeY;
    private float[] size;
    private Preferences prefs;
    private IGallery gallery;
    private String res;

    public MiscOptions(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector=noteCollector;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();
        font = assetsManager.createBitmapFont();
        table = new Table();
        exitBtnTable=new Table();
        LoadAssets();
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
    }

    private void LoadAssets()
    {
        assetsManager.LoadAssets();
        font=assetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);
    }

    @Override
    public void show()
    {
        size=assetsManager.setButtonSize(sizeX,sizeY);
        sizeX=size[0];
        sizeY=size[1];
        Gdx.input.setInputProcessor(stage);
        createTable();
        createLogo();
        createCustomBg();
        stage.addActor(table);
        stage.addActor(exitBtnTable);
        createBackButton("Menu");
        table.getColor().a=0;//Fade table in
        exitBtnTable.getColor().a=0;//Fade table in
        table.addAction(Actions.fadeIn(0.2f));
        exitBtnTable.addAction(Actions.fadeIn(0.2f));
    }

    private void createTable()
    {
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.padTop(10f*VIEWPORT_HEIGHT/1080);
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

    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        textButtonStyle.up = ButtonImage;
        textButtonStyle.down = selectionColorPressed;
        textButtonStyle.over = ButtonImage;
        textButtonStyle.font = font;
        return textButtonStyle;
    }

    private Label createLabel(String text)
    {
        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;
    }

    private void createBackButton(String text)
    {
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        exitBtnTable.bottom().left();
        exitBtnTable.setFillParent(true);
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton);
        exitBtnTable.add(MenuButton).bottom().left().padRight(5f).size(sizeX,sizeY);
    }

    private ImageTextButton createButton(String text,ImageTextButton.ImageTextButtonStyle textButtonStyle )
    {
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        return MenuButton;
    }

    private void fadeBackground()
    {
        FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
        Image background=noteCollector.getAssetsManager().scaleBackground(file);
        stage.addActor(background);
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(2f)));
        background.getColor().a=0;
        background.addAction(Actions.fadeIn(0.2f));
    }

    private void AddButtonListener(final ImageTextButton MenuButton)
    {
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    table.addAction(Actions.fadeOut(0.4f));
                    exitBtnTable.addAction(Actions.fadeOut(0.4f));
                    Timer.schedule(new Timer.Task() {

                        @Override
                        public void run() {
                            FileHandle file = Gdx.files.internal(Constants.getBackgroundMenu().toString());
                            Image background=noteCollector.getAssetsManager().scaleBackground(file);
                            stage.addActor(background);
                            dispose();
                            noteCollector.setScreen(new OptionsScreen(noteCollector,stage));

                        }

                    }, 0.4f);

                }
                return true;
            }

        });
    }

    private void createCustomBg()//Creates the buttons for the custom backgrounds
    {
        Label title = createLabel("Custom Backgrounds:");
        table.row().padTop(20f);
        table.add(title).padRight(5f);
        ImageTextButton.ImageTextButtonStyle NormalStyle;
        NormalStyle = createButtonStyle(selectionColor);
        menuBg = createButton("Menu", NormalStyle);
        gameBg = createButton("In-Game", NormalStyle);

        menuBg.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                System.out.println("Calling interface");
                Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        System.out.println(this.isInterrupted());
                        gallery=noteCollector.getGallery();
                        gallery.getImagePath();
                        res="";
                        while (!interrupted())
                        {
                            res=gallery.getSelectedFilePath();
                            if(res!=null)
                            {
                                System.out.println("Selected img: "+res);
                                //FileHandle from = Gdx.files.external(res);//Get the path
                                FileHandle from = Gdx.files.external(res);//Get the path
                                File f= new File(res);//Get the file from supplied uri
                                //System.out.println("Got the Path 2 "+f.getPath());

                                byte[] data = new byte[625000];//5MByte array for image

                                try
                                {
                                    data = readFileToByteArray(f);//Read image as bytes
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                final File img = new File(Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").file().getAbsolutePath());//Destination file
                                String a=Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").file().getAbsolutePath();
                                String b=Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").file().toString();
                                String c=Gdx.files.external("Note Collector/Custom Images/menu_image.jpg").toString();
                                System.out.println("The abspath is "+a);
                                System.out.println("path is "+b);
                                System.out.println("str is "+c);
                                try
                                {
                                    FileUtils.writeByteArrayToFile(img,data);//Write file from byte array
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                System.out.println("Setting Background Image");
                                Constants.setBackgroundMenu("Note Collector/Custom Images/menu_image.jpg");
                                System.out.println(Constants.getBackgroundMenu());
                                gallery.clearSelectedPath();
                                this.interrupt();
                            }
                        }
                    }
                };

                t.start();

                return true;
            }


        });
        gameBg.addListener(new ClickListener()
        {


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                System.out.println("Calling interface");
                Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        gallery=noteCollector.getGallery();
                        gallery.getImagePath();
                        String res;
                        while (!interrupted())
                        {
                            res=gallery.getSelectedFilePath();

                            if(res!=null)
                            {
                                System.out.println("Selected img: "+res);
                                //FileHandle from = Gdx.files.external(res);//Get the path
                                FileHandle from = Gdx.files.external(res);//Get the path
                                File f= new File(res);//Get the file from supplied uri
                                //System.out.println("Got the Path 2 "+f.getPath());

                                byte[] data = new byte[625000];//5MByte array for image
                                try
                                {
                                    data = readFileToByteArray(f);//Read image as bytes
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                final File img = new File(Gdx.files.external("Note Collector/Custom Images/game_image.jpg").file().getAbsolutePath());//Destination file
                                String a=Gdx.files.external("Note Collector/Custom Images/game_image.jpg").file().getAbsolutePath();
                                String b=Gdx.files.external("Note Collector/Custom Images/game_image.jpg").file().toString();
                                String c=Gdx.files.external("Note Collector/Custom Images/game_image.pg").toString();
                                System.out.println("The abspath is "+a);
                                System.out.println("path is "+b);
                                System.out.println("str is "+c);
                                try
                                {
                                    FileUtils.writeByteArrayToFile(img,data);//Write file from byte array
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                System.out.println("Setting Background Image");
                                Constants.setBackgroundGame("Note Collector/Custom Images/game_image.jpg");
                                System.out.println(Constants.getBackgroundGame());
                                gallery.clearSelectedPath();
                                this.interrupt();
                            }
                        }
                    }
                };

                t.start();

                return true;
            }


        });
        table.add(menuBg).padRight(0.5f).size(sizeX,sizeY);
        table.add(gameBg).padRight(0.7f).size(sizeX,sizeY);
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
        //stage.dispose();
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(exitBtnTable);
        table.clear();
    }
}
