package com.mygdx.notecollector;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.notecollector.Utils.ScoreClass;

public interface IAuthUser
{
    boolean isConnected();
    void createAccount(String userName,String email, String password,NoteCollector noteCollector,Stage stage,ScoreClass score);
    void signIn(String email, String password, NoteCollector noteCollector, Stage stage, ScoreClass score);
    boolean getAuthState();
    String getUserName();
    String getUID();
    void logOut();

}
