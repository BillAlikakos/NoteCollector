package com.mygdx.notecollector.GameStage;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.notecollector.Multiplayer.ClientClass;
import com.mygdx.notecollector.Multiplayer.ServerClass;
import com.mygdx.notecollector.NoteCollector;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.Utils.WorldUtils;
import com.mygdx.notecollector.actors.Collector;
import com.mygdx.notecollector.actors.Piano;
import com.mygdx.notecollector.actors.Score;
import com.mygdx.notecollector.actors.SquareNotes;
import com.mygdx.notecollector.actors.Text;
import com.mygdx.notecollector.midilib.MidiNote;
import com.mygdx.notecollector.screens.menu.MainMenuScreen;

import java.io.IOException;
import java.util.ArrayList;

import static com.badlogic.gdx.graphics.Color.RED;

//import com.mygdx.notecollector.Utils.Pair;

/**
 * Created by bill on 6/18/16.
 */
public class GameStage extends Stage implements ContactListener {

    private World world;
    private Vector3 touchPoint,PuttonPoint;

    private Rectangle collectorPosition;
    private BitmapFont font;

    //Variables for time loop
    private long starttime;
    private float prevTick;
    private ArrayList<MidiNote> notes;
    private Timer timer;
    private Timer.Task task;
    private Music music;
    private float TickPerMsec;
    private float curretnTick;
    private long msec;
    //Texture for background
    private Texture Background;
    //actor objects
    private SquareNotes squareNotes;
    private Collector collector;
    private Score score;
    private Score score2;
    private Piano piano;
    private Text text;

    private int redcounts;
    private Assets AssetsManager;
    private OrthographicCamera camera;
    private boolean fisttime;
    private String message;
    private String MessageScore;
    private String GameState;
    private Preferences prefs;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    private float accumulator = 0;
    private final float TIME_STEP = 1 / 300f;

    private NoteCollector noteCollector;
    private TextureRegionDrawable selectionColor;
    private BitmapFont fontbutton;
    //private  ArrayList<Pair> squarespair;
    private float squarewidth,squareheight;

    private Label resume,quit;
    private Image BackgroundPause;
    public long pausetime;

    private ClientClass c;
    private ServerClass srv;
    private boolean isHost;
    private boolean isGuest;
    private boolean mode;
    private Thread t;
    private Image x1;
    private Image x2;
    private Image x3;
    private Image x4;
    private Image x5;

