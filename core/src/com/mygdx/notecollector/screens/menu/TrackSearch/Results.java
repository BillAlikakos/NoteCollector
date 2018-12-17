package com.mygdx.notecollector.screens.menu.TrackSearch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
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

import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.SongObj;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;



public class Results implements Screen {

    private Viewport viewport;
    private Stage stage;
    //private BitmapFont font, fontList, fontH;
    private Texture img;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private NoteCollector noteCollector;
    private Assets assetsManager;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private TextureRegionDrawable selectionColorList;
    private Table table;
    private ScrollPane scrollPane;
    private List<Object> list;
    private Skin skin;
    private ArrayList<String> item = null;

    private Table btn;
    private Label label;
    private float[] size;
    private float sizeX;
    private float sizeY;
    private VerticalGroup verticalGroup;
    private String root=Constants.root;
    private ArrayList<SongObj> songs;

    public Results(NoteCollector noteCollector, ArrayList<SongObj> songList,Stage stage)
    {
        this.noteCollector = noteCollector;
        this.songs=songList;
        this.stage=stage;
        assetsManager = noteCollector.getAssetsManager();

        LoadAssets();
        ListStyle();
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
        btn = new Table();
        btn.setFillParent(true);
        createTable();
        createList();
        getSongs();
        createLogo();
        size = assetsManager.setButtonSize(sizeX, sizeY);
        sizeX = size[0];
        sizeY = size[1];
        table.add(createLabel("Select a Song:")).padTop(70f*VIEWPORT_HEIGHT/1080);
        table.row();
        createScrollPane();

        ImageTextButton back = createButton("Back");
        btn.left();
        btn.add(back).bottom().left().expand().size(sizeX, sizeY);
        //btn.setDebug(true);
        //table.setDebug(true);
        stage.addActor(btn);

        stage.addActor(verticalGroup);
        stage.addActor(table);
        table.getColor().a=0;//Set actor's alpha value to 0(Transparent) to enable fading
        btn.getColor().a=0;
        table.addAction(Actions.sequence(Actions.fadeIn(0.2f)));
        btn.addAction(Actions.sequence(Actions.fadeIn(0.2f)));

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

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
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
        assetsManager.disposeListMenuAssets();
        table.clear();
        stage.getRoot().removeActor(verticalGroup);
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(btn);
        //font.dispose();
        //fontList.dispose();
    }

    //create a table to organize buttons and list of tracks
    private void createTable() {
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(200f*VIEWPORT_HEIGHT/1080, 10f*VIEWPORT_WIDTH/1920, 115f*VIEWPORT_HEIGHT/1080, 10f*VIEWPORT_WIDTH/1920);
        //table.setDebug(true);
    }

    private ImageTextButton createButton(String text)
    {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);

        AddButtonListener(MenuButton, text);
        return MenuButton;
    }

    private void AddButtonListener(final ImageTextButton MenuButton, final String text) {
        MenuButton.addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

                if (MenuButton.isPressed()) {
                    if (prefs.getBoolean("sound")) {
                        noteCollector.getClick().play();
                    }
                    table.addAction(Actions.fadeOut(0.4f));
                    btn.addAction(Actions.fadeOut(0.4f));
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if (text.equals("Back"))
                            {
                                dispose();
                                noteCollector.setScreen(new SearchTrack(noteCollector,stage));
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


    private void LoadAssets() {
        assetsManager.LoadAssets();
       // fontH = assetsManager.createBimapFont(50*VIEWPORT_WIDTH/1920);
        //font = assetsManager.createBitmapFont();
        selectionColor = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage, Texture.class)));
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed, Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);


    }

    private void ListStyle() {
        assetsManager.LoadListAssets();
        //fontList = assetsManager.createBitmapFont();
        selectionColorList = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.SelectionColor, Texture.class)));
        skin = assetsManager.assetManager.get(Constants.Skin, Skin.class);
    }

    private void createList() {
        list = new List<>(skin);
        list.getStyle().selection = selectionColorList;
        list.getStyle().font = assetsManager.getFontH();
        list.getStyle().selection.setTopHeight(10*VIEWPORT_HEIGHT/1080);
        addListener(list);
    }

    private void createScrollPane() {
        scrollPane = new ScrollPane(list);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setScrollingDisabled(true, false);
        table.add(scrollPane).expand().fill().padBottom(50f*VIEWPORT_HEIGHT/1080);
        table.row();
    }


    private void addListener(Actor actor) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {

                SongObj s = songs.get(list.getSelectedIndex());//Get the selected track
                String name = item.get(list.getSelectedIndex());//Get the selected instrument
                System.out.println("Selected Song: " +name);
                if(name.contains("/"))
                    name = name.replace("/", " ");
                System.out.println("Song Url: " + s.getUrl());
                downloadFile(s.getUrl(),name);
                //Hide tables and show message of completion

                //btn.clear();
                //table.clear();
                table.addAction(Actions.fadeOut(0.4f));//Fade table out
                btn.addAction(Actions.fadeOut(0.4f));
                label=createLabel("File downloaded");
                //stage.addActor(createLabel("File downloaded"));
                stage.addActor(label);
                label.getColor().a=0;
                label.addAction(Actions.fadeIn(0.2f));
                label.addAction(Actions.fadeOut(0.4f));
                Timer.schedule(new Timer.Task()//Wait for 0,4s and then return to main menu
                {
                    @Override
                    public void run()
                    {
                        dispose();
                        noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));
                    }
                }, 0.4f);
            }
        });
    }

    private void downloadFile(String url, final String name)//Method to request and download selected file from list
    {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setTimeOut(2500);
        request.setUrl(url);

        // Send the request, listen for the response
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener()
        {
            @Override
            public void handleHttpResponse (Net.HttpResponse httpResponse)
            {
                System.out.println("Sending request");
                // Determine the size of the file we have to download
                long length = Long.parseLong(httpResponse.getHeader("Content-Length"));

                // Create streams for external storage
                System.out.println("Creating streams");
                InputStream is = httpResponse.getResultAsStream();
                OutputStream os = Gdx.files.external("/Note Collector/Sample Tracks/"+name+".midi").write(false);
                System.out.println("Writing file");
                byte[] bytes = new byte[125000];//1,25 Mb buffer
                int count;
                long read = 0;
                try
                {
                    // Keep reading bytes from inputStream and storing them until there are no more.
                    while ((count = is.read(bytes, 0, bytes.length)) != -1)
                    {
                        os.write(bytes, 0, count);
                        read += count;
                    }
                    System.out.println("File downloaded");
                }
                catch (IOException e)
                {
                    System.out.println("IOException");
                }
            }
            @Override
            public void failed(Throwable t)
            {
                Gdx.app.log("WebRequest", "HTTP request failed");
            }

            @Override
            public void cancelled() {
                Gdx.app.log("WebRequest", "HTTP request cancelled");
            }
        });
    }

    private Label createLabel(String text)
    {
        Label.LabelStyle labelstyle = new Label.LabelStyle(assetsManager.getFontH(), Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        fileLabel.setPosition((stage.getCamera().viewportWidth/2)-fileLabel.getWidth()/2,stage.getCamera().viewportHeight/2);
        return fileLabel;

    }

    private void getSongs()//Method that finds all the instruments that are used in each track
    {
        item=new ArrayList<>();
        for (int i = 0; i < songs.size(); i++)
        {
            //System.out.println(songs.get(i).getName());
            item.add(songs.get(i).getName());
        }
        list.setItems(item.toArray());
    }


}
