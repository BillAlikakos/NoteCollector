package com.mygdx.notecollector;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.notecollector.Utils.ScoreClass;

public interface IDataBase
{
    void read(IAuthUser user,NoteCollector noteCollector,Stage stage,boolean competitive);
    void write(IAuthUser user,ScoreClass score);
}