    public GameStage(NoteCollector noteCollector, float TickPerMsec, ArrayList<MidiNote> notes, int speed, long delay,boolean mode) throws IOException, InterruptedException {
        super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));

        squarewidth=32f;
        squareheight=32f;
        this.AssetsManager =noteCollector.getAssetsManager();
        this.noteCollector=noteCollector;
        this.notes =notes;
        this.TickPerMsec = TickPerMsec;
        this.isGuest=false;
        this.isHost=false;
        this.mode=mode;
        prefs= Gdx.app.getPreferences("NoteCollectorPreferences");

        //set the size of square which controlled by player
        setCollectorSize();
        message ="";
        GameState ="running";
        redcounts =0;
        fisttime =true;
        createObjects();
        createBackground();
        setupWorld();
        setupCamera();
        setupActros(speed,delay);
        StartTimer();
        createPauseLabels();
        createBackgroundPause();
        addActorSPause();

    }

    public GameStage(NoteCollector noteCollector, float TickPerMsec, ArrayList<MidiNote> notes, int speed, long delay, ServerClass srv,boolean mode) throws IOException, InterruptedException {
        super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));

        squarewidth=32f;
        squareheight=32f;
        this.AssetsManager =noteCollector.getAssetsManager();
        this.noteCollector=noteCollector;
        this.notes =notes;
        this.TickPerMsec = TickPerMsec;
        this.srv=srv;
        this.isGuest=false;
        this.isHost=true;
        this.mode=mode;
        prefs= Gdx.app.getPreferences("NoteCollectorPreferences");

        //set the size of square which controlled by player
        setCollectorSize();
        addGameOverListener();
        message ="";
        GameState ="running";
        redcounts =0;
        fisttime =true;
        createObjects();
        createBackground();
        setupWorld();
        setupCamera();
        setupActros(speed,delay);
        StartTimer();
        //createPauseLabels();
        //createBackgroundPause();
       // addActorSPause();

    }
    public GameStage(NoteCollector noteCollector, float TickPerMsec, ArrayList<MidiNote> notes, int speed, long delay, ClientClass c,boolean mode) throws IOException, InterruptedException {
        super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));

        squarewidth=32f;
        squareheight=32f;
        this.AssetsManager =noteCollector.getAssetsManager();
        this.noteCollector=noteCollector;
        this.notes =notes;
        this.TickPerMsec = TickPerMsec;
        this.c=c;
        this.isGuest=true;
        this.isHost=false;
        this.mode=mode;
        prefs= Gdx.app.getPreferences("NoteCollectorPreferences");

        //set the size of square which controlled by player
        setCollectorSize();
        addGameOverListener();
        message ="";
        GameState ="running";
        redcounts =0;
        fisttime =true;
        createObjects();
        createBackground();
        setupWorld();
        setupCamera();
        setupActros(speed,delay);
        StartTimer();
        //createPauseLabels();
        //createBackgroundPause();
       // addActorSPause();

    }
    private void setCollectorSize()
    {
        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)
        {
            if(prefs.getBoolean("normal"))
            {
                squarewidth =32f;
                squareheight=32f;
            }
            else if (prefs.getBoolean("big"))
            {
                squarewidth =40f;
                squareheight=40f;
            }
            else if (prefs.getBoolean("vbig"))
            {
                squarewidth =48f;
                squareheight=48f;

            }
        }
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            if(prefs.getBoolean("normal"))
            {
                squarewidth =40f;
                squareheight=40f;
            }
            else if (prefs.getBoolean("big"))
            {
                squarewidth =48f;
                squareheight=48f;
            }
            else if (prefs.getBoolean("vbig"))
            {
                squarewidth =56f;
                squareheight=56f;

            }
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            if(prefs.getBoolean("normal"))
            {
                squarewidth =48f;
                squareheight=48f;
            }
            else if (prefs.getBoolean("big"))
            {
                squarewidth =56f;
                squareheight=56f;
            }
            else if (prefs.getBoolean("vbig"))
            {
                squarewidth =64f;
                squareheight=64f;
            }
        }
    }

   public void addGameOverListener()//If the one player loses the other player wins automatically
   {
       if(isHost)
       {
           srv.getServer().addListener(new Listener()//Wait for client to load
           {
               public void received (Connection connection, Object object)
               {
                   if (object instanceof ServerClass.gameOver)//When host receives the client's score
                   {
                       System.out.println("Client lost");
                       ServerClass.gameOver request = (ServerClass.gameOver)object;
                       GameState ="finished";
                   }

               }
           });

       }
       else if(isGuest)
       {
           c.getClient().addListener(new Listener()
           {
               public void received (Connection connection, Object object)
               {
                   if (object instanceof ClientClass.gameOver)
                   {
                       System.out.println("Host lost");
                       ClientClass.gameOver request = (ClientClass.gameOver)object;
                       GameState ="finished";
                   }

               }
           });
       }
   }

    public void receiveScore()
    {
        if(isHost)
        {
            srv.getServer().addListener(new Listener()//Wait for client to load
            {

                public void received (Connection connection, Object object)
                {
                    if (object instanceof ServerClass.scoreObj)//When host receives the client's score
                    {
                        System.out.println("Host received score");
                        ServerClass.scoreObj request = (ServerClass.scoreObj)object;
                        System.out.println(request.score);
                        score2.setScore(Integer.parseInt(request.score));//Convert to int
                    }

                }
            });

        }
        else if(isGuest)
        {
            c.getClient().addListener(new Listener()
            {
                public void received (Connection connection, Object object)
                {
                    if (object instanceof ClientClass.scoreObj)//When client receives the Host's score
                    {
                        System.out.println("Client received score");
                        ClientClass.scoreObj request = (ClientClass.scoreObj)object;
                        System.out.println(request.score);
                        score2.setScore(Integer.parseInt(request.score));
                        //scoreV= request.score;
                        // srv.getServer().removeListener(this);
                    }

                }
            });
        }
    }


    private void createObjects(){
        touchPoint = new Vector3();
        PuttonPoint= new Vector3();
        timer = new Timer();
        collectorPosition = new Rectangle(touchPoint.x, touchPoint.y, 36f, 36f);
    }
    private void createPauseLabels()
    {
        fontbutton = AssetsManager.createBimapFont(45);

        resume = createLabel("Resume");
        resume.setPosition((getCamera().viewportWidth-resume.getWidth())/2,(getCamera().viewportHeight)/2 +50f);
        resume.setVisible(false);

        quit = createLabel("Quit");
        quit.setPosition((getCamera().viewportWidth-quit.getWidth())/2,(getCamera().viewportHeight-quit.getHeight())/3 +50f);
        quit.setVisible(false);
    }
    private void addActorSPause(){
        addActor(BackgroundPause);
        addActor(resume);
        addActor(quit);
    }
    private void createBackgroundPause(){
        selectionColor =new TextureRegionDrawable(new TextureRegion(AssetsManager.assetManager.get(Constants.ButtonImage,Texture.class))) ;
        selectionColor.setRightWidth(5f);
        selectionColor.setBottomHeight(2f);
        BackgroundPause = new Image(selectionColor);
        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)
        {
            BackgroundPause.setSize(200,200);
        }
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            BackgroundPause.setSize(250,300);
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            BackgroundPause.setSize(300,350);
        }
        BackgroundPause.setPosition((getCamera().viewportWidth- BackgroundPause.getWidth())/2,(getCamera().viewportHeight  -BackgroundPause.getHeight())/2 );


        BackgroundPause.setVisible(false);
    }
    private void setupActros(int speed,long delay){
        setupCollector();
        setupSquareNotes(speed,delay);
        setupPiano();
        setUpScore();
        setUpText();
        createX();
    }
    private void createBackground(){
        Background = AssetsManager.assetManager.get(Constants.BackgroundGame);
        Image mazePreview = new Image(Background);
        mazePreview.setScaling(Scaling.fit );
        addActor(mazePreview);
    }
    private void setUpText() {

        //set the position of text messages
        Rectangle textBounds = new Rectangle(getCamera().viewportWidth  / 64,
                getCamera().viewportHeight * 62 / 64, getCamera().viewportWidth / 4,
                getCamera().viewportHeight / 6);
        text = new Text(textBounds,AssetsManager);



    }
    private void CreateText(String message,Color color) {
        text.setColor(color);
        text.setMessage(message);
        //set action for fade in and fade out after some seconds
        text.addAction(Actions.sequence(
                Actions.show(),
                Actions.fadeIn(0f),
                Actions.delay(1.5f),
                Actions.fadeOut(1f),
                Actions.hide())
        );

        addActor(text);
    }

    private void createX()
    {
            AssetsManager.LoadX();
            Texture X = AssetsManager.assetManager.get(Constants.X);
            x1=new Image(X);
            x2=new Image(X);
            x3=new Image(X);
            x4=new Image(X);
            x5=new Image(X);
            if(VIEWPORT_HEIGHT==480 && VIEWPORT_WIDTH==800)//Set X positions according to resolution so that they don't overlap with the messages
            {
                x1.setPosition(getCamera().viewportWidth  / 64, getCamera().viewportHeight * 62 / 70-25);
                x2.setPosition(getCamera().viewportWidth  / 64+50, getCamera().viewportHeight * 62 / 70-25);
                x3.setPosition(getCamera().viewportWidth  / 64+100, getCamera().viewportHeight * 62 / 70-25);
                x4.setPosition(getCamera().viewportWidth  / 64+150, getCamera().viewportHeight * 62 / 70-25);
                x5.setPosition(getCamera().viewportWidth  / 64+200, getCamera().viewportHeight * 62 / 70-25);
            }
            else if(VIEWPORT_HEIGHT==720 && VIEWPORT_WIDTH==1080)
            {
                x1.setPosition(getCamera().viewportWidth  / 64, getCamera().viewportHeight * 62 / 70-10);
                x2.setPosition(getCamera().viewportWidth  / 64+50, getCamera().viewportHeight * 62 / 70-10);
                x3.setPosition(getCamera().viewportWidth  / 64+100, getCamera().viewportHeight * 62 / 70-10);
                x4.setPosition(getCamera().viewportWidth  / 64+150, getCamera().viewportHeight * 62 / 70-10);
                x5.setPosition(getCamera().viewportWidth  / 64+200, getCamera().viewportHeight * 62 / 70-10);
            }
            else if(VIEWPORT_HEIGHT>720 && VIEWPORT_WIDTH>1080)
            {
                x1.setPosition(getCamera().viewportWidth  / 64, getCamera().viewportHeight * 62 / 70);
                x2.setPosition(getCamera().viewportWidth  / 64+50, getCamera().viewportHeight * 62 / 70);
                x3.setPosition(getCamera().viewportWidth  / 64+100, getCamera().viewportHeight * 62 / 70);
                x4.setPosition(getCamera().viewportWidth  / 64+150, getCamera().viewportHeight * 62 / 70);
                x5.setPosition(getCamera().viewportWidth  / 64+200, getCamera().viewportHeight * 62 / 70);
            }
            addActor(x1);
            addActor(x2);
            addActor(x3);
            addActor(x4);
            addActor(x5);
    }

    private void setupWorld(){
        world = WorldUtils.CreateWorld();
        world.setContactListener(this);
    }
    private void setupSquareNotes(int speed,long delay){
        squareNotes = new SquareNotes(WorldUtils.CreateSquareNotes(world), AssetsManager,speed,delay,mode);
        addActor(squareNotes);
    }
    private void setupCollector(){
        collector = new Collector(WorldUtils.CreateCollector(world), AssetsManager);
        addActor(collector);
    }
    private void setupPiano()
    {
        piano = new Piano(WorldUtils.CreatePianoKeyboard(world), AssetsManager);
        piano.setNotes(notes);
        piano.setSquareNotes(squareNotes);
        addActor(piano);

    }

    private void setUpScore() {
        Rectangle scoreBounds = new Rectangle(getCamera().viewportWidth * 57 / 64,
                getCamera().viewportHeight * 62 / 64, getCamera().viewportWidth / 4,
                getCamera().viewportHeight / 6);
        score = new Score(scoreBounds, AssetsManager);
        if(this.isHost || this.isGuest)
        {
            Rectangle scoreBounds2 = new Rectangle(getCamera().viewportWidth * 57 / 64,
                    getCamera().viewportHeight * 52 / 64, getCamera().viewportWidth / 4,
                    getCamera().viewportHeight / 6);
            if(isHost)
            {
                score2 = new Score(scoreBounds2, AssetsManager,srv);
            }
            else
            {
                score2 = new Score(scoreBounds2, AssetsManager,c);
            }
            addActor(score2);
            receiveScore();
        }

        addActor(score);

    }

     private void setupCamera() {
         camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
         camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
         camera.update();
     }

    private void StartTimer(){
        createTimer();
        SetupMusic();//With this method create the object of music class for play the music track
/*Check if the player have select to play without sound*/
        if (prefs.getBoolean("music")) music.setVolume(100f);
        else music.setVolume(0f);

        music.play();
        timer.scheduleTask(task, 0f,0.1f);//Define the task we created with method createTimer and scheduled for run every 0.1 seconds
        starttime = System.currentTimeMillis();// find the current time in millisecond
        curretnTick = 0;//initialize the variable curretnTick

    }
    private void createTimer(){

        task = new Timer.Task() {
            @Override
            public void run() {
                if(GameState.equals("running")) {
                    /*find the millisecond with computing the subtract the starttime of task from current time in milliseconds
*/
                    msec = System.currentTimeMillis()- starttime;
                    prevTick =  curretnTick;
                    curretnTick =  (msec / TickPerMsec);
                    /* call the method from object of Piano class DrawNotes for drawing the current played note and
produce the corresponding square*/
                    piano.DrawNotes((int) curretnTick,(int) prevTick);

                }


            }
        };
    }
    private void SetupMusic(){
        music =AssetsManager.assetManagerFiles.get(AssetsManager.MusicName,Music.class);
        music.setOnCompletionListener(new Music.OnCompletionListener() {
           //if the music track is ended set the state of game in finished for stopping the game
            @Override
            public void onCompletion(Music music) {
                GameState ="finished";
                //t.interrupt();
            }
        });
    }


    public void setGameState(String gameState) {
        GameState = gameState;
    }

    private Label createLabel(String text){
        Label.LabelStyle labelstyle = new Label.LabelStyle(fontbutton, Color.WHITE);
        Label fileLabel = new Label(text, labelstyle);
        return  fileLabel;

    }

    private void pauseTimer(long paused){
        if (music == null)
            return;
        //pause the music 
        if (music.isPlaying()) {
            this.pausetime = paused;
            long   msec = System.currentTimeMillis() - starttime;
            prevTick = curretnTick;
            curretnTick = (msec / TickPerMsec);
            piano.DrawNotes((int) curretnTick, (int) prevTick);
        }
        music.pause();
        timer.stop();
    }

