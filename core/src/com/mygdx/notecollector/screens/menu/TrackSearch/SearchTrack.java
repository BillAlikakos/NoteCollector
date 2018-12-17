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
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class SearchTrack implements Screen {

    private Assets AssetsManager;
    private Stage stage;
    private Viewport viewport;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Table table;
    private TextField textField;

    private TextureRegionDrawable selectionColor;
    private TextureRegionDrawable selectionColorPressed;
    //private BitmapFont font;
    private Image icon;
    private Label err1;
    private Label err2;
    private Label search;
    private Label noRes;
    private NoteCollector noteCollector;
    private TextureRegionDrawable selectionColorList;
    private boolean touched;
    private VerticalGroup verticalGroup;
    private float[] size;
    private float sizeX;
    private float sizeY;
    private ImageTextButton ByName;
    private ImageTextButton ByArtist;
    private boolean searchByName;
    private ArrayList<SongObj> songs;
    private boolean networkAccess;
    private String query;
    private boolean isResult;

    public SearchTrack(NoteCollector noteCollector,Stage stage)
    {
        this.stage=stage;
        this.noteCollector = noteCollector;
        touched = false;
        AssetsManager = noteCollector.getAssetsManager();
        isResult=true;
        //searchByName=true;//Preset
        LoadAssets();
    }

    @Override
    public void show()
    {
        networkAccess=noteCollector.getWifiCtx();
        createTable();
        createLogo();
        size = AssetsManager.setButtonSize(sizeX, sizeY);//Get the dimensions for the button
        sizeX = size[0];
        sizeY = size[1];
        createButton("Back", 0f*VIEWPORT_WIDTH/1920, 0f*VIEWPORT_HEIGHT/1080, sizeX, sizeY);
        if(networkAccess)
        {
            //float Yaxis=300*VIEWPORT_HEIGHT/1080;
            //noRes=createLabel("",Yaxis);
            createTextField();
            search=createLabel("Search For Track", stage.getCamera().viewportHeight / 2 + 135*VIEWPORT_HEIGHT/1080);
            stage.addActor(search);
            createButton("Submit", ((stage.getCamera().viewportWidth / 2) - sizeX / 2), 410f*VIEWPORT_HEIGHT/1080, sizeX, sizeY);
            stage.addActor(table);
            table.getColor().a=0;
            search.getColor().a=0;
            textField.getColor().a=0;
            table.addAction(Actions.fadeIn(0.2f));
            search.addAction(Actions.fadeIn(0.2f));
            textField.addAction(Actions.fadeIn(0.2f));
        }
        else
        {

            stage.addActor(table);
            createImg();
            err1=createLabel("Network Connection Unavailable", (stage.getCamera().viewportHeight / 2 + 100*VIEWPORT_HEIGHT/1080));
            err2=createLabel("Please Connect to a Network", (stage.getCamera().viewportHeight / 2 + 65*VIEWPORT_HEIGHT/1080));
            stage.addActor(err1);
            stage.addActor(err2);
            err1.getColor().a=0;
            err2.getColor().a=0;
            table.getColor().a=0;
            table.addAction(Actions.fadeIn(0.2f));
            err1.addAction(Actions.fadeIn(0.2f));
            err2.addAction(Actions.fadeIn(0.2f));
        }
        Gdx.input.setInputProcessor(stage);
    }

    private void createImg()
    {
        Texture img = AssetsManager.assetManager.get(Constants.noConn);
        icon = new Image(img);
        float Xaxis=(stage.getCamera().viewportWidth - 375*VIEWPORT_WIDTH/1920)/2;
        float Yaxis=stage.getCamera().viewportHeight/2-325*VIEWPORT_HEIGHT/1080;
        icon.setScale(.5f*VIEWPORT_WIDTH/1920,.5f*VIEWPORT_HEIGHT/1080);
        icon.setPosition(Xaxis,Yaxis);
        stage.addActor(icon);
        icon.getColor().a=0;
        icon.addAction(Actions.fadeIn(0.2f));
    }


    private void addListener() {
        //if textbox is clicked, show the keyboard
        textField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                if (!touched)
                    touched = true;
                Gdx.input.setOnscreenKeyboardVisible(true);


            }
        });
    }

    private void createTextField() {
        textField = new TextField("", createTextFieldStyle());
        textField.setHeight(100*VIEWPORT_HEIGHT/1080);
        textField.setWidth(360*VIEWPORT_WIDTH/1920);
        float Xaxis=(stage.getCamera().viewportWidth - 360*VIEWPORT_WIDTH/1920) / 2;
        float Yaxis=(stage.getCamera().viewportHeight)/2+ 50*VIEWPORT_HEIGHT/1080;
        textField.setPosition(Xaxis, Yaxis);

        addListener();
        table.addActor(textField);
    }

    private TextField.TextFieldStyle createTextFieldStyle()//Method to create the style for the submission text field
    {
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        selectionColorList = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.SelectionColor, Texture.class)));
        textFieldStyle.background = selectionColorList;
        textFieldStyle.font = AssetsManager.getFont();
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.messageFont = AssetsManager.getFont();
        textFieldStyle.messageFontColor = Color.WHITE;

        return textFieldStyle;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        // if  textfield is not touched hide keyboard
        if (Gdx.input.isTouched() && touched) {
            Gdx.input.setOnscreenKeyboardVisible(false);
            stage.unfocus(textField);
            touched = false;
        }

    }

    @Override
    public void resize(int width, int height) {

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
        AssetsManager.disposeListMenuAssets();
        AssetsManager.disposeWifiAssets();
        //stage.dispose();
        table.clear();
        if(!networkAccess)
        {
            stage.getRoot().removeActor(icon);
        }
        if(!isResult)
        {
            stage.getRoot().removeActor(noRes);
        }
        stage.getRoot().removeActor(table);
        stage.getRoot().removeActor(verticalGroup);
        //font.dispose();

    }

    private Label createLabel(String text, float Yaxis) //Method for label creation
    {
        Label.LabelStyle labelstyle = new Label.LabelStyle(AssetsManager.getFont(), Color.WHITE);
        Label label = new Label(text, labelstyle);
        label.setPosition(((stage.getCamera().viewportWidth - label.getWidth()) / 2), (Yaxis + 50*VIEWPORT_HEIGHT/1080));
       // stage.addActor(label);
        return label;
    }

    private void createTable() {
        table = new Table();
        table.center();
        table.setFillParent(true);
        table.pad(10f*VIEWPORT_HEIGHT/1080, 10f*VIEWPORT_WIDTH/1920, 10f*VIEWPORT_HEIGHT/1080, 10f*VIEWPORT_WIDTH/1920);
        //table.setTouchable(Touchable.enabled);
    }

    private void LoadAssets()
    {
        AssetsManager.LoadAssets();
        AssetsManager.LoadListAssets();
        AssetsManager.LoadWiFiAssets();
        //font = AssetsManager.createBitmapFont();
        selectionColor = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage, Texture.class)));
        selectionColor.setRightWidth(7f);
        selectionColor.setBottomHeight(2f);
        selectionColorPressed = new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonPressed, Texture.class)));
        selectionColorPressed.setRightWidth(7f);
        selectionColorPressed.setBottomHeight(2f);


    }

    private void createLogo() {
        Image logo=noteCollector.getAssetsManager().scaleLogo(Gdx.files.internal(Constants.logo));
        verticalGroup  = new VerticalGroup();
        verticalGroup.setFillParent(true);
        verticalGroup.center();
        verticalGroup.addActor(logo);
        noteCollector.getAssetsManager().setLogoPosition(verticalGroup);
        stage.addActor(verticalGroup);
        // addVerticalGroup(background);
    }

    private void createButton(String text, float Xaxis, float y, float sizeX, float sizeY) {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = createButtonStyle(selectionColor);
        ImageTextButton MenuButton = new ImageTextButton(text, textButtonStyle);
        AddButtonListener(MenuButton, text);
        MenuButton.setPosition(Xaxis, y);
        MenuButton.setSize(sizeX, sizeY);
        table.add(MenuButton).size(sizeX, sizeY);
        table.addActor(MenuButton);
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
                    if(text.equals("Search By Name"))
                    {
                        searchByName=true;
                        ByName.setStyle(createButtonStyle(selectionColorPressed));
                        ByArtist.setStyle(createButtonStyle(selectionColor));
                    }
                    if(text.equals("Search By Artist"))
                    {
                        searchByName=false;
                        ByArtist.setStyle(createButtonStyle(selectionColorPressed));
                        ByName.setStyle(createButtonStyle(selectionColor));
                    }

                    if(!networkAccess)
                    {
                        err1.addAction(Actions.fadeOut(0.4f));
                        err2.addAction(Actions.fadeOut(0.4f));
                        icon.addAction(Actions.fadeOut(0.4f));
                        table.addAction(Actions.fadeOut(0.4f));
                    }
                    if(networkAccess && text.equals("Back"))
                    {
                        table.addAction(Actions.fadeOut(0.4f));
                        search.addAction(Actions.fadeOut(0.4f));
                        textField.addAction(Actions.fadeOut(0.4f));
                        if(!isResult)
                        {
                            noRes.addAction(Actions.fadeOut(0.4f));
                        }
                    }
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if (text.equals("Submit"))
                            {
                                System.out.println(textField.getText());
                                sendRequest();
                            }
                            else if(text.equals("Back"))
                            {
                                noteCollector.setScreen(new MainMenuScreen(noteCollector,stage));
                            }

                        }

                    }, 0.4f);
                }
                return true;
            }

        });

    }

    private void sendRequest()
    {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        query=textField.getText();
        //Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url("http://www.midiworld.com/search/?q="+textField.getText()).header("Content-Type", "application/json").header("Accept", "application/json").build();
        if(textField.getText().contains(" "))
        {
            query=textField.getText().replace(' ','+');
            System.out.println(query);
        }
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url("http://www.midiworld.com/search/?q="+query).build();
        final long start = System.nanoTime(); //for checking the time until response
        //System.out.println(request.getUrl().toString());
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener()
        {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse)//Log response
            {
                Gdx.app.log("WebRequest", "HTTP Response code: " + httpResponse.getStatus().getStatusCode());
               // Gdx.app.log("WebRequest", "HTTP Response code: " + httpResponse.getResultAsString());
                Gdx.app.log("WebRequest", "Response time: " + ((System.nanoTime() - start) / 1000000) + "ms");
                String response = httpResponse.getResultAsString();
                //System.out.println(response.toString());//Prints out the html response tree
                Document doc=Jsoup.parse(response);//Parse html response
                Element content=doc.getElementById("content");//Get content div
                Element page=content.getElementById("page");
                System.out.println(page.text()) ;
                String result=page.text();
                if(result.equalsIgnoreCase("Search result: found nothing!"))
                {
                    if(isResult)
                    {
                        float Yaxis=300*VIEWPORT_HEIGHT/1080;
                        noRes=createLabel("No results for your query",Yaxis);
                        stage.addActor(noRes);
                        noRes.getColor().a=0;
                        noRes.addAction(Actions.fadeIn(0.1f));
                        isResult=false;
                    }
                }
                else
                {
                    Element songList=content.getElementsByTag("ul").first();//Get the first list that contains the search result
                    //System.out.println(songList.getAllElements());
                    Elements songItems=songList.getElementsByTag("li");//Get all list items
                    songs=new ArrayList<>();
                    for(int i=0;i<songItems.size();i++)
                    {
                        //System.out.println(songs.get(i));
                        String[] lines=songItems.get(i).text().split("-");//Split text on "-" character to keep only the song title and the artist name
                        Elements url=songItems.get(i).getElementsByTag("a");//Get the anchor elements of each song
                        String[] address=url.toString().split("\"");//Split on "" to get the address of the file
                        SongObj song=new SongObj(lines[0],address[1]);
                        songs.add(song);
                    }
                    if(songs.size()!=0)
                    {
                        table.addAction(Actions.fadeOut(0.4f));
                        search.addAction(Actions.fadeOut(0.4f));
                        textField.addAction(Actions.fadeOut(0.4f));
                        if(!isResult)
                        {
                            noRes.addAction(Actions.fadeOut(0.4f));
                        }

                    }


                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run()
                        {
                            dispose();
                            if(songs.size()!=0)
                            {
                                noteCollector.setScreen(new Results(noteCollector,songs,stage));
                            }

                        }
                    }, 0.4f);
                }


            }

            @Override
            public void failed(Throwable t)
            {
                if(isResult)
                {
                    Gdx.app.log("WebRequest", "HTTP request failed");
                    float Yaxis=300*VIEWPORT_HEIGHT/1080;
                    noRes=createLabel("HTTP Request failed. Please try again.",Yaxis);
                    stage.addActor(noRes);
                    noRes.getColor().a=0;
                    noRes.addAction(Actions.fadeIn(0.1f));
                    isResult=false;
                }
            }

            @Override
            public void cancelled() {
                Gdx.app.log("WebRequest", "HTTP request cancelled");
            }
        });
    }


    private ImageTextButton.ImageTextButtonStyle createButtonStyle(TextureRegionDrawable ButtonImage) {
        ImageTextButton.ImageTextButtonStyle textButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        textButtonStyle.up = ButtonImage;
        textButtonStyle.down = selectionColorPressed;
        textButtonStyle.over = ButtonImage;
        textButtonStyle.font = AssetsManager.getFont();
        return textButtonStyle;
    }

}
