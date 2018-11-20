package com.mygdx.notecollector;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.IntroScreen;

public class NoteCollector extends Game {


	private Assets AssetsManager;
	private Sound click;
	private IWiFi wifiCtx;
	private IGallery andoridGallery;
	private IAuthUser auth;
	private IDataBase db;
	private IGoogleLogin login;
	private Stage stage;
	public NoteCollector(){}
	public NoteCollector(IWiFi wiFi, IGallery gallery,IAuthUser authUser,IDataBase dbRef,IGoogleLogin logIn)
	{
	    wifiCtx=wiFi;
		andoridGallery=gallery;
		auth=authUser;
		db=dbRef;
		login=logIn;
	}

    public Stage getStage()
    {
        return stage;
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public IGoogleLogin getGoogleLogin() { return login; }
	public boolean getWifiCtx()
	{
		return  wifiCtx.isNetworkConnected();
	}

	public IGallery getGallery()
	{
		return andoridGallery;
	}

	public IAuthUser getAuth()
	{
		return auth;
	}

	public IGallery getAndoridGallery()
	{
		return andoridGallery;
	}

	public IDataBase getDb()
	{
		return db;
	}

	// method for getting asset manager
	public Assets getAssetsManager() {
		return AssetsManager;
	}

	public void create()
    {
		//initialize asset manager and audio for click
		Constants.viewportDimensions();
		AssetsManager = new Assets();

        Preferences prefs = Gdx.app.getPreferences("NoteCollectorPreferences");

		click = Gdx.audio.newSound(Gdx.files.internal("data/ui/Sounds/click.mp3"));
		setScreen(new IntroScreen(this));
	}
// method for getting click
	public Sound getClick() {
		return click;
	}
}
