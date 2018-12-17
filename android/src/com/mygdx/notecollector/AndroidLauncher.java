package com.mygdx.notecollector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Timer;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mygdx.notecollector.screens.menu.UserArea.SocialSplashScreen;

public class AndroidLauncher extends AndroidApplication
{
    private static final int RC_SIGN_IN = 100;
    private static String TAG = "AndroidLauncher";
	private WiFi wifi;
	private AndroidApplicationConfiguration config;
	private AuthUser user;
	private DataBase db;
	private Gallery gallery;
	private LoginHandler googleHandler;
	private NoteCollector noteCollector;
	String userImagePath = null;
	//@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		Activity thisActivity = (Activity) this.getContext();
		super.onCreate(savedInstanceState);
		config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;//Launch in immersive mode
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Keep screen continuously on
		wifi = new WiFi(this.getContext());
		user=new AuthUser(this.getContext(),this);
		db=new DataBase(this.getContext());
		gallery = new Gallery(this);
		googleHandler=new LoginHandler(this.getContext());
        noteCollector=new NoteCollector(wifi,gallery,user,db,googleHandler);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)//Check if device is running android version >6
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
			initialize(noteCollector, config);
		}
		else
		{
			initialize(noteCollector, config);
		}
	}

	@Override
	public void onRequestPermissionsResult (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
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
					finish();
					// permission denied
				}
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
		if (cursor == null)
		{
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
		if(resultCode==RESULT_CANCELED && requestCode == Gallery.SELECT_IMAGE_CODE)
		{
			gallery.setState(true);
			gallery.setImageResult("cancelled");
		}
		if(resultCode== RESULT_OK && requestCode == Gallery.SELECT_IMAGE_CODE)
		{
			Uri imageUri=data.getData();
			this.userImagePath=getPath(imageUri);
			Gdx.app.log("Gallery","Image path is " + userImagePath);
			gallery.setImageResult(userImagePath);
		}
		if (requestCode==LoginHandler.RC_SIGN_IN)
		{
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try
			{
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = task.getResult(ApiException.class);
				System.out.println(account.getDisplayName());
                googleHandler.connect();
				user.firebaseAuthWithGoogle(account,noteCollector,noteCollector.getStage(),googleHandler.getScore());
			}
			catch (ApiException e)
			{
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
				Toast.makeText(this.getContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
				Timer.Task schedule = Timer.schedule(new Timer.Task()
				{
					@Override
					public void run()
					{
						noteCollector.getScreen().dispose();
						noteCollector.setScreen(new SocialSplashScreen(noteCollector, noteCollector.getStage()));
					}
				}, 0.2f);
			}
		}
	}

	@Override
	public void onBackPressed()//Override back button handler to prevent exiting
	{
		//super.onBackPressed();
	}


	@Override
	public void onDestroy()
	{
		user.logOut();//Disconnect user when app closes
		if(googleHandler.isConnected())//If google account is used then it must be disconnected separately
		{
			googleHandler.logout();
		}
		super.onDestroy();
	}
}