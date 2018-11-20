package com.mygdx.notecollector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mygdx.notecollector.Utils.ScoreClass;

public class LoginHandler implements IGoogleLogin
{

    //private static final LoginHandler INSTANCE = new LoginHandler();
    private static final String TAG = "LoginHandler";
    public static int RC_SIGN_IN = 9001; // Request code to use when launching the resolution activity
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount account;
    private Context context;
    private NoteCollector noteCollector;
    private Stage stage;
    private ScoreClass score;

    public LoginHandler(Context ctx)
    {
        this.context=ctx;
        this.startApiClient();
    }

    /*public static LoginHandler getInstance() {
        return INSTANCE;
    }*/

    public ScoreClass getScore()
    {
        return score;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    public void startApiClient() {
        if (mGoogleApiClient == null) {
            System.out.println("Starting api client");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("455152900263-1ct2o8vtbdgp0gn2upv8p14lelj8qj2n.apps.googleusercontent.com")
                    //.requestEmail()
                    .build();
            System.out.println("Gso Set");
            PlayServiceListener playServiceListener = new PlayServiceListener();
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(playServiceListener)
                    .addOnConnectionFailedListener(playServiceListener)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            System.out.println("All set ?");
        }
    }
    @Override
    public void login(NoteCollector noteCollector,Stage stage,ScoreClass score)
    {
        System.out.println("LogIn");
        this.noteCollector=noteCollector;
        this.stage=stage;
        this.score=score;
        if (!isConnected())
        {
            if(mGoogleApiClient.isConnected())
            {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            ((AndroidLauncher) context).startActivityForResult(signInIntent, RC_SIGN_IN);
            /*if (context instanceof AndroidLauncher) {
                System.out.println("Starting acivity...");
                ((AndroidLauncher) context).startActivityForResult(signInIntent, RC_SIGN_IN);
            }*/
        }
        else
        {
            if(mGoogleApiClient.isConnected())
            {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            ((AndroidLauncher) context).startActivityForResult(signInIntent, RC_SIGN_IN);
            //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            System.out.println("???");
        }
    }
    @Override
    public void logout() {
        if (isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                System.out.println("Lolololol");
                                mGoogleApiClient.disconnect();
                                //Toast.makeText(context, R.string.logout_succeed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public GoogleApiClient getmGoogleApiClient()
    {
        return mGoogleApiClient;
    }
    public void setAccount(GoogleSignInAccount account)
    {
        this.account=account;
    }

    @Override
    public void clearDefault()
    {
        mGoogleApiClient.clearDefaultAccountAndReconnect();
    }

    @Override
    public boolean isConnected()
    {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    public void connect() {
        if (!isConnected())
        {
            mGoogleApiClient.connect();
        }
    }

    private class PlayServiceListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "onConnected");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
            // Attempt to reconnect
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed" + connectionResult);
        }
    }
}