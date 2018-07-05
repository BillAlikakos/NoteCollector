package com.mygdx.notecollector.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by bill on 7/19/16.
 */
public class IntroScreen implements Screen {


    private NoteCollector noteCollector;
    private Viewport viewport;
    private Stage stage;
    private Image background;
    private Texture img;
    private Preferences prefs;


    public IntroScreen(NoteCollector noteCollector)
    {

        this.noteCollector =noteCollector;
        noteCollector.getAssetsManager().assetManager.load(Constants.IntroImage, Texture.class);
        noteCollector.getAssetsManager().assetManager.finishLoading();
        noteCollector.getAssetsManager().createFonts();

        setupCamera();
        createBackground();
        createFolders();
        // load preferences
        prefs = Gdx.app.getPreferences("NoteCollectorPreferences");
        prefs.putBoolean("music", true);
        prefs.putBoolean("sound", true);
        if(!prefs.getBoolean("big") && !prefs.getBoolean("vbig"))
        prefs.putBoolean("normal", true);
        else
        prefs.putBoolean("normal", false);
        prefs.flush();

    }



    private void createFolders(){
        try {
            getAssetAppFolder("Note Collector/Sample Tracks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createBackground(){
        img   = noteCollector.getAssetsManager().assetManager.get(Constants.IntroImage);
        background = new Image(img);
        background.setPosition((stage.getCamera().viewportWidth - img.getWidth())/2,(stage.getCamera().viewportHeight - img.getHeight())/2);
        stage.addActor(background);
    }
    @Override
    public void show() {
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(fadeIn(0.4f));
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                noteCollector.setScreen(new MainMenuScreen(noteCollector));
            }
        },2f );
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //stage.act(delta);
        stage.act(Gdx.graphics.getDeltaTime());
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
    public void dispose() {
        noteCollector.getAssetsManager().assetManager.unload(Constants.IntroImage);
    }
    private void setupCamera(){
        viewport = new ScalingViewport(Scaling.fit, Constants.APP_HEIGHT, Constants.APP_HEIGHT, new OrthographicCamera(Constants.APP_WIDTH, Constants.APP_HEIGHT));
        stage = new Stage(viewport);
        stage.getCamera().update();
    }



    //create folder if not exist on device
    private void getAssetAppFolder(String dir) throws Exception{

        {
            File f = new File(Constants.root + "/" + dir);

            if (!f.exists() || !f.isDirectory()) {
                f.mkdirs();

            }


        }

        FileHandle fileHandle = Gdx.files.internal("sample");
        FileHandle [] aplist=fileHandle.list();

        for(FileHandle strf:aplist){
            try{
                InputStream is=   strf.read();
                copyToDisk(dir,strf.name(),is);
            }catch(Exception ex){

                getAssetAppFolder(dir+"/"+strf.name());
            }
        }



    }
    public void copyToDisk(String dir,String name,InputStream is) throws IOException {
        int size;
        byte[] buffer = new byte[2048];

        FileOutputStream fout = new FileOutputStream(Constants.root +"/"+dir+"/" +name);
        BufferedOutputStream bufferOut = new BufferedOutputStream(fout, buffer.length);

        while ((size = is.read(buffer, 0, buffer.length)) != -1) {
            bufferOut.write(buffer, 0, size);
        }
        bufferOut.flush();
        bufferOut.close();
        is.close();
        fout.close();
    }


}
