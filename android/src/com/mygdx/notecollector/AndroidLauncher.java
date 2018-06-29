package com.mygdx.notecollector;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class AndroidLauncher extends AndroidApplication
{
	private static String TAG = "AndroidLauncher";

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config =new AndroidApplicationConfiguration();
		config.useImmersiveMode=true;//Launch in immersive mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Keep screen continuously on
//        WifiP2pManager mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);//
//		WifiP2pManager.Channel mChannel = mManager.initialize(this, getMainLooper(), null);


		initialize(new NoteCollector(this.getContext()),config);
	}

	

}
