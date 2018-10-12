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
import com.esotericsoftware.kryonet.Client;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class MultiplayerClientScreen implements  Screen
{
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font, fontList, fontH;


    private static  int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static  int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private NoteCollector noteCollector;
    private Assets assetsManager;
    private Texture img;
    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    private TextureRegionDrawable selectionColorList;
    private Table table;
    private ScrollPane scrollPane;
    private List<Object> list;
    private Skin skin;
    private ArrayList<InetAddress> hosts = null;
    private ArrayList<String> names = null;
    private Table btn;
    private float[] size;
    private float sizeX;
    private float sizeY;
    private VerticalGroup verticalGroup;
    private Client c;
    private ClientClass client;
    private boolean mode;


    public MultiplayerClientScreen(NoteCollector noteCollector,Stage stage)
    {
        this.noteCollector = noteCollector;
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
        client=new ClientClass();
        getHosts();
        createLogo();
        size = assetsManager.setButtonSize(sizeX, sizeY);
        sizeX = size[0];
        sizeY = size[1];
        table.add(createLabel("Available Peers:")).padTop(70f);
        table.row();
        table.padBottom(50f);
        createScrollPane();
        ImageTextButton back = createButton("Back");
        btn.left();
        btn.add(back).bottom().left().expand().size(sizeX, sizeY);
        //btn.setDebug(true);

        stage.addActor(verticalGroup);
        stage.addActor(table);
        stage.addActor(btn);
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
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(btn);
        stage.getRoot().removeActor(verticalGroup);
        font.dispose();
        fontList.dispose();

    }
    //create a table for organize buttons and list of tracks
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
                                c.close();
                                dispose();
                                noteCollector.setScreen(new MultiplayerModeScreen(noteCollector,stage));
                            }
                        }
                    }, 0.4f);//prev 0.15f
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
        fontH = assetsManager.createBimapFont(45*VIEWPORT_WIDTH/1920);
        font = assetsManager.createBitmapFont();
        selectionColor =new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.ButtonPressed,Texture.class)));
        selectionColorPressed.setRightWidth(5f);
        selectionColorPressed.setBottomHeight(2f);


    }

    private void ListStyle(){
        assetsManager.LoadListAssets();
        fontList = assetsManager.createBitmapFont();
        selectionColorList = new TextureRegionDrawable(new TextureRegion(assetsManager.assetManager.get(Constants.SelectionColor,Texture.class)));
        skin = assetsManager.assetManager.get(Constants.Skin,Skin.class);
    }

    private void createList()  {
        list = new List<Object>(skin);
        list.getStyle().selection = selectionColorList;
        list.getStyle().font = fontList;
        addListener(list);
    }

    private void createScrollPane(){
        scrollPane = new ScrollPane(list);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setScrollingDisabled(true,false);
        table.add(scrollPane).expand().fill().padBottom(50f);
        table.row();
    }


    private void addListener(Actor actor){
        actor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                String hostName=names.get(list.getSelectedIndex());
                String address=hosts.get(list.getSelectedIndex()).toString();
                InetAddress adr=hosts.get(list.getSelectedIndex());
                System.out.println("Selected host:"+hostName);
                System.out.println("Host address:"+address);
                client.connect(adr);//Connect to selected device
                table.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                btn.addAction(Actions.sequence(Actions.fadeOut(0.4f)));
                Timer.schedule(new Timer.Task()
                {
                    @Override
                    public void run()
                    {
                        dispose();
                        noteCollector.setScreen(new MultiplayerWaitingScreen(noteCollector,client,stage));
                    }
                }, 0.4f);//prev 0.2f

            }
        });
    }
    private Label createLabel(String text){
        Label.LabelStyle labelstyle = new Label.LabelStyle(fontH, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;

    }
    private void getHosts()//Method to acquire a list of available servers
    {
        hosts = new ArrayList<InetAddress>();
        names = new ArrayList<String>();

            final ClientClass client= new ClientClass();
            hosts=client.discoverServers();

        if(hosts.size()==0)
            {
                Thread t=new Thread()
                {
                    @Override public void run()//Start a thread to manage host discovery
                    {
                        while(hosts.size()==0)
                        {
                            hosts=client.discoverServers();
                            if(hosts.size()!=0)
                            {
                                this.interrupt();
                                addToList();
                            }
                        }
                    }
                };
                t.start();

            }
            else
            {
                addToList();

            }


    }

    private void addToList()//Method to display discovered hosts
    {
        c=client.getClient();
        for (int i = 0; i <hosts.size() ; i++)
        {
            System.out.println(i);

            System.out.println("Hosts.get(i) "+hosts.get(i));
            System.out.println("getHostName "+hosts.get(i).getHostName());
            if(hosts.get(i).getHostName().equalsIgnoreCase("localhost"))//Ignore localhost listing
            {
                continue;
            }
            names.add(hosts.get(i).getHostName());

        }
        list.setItems(names.toArray());
    }

}