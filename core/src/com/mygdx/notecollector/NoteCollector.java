package com.mygdx.notecollector;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.screens.IntroScreen;

public class NoteCollector extends Game {


	private Assets AssetsManager;
	private Sound click;
	IWiFi wifiCtx;
	IGallery andoridGallery;
	public NoteCollector(){}
	public NoteCollector(IWiFi wiFi, IGallery gallery)
	{
	    wifiCtx=wiFi;
		andoridGallery=gallery;
	}


	public boolean getWifiCtx()
	{
		return  wifiCtx.isNetworkConnected();
	}

	public IGallery getGallery()
	{
		return andoridGallery;
	}
// method for getting asset manager
	public Assets getAssetsManager() {
		return AssetsManager;
	}

	/*public Context getContext()
	{
		return mContext;
	}

    public boolean isNetworkConnected()
    {

        WifiManager wifi = (WifiManager) this.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled())
        {

            return true;
            //wifi is enabled
        }
        else
        {
           return false;
        }

    }*/

	public void create()
    {
		//initialize asset manager and audio for click
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
