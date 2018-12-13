package com.mygdx.notecollector;

import android.content.Context;
import android.widget.Toast;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.notecollector.Utils.ScoreClass;
import com.mygdx.notecollector.Utils.scoreObj;
import com.mygdx.notecollector.screens.menu.UserArea.ScoresScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DataBase implements IDataBase
{
    private Context  applicationContext;
    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference myRef=database.getReference("highScores");
    private String score;
    private String songName;
    private String difficulty;
    private String userName;
    private String gameMode;

    public DataBase(Context ctx)
    {
        this.applicationContext=ctx;
    }

    @Override
    public void read(final IAuthUser user, final NoteCollector noteCollector, final Stage stage, final boolean competitive)
    {
        final DatabaseReference ref;
        //DatabaseReference ref = database.getReference("highScores/"+user.getUID());
        if(competitive)
        {
            ref= database.getReference("highScores").child("competitive").child(user.getUID()).child("scores/");
        }
        else
        {
            ref = database.getReference("highScores").child("practice").child(user.getUID()).child("scores/");
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final ArrayList <ScoreClass> scores=new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    ScoreClass value = snapshot.getValue(ScoreClass.class);
                    scores.add(value);
                }

                Collections.sort(scores, new Comparator<ScoreClass>()//Sort scores
                {
                    @Override
                    public int compare(ScoreClass score2, ScoreClass score1)
                    {

                        return  score1.getScore().compareTo(score2.getScore());
                    }
                });
                Timer.schedule(new Timer.Task() {

                    @Override
                    public void run() {
                        noteCollector.getScreen().dispose();
                        noteCollector.setScreen(new ScoresScreen(noteCollector,stage,scores,competitive));

                    }

                }, 0.4f);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public ScoreClass returnResults(ScoreClass obj)
    {
        return obj;
    }


    @Override
    public void write(final IAuthUser user, final ScoreClass score)
    {
        final DatabaseReference myRef;
        if(score.getMode().equals("practice"))
        {
             myRef=database.getReference("highScores/practice/"+user.getUID()+"/");
        }
        else
        {
             myRef=database.getReference("highScores/competitive/"+user.getUID()+"/");
        }
        myRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long num=dataSnapshot.getChildrenCount();
                String index=num.toString();
                HashMap<String, ScoreClass> entry = new HashMap<>();
                myRef.child("scores").push().setValue(new ScoreClass(score.getSongName(),score.getScore(),score.getDifficulty(),user.getUserName(),score.getMode()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       // myRef.setValueAsync(users);
    }

    public void registerUser(FirebaseUser user)
    {
        //DatabaseReference myRef=database.getReference("users");
        DatabaseReference myRef=database.getReference();
        //Map<String,String> users = new HashMap<>();
        //users.put(user.getUid(), user.getDisplayName());
        //myRef.setValue(users);
        myRef.child("users").push().setValue(user.getDisplayName());

    }


    public FirebaseDatabase getDatabase()
    {
        return database;
    }

    public DatabaseReference getMyRef()
    {
        return myRef;
    }
}
