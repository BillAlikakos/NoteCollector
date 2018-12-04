package com.mygdx.notecollector.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Created by bill on 7/14/16.
 */
public  class Assets
{
    //class for managing assets
    private  AbsoluteFileHandleResolver fileHandleResolver;
    public AssetManager assetManager;
    public AssetManager assetManagerFiles;
    public AssetManager externalAssets;
    public String MusicName;
    private FreetypeFontLoader.FreeTypeFontLoaderParameter parameter;
    private Preferences prefs;
    private int viewportHeight;
    private BitmapFont font;
    private BitmapFont fontH;
    private BitmapFont fontList;
    private BitmapFont fontBig;
    private BitmapFont fontMid;
    private BitmapFont fontSm;
    private Texture logo;
    private Texture background;
    private Texture backgroundGame;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;//Gdx.graphics.getWidth();//
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;//Gdx.graphics.getHeight(); //
    private boolean isDark;

    public Assets() {
         fileHandleResolver = new AbsoluteFileHandleResolver();
         assetManager = new AssetManager();
         assetManagerFiles = new AssetManager(fileHandleResolver);
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
    }

    public void loadMenuBackground()//Must be called whenever the menu background is loaded
    {
        if(!prefs.getString("menuBackground").equals("data/ui/images/new1080.png"))
        {
            //System.out.println("External Asset");
            //AssetManager manager = new AssetManager(new ExternalFileHandleResolver());
            externalAssets = new AssetManager(new ExternalFileHandleResolver());
            File  bg = new File(prefs.getString("menuBackground"));
            //System.out.println(bg.getPath());
            externalAssets.load(bg.getPath(),Texture.class);
            externalAssets.finishLoading();
            //externalAssets.dispose();
        }
        else
        {
            LoadTexture(prefs.getString("menuBackground"));
            assetManager.finishLoading();
        }
    }

    public void LoadMenuAssets()
    {
        LoadTexture(Constants.ButtonImage);
        LoadTexture(Constants.logo);
        LoadTexture(Constants.text);
        LoadTexture(Constants.ButtonPressed);
        LoadTexture(Constants.exitImage);
        LoadTexture(Constants.scoresImage);
        LoadTexture(Constants.logInImage);
        LoadTexture(Constants.helpImage);
        LoadTexture(Constants.settingsImage);
        LoadTexture(Constants.exitImageP);
        LoadTexture(Constants.scoresImageP);
        LoadTexture(Constants.logInImageP);
        LoadTexture(Constants.helpImageP);
        LoadTexture(Constants.settingsImageP);
        assetManager.finishLoading();
    }
    public void LoadLogInAssets()
    {
        LoadTexture(Constants.showPasswordImage);
        LoadTexture(Constants.showPasswordImageP);
        assetManager.finishLoading();
    }

    public void LoadAssets()
    {
        LoadTexture(Constants.ButtonImage);
        LoadTexture(Constants.logo);
        LoadTexture(Constants.ButtonPressed);
        assetManager.finishLoading();
    }

    public void LoadWiFiAssets()
    {
        LoadTexture(Constants.noConn);
        assetManager.finishLoading();
    }

    public void LoadListAssets()
    {
        LoadTexture(Constants.SelectionColor);
        assetManager.load(Constants.skinAtlas, TextureAtlas.class);
        assetManager.load(Constants.Skin, Skin.class);
        assetManager.finishLoading();
    }
    public void LoadGameOverAssets()
    {
        LoadTexture(Constants.ButtonImage);
        LoadTexture(Constants.GameOver);
        LoadTexture(Constants.ButtonPressed);
        assetManager.finishLoading();
    }
    public void disposeMenuAssets()
    {
        this.font.dispose();
        this.fontH.dispose();
        this.fontList.dispose();
        this.logo.dispose();
        assetManager.unload(Constants.ButtonImage);
        assetManager.unload(Constants.logo);
        assetManager.unload(Constants.ButtonPressed);
    }
    public void disposeListMenuAssets()
    {
        this.logo.dispose();
        assetManager.unload(Constants.ButtonImage);
        assetManager.unload(Constants.logo);
        assetManager.unload(Constants.ButtonPressed);
        assetManager.unload(Constants.SelectionColor);
        assetManager.unload(Constants.skinAtlas);
        assetManager.unload(Constants.Skin);
    }
    public void disposeGameOverAssets()
    {
        this.logo.dispose();
        assetManager.unload(Constants.ButtonImage);
        assetManager.unload(Constants.GameOver);
        assetManager.unload(Constants.ButtonPressed);
    }
    public void disposeWifiAssets()
    {
        assetManager.unload(Constants.noConn);
    }
    public void disposeLogInAssets()
    {
        assetManager.unload(Constants.showPasswordImage);
        assetManager.unload(Constants.showPasswordImageP);
    }

