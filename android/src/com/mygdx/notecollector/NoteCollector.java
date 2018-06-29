package com.mygdx.notecollector;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.screens.IntroScreen;

public class NoteCollector extends Game {


	private Assets AssetsManager;
	private Sound click;
	private Context mContext;


	public NoteCollector()
	{

	}
	public NoteCollector(Context c)
	{
		this.mContext=c;
	}

// method for getting asset manager
	public Assets getAssetsManager() {
		return AssetsManager;
	}

	public Context getContext()
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

    }

	public void create()
    {
		//initialize asset manager and audio for click
		AssetsManager = new Assets();

		click = Gdx.audio.newSound(Gdx.files.internal("data/ui/Sounds/click.mp3"));
		setScreen(new IntroScreen(this));
	}
// method for getting click
	public Sound getClick() {
		return click;
	}
}
