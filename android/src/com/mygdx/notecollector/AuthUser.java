package com.mygdx.notecollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.notecollector.Utils.ScoreClass;
import com.mygdx.notecollector.screens.menu.UserArea.ResultScreen;
import com.mygdx.notecollector.screens.menu.UserArea.ScoresScreen;

import static android.content.ContentValues.TAG;

public class AuthUser implements IAuthUser
{
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context applicationContext;
    private Activity activity;
    private boolean authState = false;
    private boolean userExists = false;
    private boolean signUpSuccess = false;
    private boolean signUpFailure = false;

    public AuthUser(Context ctx,Activity activity)
    {
        applicationContext = ctx;
        this.activity=activity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean isConnected()
    {
        return mAuth.getCurrentUser() != null;
    }

    public void createAccount(final String userName, final String email, final String password, final NoteCollector noteCollector, final Stage stage, final ScoreClass score)
    {
        System.out.println(userName);
        System.out.println(email);
        System.out.println(password);
        final AuthUser usr = this;//Reference to use as 'this' cannot be used inside anonymous classes
        final DataBase db = new DataBase(applicationContext);
        db.getDatabase().getReference().child("users")//Check if the username already exists in users database before registering the user
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            final String regUser = snapshot.getValue(String.class);
                            if (!userExists && userName.equals(regUser))
                            {
                                System.out.println(userName);
                                Toast.makeText(applicationContext, "Username Is Taken", Toast.LENGTH_SHORT).show();
                                userExists = true;
                            }
                        }
                        if (!userExists)
                        {
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) applicationContext, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        authState = true;
                                        //Sign in success . update ui with signed in users information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        user = mAuth.getCurrentUser();
                                        if (user != null)
                                        {
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(userName)
                                                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                                    .build();
                                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        db.registerUser(user);
                                                        Timer.Task schedule = Timer.schedule(new Timer.Task()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                noteCollector.getScreen().dispose();
                                                                if (score.OnSubmit())
                                                                {
                                                                    score.setUserName(userName);
                                                                    db.write(usr, score);
                                                                    noteCollector.setScreen(new ScoresScreen(noteCollector, stage));
                                                                        /*if (score.getMode().equals("competitive"))
                                                                        {
                                                                            db.read(usr, noteCollector, stage, true);
                                                                        }
                                                                        else
                                                                        {
                                                                            db.read(usr, noteCollector, stage, false);
                                                                        }*/
                                                                    // noteCollector.setScreen(new ScoresScreen(noteCollector,stage,score));
                                                                } else
                                                                {
                                                                    noteCollector.setScreen(new ResultScreen(noteCollector, stage));
                                                                }

                                                                //noteCollector.setScreen(new ResultScreen(noteCollector, stage));
                                                            }
                                                        }, 0.4f);
                                                    }
                                                }
                                            });
                                        }
                                    } else
                                    {
                                        //If sign in fails, display a message to the user
                                        authState = false;
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
    }

    @Override
    public void signInEmail(String email, String password, final NoteCollector noteCollector, final Stage stage, final ScoreClass score)
    {
        final AuthUser usr = this;//Reference to use as 'this' connot be used inside anonymous classes
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) this.applicationContext, new OnCompleteListener<AuthResult>()
        {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    //Sign in success, update UI with the signed in user's info
                    Log.d(TAG, "signInWithEmail:success");
                    user = mAuth.getCurrentUser();
                    authState = true;
                    final DataBase db = new DataBase(applicationContext);
                    //When the user is signed in change to the appropriate screen
                    Timer.Task schedule = Timer.schedule(new Timer.Task()
                    {
                        @Override
                        public void run()
                        {
                            noteCollector.getScreen().dispose();
                            if (score.OnSubmit())
                            {
                                score.setUserName(user.getDisplayName());
                                if (score.OnSubmit())
                                {
                                    score.setUserName(user.getDisplayName());
                                    db.write(usr, score);
                                    /*if (score.getMode().equals("competitive"))
                                    {
                                        db.read(usr, noteCollector, stage, true);
                                    } else
                                    {
                                        db.read(usr, noteCollector, stage, false);
                                    }*/
                                    noteCollector.setScreen(new ScoresScreen(noteCollector, stage));
                                }
                                //noteCollector.setScreen(new ScoresScreen(noteCollector,stage,score));
                            } else
                            {
                                noteCollector.setScreen(new ResultScreen(noteCollector, stage));
                            }

                        }
                    }, 0.4f);
                } else
                {
                    //If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    authState = false;
                }
            }
        });
    }
    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct, final NoteCollector noteCollector, final Stage stage,final ScoreClass score)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final AuthUser usr = this;//Reference to use as 'this' connot be used inside anonymous classes
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity)this.applicationContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final DataBase db = new DataBase(applicationContext);
                            Timer.Task schedule = Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    noteCollector.getScreen().dispose();
                                    if (score.OnSubmit())
                                    {
                                        score.setUserName(user.getDisplayName());
                                        if (score.OnSubmit())
                                        {
                                            score.setUserName(user.getDisplayName());
                                            db.write(usr, score);
                                            noteCollector.setScreen(new ScoresScreen(noteCollector, stage));
                                        }
                                        //noteCollector.setScreen(new ScoresScreen(noteCollector,stage,score));
                                    }
                                    else
                                    {
                                        noteCollector.setScreen(new ResultScreen(noteCollector, stage));
                                    }
                                }
                            }, 0.4f);
                            //updateUI(user);
                        } else
                            {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });

    }

    @Override
    public String getUID()
    {
        return mAuth.getCurrentUser().getUid();
    }

    @Override
    public String getUserName()
    {
        return mAuth.getCurrentUser().getDisplayName();
    }
    @Override
    public void logOut()
    {
        mAuth.signOut();
    }

}