// pause all the objects of game
    private void pauseObjects(boolean paused){
        squareNotes.setPaused(paused);
        collector.setPaused(paused);
        piano.setPaused(true);

    }


    private void fadeInPause(boolean visible){
        BackgroundPause.setVisible(visible);
        resume.setVisible(visible);
        quit.setVisible(visible);
    }
     public void pauseGame(long paused){
         pauseTimer(paused);
         pauseObjects(true);
         fadeInPause(true);

     }
    @Override
     public void act(float delta) {
         super.act(delta);

         squareNotes.setCollector(collectorPosition);
         redcounts = squareNotes.getRedcounts();
         if(redcounts==1)//Change color of X's depending on the number of redcounts
         {
             x1.setColor(RED);
         }
         else if(redcounts==2)
         {
             x2.setColor(RED);
         }
         else if(redcounts==3)
         {
             x3.setColor(RED);
         }
         else if(redcounts==4)
         {
             x4.setColor(RED);
         }
         else if(redcounts==5)
         {
             x5.setColor(RED);
         }
         score.setScore(squareNotes.getScore());
         if(getGameState().equals("running"))
         {

             if(isHost)//Send score to host/guest
             {
                 Integer s=score.getScore();
                 srv.sendScore(s.toString());
             }
             else if(isGuest)
             {
                 Integer s=score.getScore();
                 c.sendScore(s.toString());
             }
         }


         //refresh the score    
         MessageScore = String.valueOf(squareNotes.getScore());


         checkMessages();

         accumulator += delta;
         while (accumulator >= delta){
             world.step(TIME_STEP, 6, 2);
             accumulator -= TIME_STEP;
         }


     }

     //check what type of message is; to printintg it
    private void checkMessages(){
        if (squareNotes.getSquareColor() == 3 && fisttime == true && squareNotes.getTypeOfFailure() == 1) {
            createMessage(RED,"Collector disabled for ");
        }else  if (squareNotes.getSquareColor() == 3 && fisttime == true && squareNotes.getTypeOfFailure() == 2) {
            createMessage(RED,"Speed up for ");
        }
        else if(squareNotes.getSquareColor() ==2 && fisttime == true ) {
            createMessage(Color.GREEN,"Bonus X "+squareNotes.getMultiplier()+" for ");
        }else if(squareNotes.getSquareColor() == 1 && fisttime == false){
            message="";
            fisttime=true;
        }
    }
    private void createMessage(Color color,String message){
        long time = squareNotes.getTime();
        message += String.valueOf(time/1000)+" seconds";
        CreateText(message,color);
        fisttime =false;

    }



    public String getScore() {
        return MessageScore;
    }

    public String getGameState() {
        return GameState;
    }

    public int getRedcounts() {
         return redcounts;
     }

     //resume the music to play 
    public void resumeTimer(){
        GameState ="running";
        pausetime =  System.currentTimeMillis() - pausetime;
        starttime = pausetime + starttime;
        music.play();
        timer.start();
    }

   //reusme the game
    public void resumeGame(){
        pauseObjects(false);
        fadeInPause(false);
        resumeTimer();
    }

     @Override
     public void draw() {

        super.draw();
         this.getBatch().setProjectionMatrix(camera.combined);
     }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
    if(isGuest || isHost) //disable pause for multiplayer
    {

    }
    else
    {
        translateScreenToWorldCoordinates(x,y);
        getCamera().unproject(PuttonPoint.set(x,y,0));
        // check the touch of selections on pause screen
        if ( GameState=="paused" && checkPositionButtons(PuttonPoint.y) ) {
            GameState ="resume";
            return false;
        }else if( GameState=="paused" && checkPositionButtons(PuttonPoint.y) == false){
            dispose();
            noteCollector.setScreen(new MainMenuScreen(noteCollector));
            return  false;
        }
        if(VIEWPORT_WIDTH==800 && VIEWPORT_HEIGHT==480)//Change dimensions of pause button for different resolutions
        {
            if (PuttonPoint.y <56f && PuttonPoint.x<800f && GameState !="paused")
            {
                GameState = "paused";
            }
        }
        if(VIEWPORT_WIDTH==1080 && VIEWPORT_HEIGHT==720)
        {
            if (PuttonPoint.y <56f && PuttonPoint.x<800f+120 && GameState !="paused")//Calculate position for pause button according to resolution (considering the padding operation that occurs when drawing the keyboard
            {
                GameState = "paused";
            }
        }
        if(VIEWPORT_WIDTH>1080 && VIEWPORT_HEIGHT>720)
        {
            if (PuttonPoint.y <56f && PuttonPoint.x<800f+550 && GameState !="paused")
            {
                GameState = "paused";
            }
        }



        }
        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        translateScreenToWorldCoordinates(screenX,screenY);
        return true;
    }

   @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
       translateScreenToWorldCoordinates(screenX,screenY);


       //set bound on collector and change position when drag the square 
       if (touchPoint.x+ 32f  >getCamera().viewportWidth || touchPoint.y +32f >getCamera().viewportHeight || touchPoint.y+32f < 56f ) {
           return false;
       }
       else {
          collectorPosition.set(touchPoint.x,touchPoint.y,squarewidth,squareheight);
           collector.changeposition(touchPoint.x, touchPoint.y);

       }
       return true;
    }

    private boolean checkPositionButtons(float y){

        if(  y >getCamera().viewportHeight/2 )
            return  true;

        return false;

    }

    private void translateScreenToWorldCoordinates(int x, int y){
        getCamera().unproject(touchPoint.set(x, y, 0));
    }
    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void dispose() {
        timer.clear();
        task.cancel();
        music.stop();
        x1.clear();
        x2.clear();
        x3.clear();
        x4.clear();
        x5.clear();
        //t.interrupt();
        AssetsManager.assetManagerFiles.dispose();
    }


}