    public void disposeMainMenuAssets()
    {
      System.out.println("Disposing icons");
        this.font.dispose();
        this.fontH.dispose();
        this.fontList.dispose();
        this.logo.dispose();
      assetManager.unload(Constants.ButtonImage);
      assetManager.unload(Constants.logo);
      assetManager.unload(Constants.text);
      assetManager.unload(Constants.ButtonPressed);
      assetManager.unload(Constants.exitImage);
      assetManager.unload(Constants.scoresImage);
      assetManager.unload(Constants.helpImage);
      assetManager.unload(Constants.settingsImage);
      assetManager.unload(Constants.exitImageP);
      assetManager.unload(Constants.scoresImageP);
      assetManager.unload(Constants.helpImageP);
      assetManager.unload(Constants.settingsImageP);
    }
    public void createFonts()//Generate font from ttf file
    {
        FileHandle fontFile = Gdx.files.internal("data/ui/fonts/helvetica.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 45;
        this.fontBig = generator.generateFont(parameter);
        parameter.size=25;
        this.fontMid = generator.generateFont(parameter);
        parameter.size=12;
        this.fontSm = generator.generateFont(parameter);
        generator.dispose();
    }

    public BitmapFont getFontBig()
    {
        return fontBig;
    }
    public BitmapFont getFontMid()
    {
        return fontMid;
    }
    public BitmapFont getFontSm()
    {
        return fontSm;
    }

    public void LoadLoadingAssets(String filepath)
    {
        LoadTexture(Constants.spinner);
        LoadGameAssets();
        loadMusic(filepath);
    }

    public void LoadX()
    {
        LoadTexture(Constants.X);
        assetManager.finishLoading();
    }
    public void disposeX()
    {
        assetManager.unload(Constants.X);
    }

    public void LoadSpinner()
    {
        LoadTexture(Constants.spinner);
    }


    private void LoadGameAssets()
    {
        LoadTexture(Constants.ButtonImage);
        //LoadTexture(Constants.BackgroundGame);
        if(!prefs.getString("gameBackground").equals("data/Game Images/backgrounGame.jpg"))
        {
            //System.out.println("External Asset");
            externalAssets = new AssetManager(new ExternalFileHandleResolver());

            File  bg = new File(prefs.getString("gameBackground"));
            //System.out.println(bg.getPath());
            externalAssets.load(bg.getPath(),Texture.class);
            //System.out.println("BG Loaded");
            System.out.println(bg.getAbsolutePath());
            externalAssets.finishLoading();
        }
        else
        {
            System.out.println("Internal Asset");
            LoadTexture(prefs.getString("gameBackground"));
            System.out.println("BG Loaded");
        }
        LoadTexture(Constants.square);
        if(prefs.getBoolean("normal")) {
            LoadTexture(Constants.Collector);
        }else if (prefs.getBoolean("big")){
            LoadTexture(Constants.BigCollector);
        }else if (prefs.getBoolean("vbig")){
            LoadTexture(Constants.VeryBigCollector);

        }
        LoadTexture(Constants.WhiteKey);
        LoadTexture(Constants.BlackKey);
        LoadTexture(Constants.WhitePressedKey);
        LoadTexture(Constants.BlackPressedKey);
        LoadTexture(Constants.ButtonImage);
        LoadTexture(Constants.ButtonPressed);
        assetManager.finishLoading();

    }
    public void disposeGameAssets()
    {
        System.out.println("Disposing GameAssets");
        if(!prefs.getString("gameBackground").equals("data/Game Images/backgrounGame.jpg"))
        {
            System.out.println(prefs.getString("gameBackground"));
            externalAssets.unload(prefs.getString("gameBackground"));
            externalAssets.dispose();
        }
        else
        {
            assetManager.unload(prefs.getString("gameBackground"));
        }
        assetManager.unload(Constants.square);
        //LoadTexture(Constants.square);
        if(prefs.getBoolean("normal"))
        {
            assetManager.unload(Constants.Collector);
        }
        else if (prefs.getBoolean("big"))
        {
            assetManager.unload(Constants.BigCollector);
        }
        else if (prefs.getBoolean("vbig"))
        {
            assetManager.unload(Constants.VeryBigCollector);
        }
        assetManager.unload(Constants.WhiteKey);
        assetManager.unload(Constants.BlackKey);
        assetManager.unload(Constants.WhitePressedKey);
        assetManager.unload(Constants.BlackPressedKey);
        assetManager.unload(Constants.ButtonImage);
        assetManager.unload(Constants.ButtonPressed);
    }
    public BitmapFont createBimapFont(int size ){

        com.mygdx.notecollector.fonts.SmartFontGenerator fontGen = new com.mygdx.notecollector.fonts.SmartFontGenerator();
        //FileHandle exoFile = Gdx.files.internal("data/ui/fonts/Ubuntu-I.ttf");
        FileHandle exoFile = Gdx.files.internal("data/ui/fonts/helvetica.ttf");
        //font=createFreetypeFont(size,true);
        return fontGen.createFont(exoFile, "exo-medium", size);
    }
    public BitmapFont createBitmapFont()
    {
        int fontSize=VIEWPORT_WIDTH*48/1920;//Set font size to the resolution (1920 is used as divider as it is considered the development resolution)
        //int fontSize=VIEWPORT_WIDTH/40;
        System.out.println(fontSize);
        font = this.createBimapFont(fontSize);
        return font;
    }
    public BitmapFont createFont()//Create standard size font for buttons etc.
    {
        int fontSize=VIEWPORT_WIDTH*48/1920;//Set font size to the resolution (1920 is used as divider as it is considered the development resolution)
        System.out.println("Standard font size:"+fontSize);
        font = this.createBimapFont(fontSize);
        //font=createFreetypeFont(fontSize,true);
        return font;
    }
    public BitmapFont createFontH()//Create font for headers
    {
       // int fontSize=VIEWPORT_WIDTH*64/1920;//Set font size to the resolution (1920 is used as divider as it is considered the development resolution)
        int fontSize=VIEWPORT_WIDTH*55/1920;//Set font size to the resolution (1920 is used as divider as it is considered the development resolution)
        System.out.println("Header font size:"+fontSize);
        fontH = this.createBimapFont(fontSize);
        //fontH=createFreetypeFont(fontSize,true);
        return fontH;
    }
    public BitmapFont createFontList()//Create font for lists
    {
        int fontSize=VIEWPORT_WIDTH*55/1920;//Set font size to the resolution (1920 is used as divider as it is considered the development resolution)
        System.out.println("List font size:"+fontSize);
        fontList = this.createBimapFont(fontSize);
        //fontList=createFreetypeFont(fontSize,true);
        return fontList;
    }

    public BitmapFont createFreetypeFont(int size)
    {
        //System.out.println("Creating font");
        FileHandle fontFile = Gdx.files.internal("data/ui/fonts/helvetica.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        //if(shadow)
        {
            //System.out.println("shadow");
            parameter.shadowColor = Color.BLACK;
            parameter.shadowOffsetX = 3;
            parameter.shadowOffsetY = 3;
        }
        this.font = generator.generateFont(parameter);
        generator.dispose();
        return  font;
    }
    public boolean getDark()
    {
        return isDark;
    }

    public void getBrightness(Pixmap pixmap)//https://stackoverflow.com/questions/13762864/image-dark-light-detection-client-sided-script
    {
        isDark=false;
        Color color = new Color();
        int max;
        int dark=0;
        int light=0;
        float diff;
        System.out.println(pixmap.getHeight());
        System.out.println(pixmap.getWidth());
        byte[] pixels;
        ByteBuffer buf;
        buf=pixmap.getPixels();
        //pixels=buf.array();
        /*for (byte val : pixels)
        {
            Color.rgba8888ToColor(color, val);
            int R = (int) (color.r * 255f);
            int G = (int) (color.g * 255f);
            int B = (int) (color.b * 255f);
            //int A = (int)(color.a * 255f);
            max = Math.max(Math.max(R, G), B);
            if (max < 128)
            {
                dark++;
            } else
            {
                light++;
            }
        }*/
        for(int i=0;i<pixmap.getWidth();i++)
        {
            for(int j=0;j<pixmap.getHeight();j++)
            {
                int val = pixmap.getPixel(i, j);
                Color.rgba8888ToColor(color, val);
                int R = (int)(color.r * 255f);
                int G = (int)(color.g * 255f);
                int B = (int)(color.b * 255f);
                //int A = (int)(color.a * 255f);
                max=Math.max(Math.max(R,G),B);
                if(max<128)
                {
                    dark++;
                }
                else
                {
                    light++;
                }
            }
        }
        diff=((light-dark)/(pixmap.getWidth()*pixmap.getHeight()));
        if(diff+0.1<0)
        {
            isDark=true;
        }
        /*System.out.println("Red Channel: "+data[0]);
        System.out.println("Green Channel: "+data[1]);
        System.out.println("Blue Channel: "+data[2]);*/
    }
    public Image scaleBackground(FileHandle file)
    {
        //FileHandle file = new FileHandle(Constants.getBackgroundMenu());
        Pixmap pixmap200;
        if(!prefs.getString("menuBackground").equals("data/ui/images/new1080.png"))//If a custom image is defined use external file loader
        {
             pixmap200 = new Pixmap(Gdx.files.external(file.path()));
            //Pixmap pixmap200 = new Pixmap(Gdx.files.internal("data/ui/images/new1080.png"));

        }
        else
        {
            pixmap200 = new Pixmap(Gdx.files.internal(file.path()));
        }
        Pixmap pixmap100 = new Pixmap(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, pixmap200.getFormat());//Scale background image to the device's resolution
        pixmap100.drawPixmap(pixmap200,
                0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
                0, 0, pixmap100.getWidth(), pixmap100.getHeight()
        );
        //getBrightness(pixmap100);
        //Texture texture = new Texture(pixmap100);
        background = new Texture(pixmap100);
        pixmap200.dispose();
        pixmap100.dispose();
        return new Image(background);
    }

    public Image scaleGameBackground(FileHandle file)
    {
        Pixmap pixmap200;
        if(!prefs.getString("menuBackground").equals("data/Game Images/backgrounGame.jpg"))//If a custom image is defined use external file loader
        {
            pixmap200 = new Pixmap(Gdx.files.external(file.path()));
            //Pixmap pixmap200 = new Pixmap(Gdx.files.internal("data/ui/images/new1080.png"));

        }
        else
        {
            pixmap200 = new Pixmap(Gdx.files.internal(file.path()));
        }
        Pixmap pixmap100 = new Pixmap(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, pixmap200.getFormat());//Scale background image to the device's resolution
        pixmap100.drawPixmap(pixmap200,
                0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
                0, 0, pixmap100.getWidth(), pixmap100.getHeight()
        );
        //getBrightness(pixmap100);
        //Texture texture = new Texture(pixmap100);
        backgroundGame = new Texture(pixmap100);
        pixmap200.dispose();
        pixmap100.dispose();
        return new Image(backgroundGame);
    }

    public Image scaleLogo(FileHandle file)//Use pixmap to scale logo for all resolutions
    {
        //Pixmap pixmap200= new Pixmap(Gdx.files.internal(Constants.logo));
        Pixmap pixmap200= new Pixmap(file);
        Pixmap pixmap100 = new Pixmap(1000*VIEWPORT_WIDTH/1080, 192*VIEWPORT_HEIGHT/720, Pixmap.Format.RGBA8888);//Scale background image to the device's resolution
        pixmap100.drawPixmap(pixmap200,
                0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
                0, 0, pixmap100.getWidth(), pixmap100.getHeight()
        );
        //pixmap200.setBlending(Pixmap.Blending.SourceOver);
        //Texture texture = new Texture(pixmap100);
        logo = new Texture(pixmap100);
        pixmap200.dispose();
        pixmap100.dispose();
        return new Image(logo);
    }

    public void setLogoPosition(VerticalGroup verticalGroup)
    {
        verticalGroup.center().padTop(VIEWPORT_HEIGHT*0.05f).space(10f);
    }

    public float[] setButtonSize(float sizeX,float sizeY)//Create different buttons for each resolution
    {
        sizeX=VIEWPORT_WIDTH*(0.185f);
        sizeY=VIEWPORT_HEIGHT*(0.17f);
        //System.out.println("sizeX2:"+sizeX+" sizeY2:"+sizeY);
        return new float[] {sizeX, sizeY};
    }


    private void setSize(int size) {

        parameter.fontParameters.size = size;
    }

    private void LoadTexture(String name){

        if (!assetManager.isLoaded(name))
            assetManager.load(name, Texture.class);
    }


    private  void loadMusic(String filepath)
    {
        fileHandleResolver = new AbsoluteFileHandleResolver();
        assetManagerFiles = new AssetManager(fileHandleResolver);

        File  music = new File(filepath);
        this.MusicName = music.getAbsolutePath();
        assetManagerFiles.load(music.getAbsolutePath(), Music.class);
        assetManagerFiles.finishLoading();

    }

    private  void loadMusicMultiplayer(File file)
    {
        fileHandleResolver = new AbsoluteFileHandleResolver();
        assetManagerFiles = new AssetManager(fileHandleResolver);

        File  music = file;
        this.MusicName = music.getAbsolutePath();
        assetManagerFiles.load(music.getAbsolutePath(), Music.class);
        assetManagerFiles.finishLoading();

    }


public  void dispose()
{
    assetManager.dispose();
    assetManagerFiles.dispose();
}

}
