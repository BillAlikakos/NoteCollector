package com.mygdx.notecollector;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.notecollector.Utils.ScoreClass;

public interface IGoogleLogin
{
    void login(NoteCollector noteCollector, Stage stage, ScoreClass score);
    void logout();
    void clearDefault();
    boolean isConnected();
}
