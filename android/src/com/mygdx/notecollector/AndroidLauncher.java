package com.mygdx.notecollector;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class AndroidLauncher extends AndroidApplication
{
	private static String TAG = "AndroidLauncher";
	Gallery gallery;
	String userImagePath = null;
	//@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		Activity thisActivity = (Activity) this.getContext();
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;//Launch in immersive mode
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Keep screen continuously on
		WiFi wifi = new WiFi(this.getContext());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if (this.getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			{
				//request the permission
				ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
			}
			else
			{
				// Permission has already been granted
			}
			gallery = new Gallery(this);
			initialize(new NoteCollector(wifi,gallery), config);
		}
	}

	@Override
	public void onRequestPermissionsResult ( int requestCode, String permissions[], int[] grantResults)
	{
		switch (requestCode)
		{
			case 0:
			{
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					System.out.println("Permission granted");
					Intent intent = getIntent();//Restart application to prevent android graphics pause/hang
					finish();
					startActivity(intent);
					// permission was granted
				}
				else
				{
					// permission denied
				}
				return;
			}

		}
	}

	private String getPath(Uri uri)//Method to get the user specified image's uri
	{
		if(uri.getScheme().equalsIgnoreCase("file"))
		{
			return uri.getPath();
		}
		Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA } , null, null, null);

		if (cursor == null) {
			return null;
		}

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		String filePath = cursor.getString(column_index);

		cursor.close();

		return filePath;
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode, Intent data)//Method that returns the uri
	{
		if(resultCode== RESULT_OK && requestCode ==Gallery.SELECT_IMAGE_CODE)
		{
			System.out.println("OnActivityResult");
			Uri imageUri=data.getData();
			this.userImagePath=getPath(imageUri);
			Gdx.app.log("Gallery","Image path is " + userImagePath);
			gallery.setImageResult(userImagePath);
		}
	}
}
