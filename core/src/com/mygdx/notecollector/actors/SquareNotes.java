package com.mygdx.notecollector.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;

import java.util.ArrayList;
import java.util.Random;

import static com.badlogic.gdx.graphics.Color.RED;

/**
 * Created by bill on 6/21/16.
 */
public class SquareNotes extends GameActor {

    private Sprite sprite;
    private Texture img;

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    //valu for counting how many red squares is collected
    private int redcounts;
    //value for speed of flying squares
    private int speed, startspeed;
    private int typeOfFailure;
    //AA list for keeping the flying objects inside the screen
    private ArrayList<Pair> noteflying;
    //The last time where created a flying square
    private long lastFlyTime;
    //delay for create a flying note
    private long delay;
    //position of collector
    private Rectangle collector;
    //multiplier for bonus
    private int multiplier;
    private int score;
    //Boolean value for disabled collector
    private boolean disable;
    //Starttime of failure or bonus
    private long startTime;
    //time of failure or bonus
    private long time;
    //value for color of square 1is red 2 is green 3 is grey
    private int SquareColor;
    //star time for bonus
    private long startBonus;
    //position of one square
    private Rectangle note;
    //A list for clear unnecessary flying squares
    private ArrayList<Pair> itemreove;
    //A pair of a position of square and color
    private Pair pair;
    private Color color;
    //andom values
    private Random randMultiplier;
    private int valueMultiplier;

    private Random randFailure;
    private int valueFailure;

    private Random randSpeed;
    private int Low;
    private int High;
    private int valueSpeed;

    private Random randTime;
    private int LowTime;
    private int HighTime;
    private int valueTime;
    private boolean paused;
    private boolean mode;


    public SquareNotes(Body body, Assets AssetsManager, int speed, long delay,boolean mode) {
        super(body, AssetsManager);
        this.delay = delay;
        this.mode=mode;
        startspeed = speed;

        img = AssetsManager.assetManager.get(Constants.square);
        sprite = new Sprite(img);
        sprite.setScale(1f*VIEWPORT_WIDTH/800,1f*VIEWPORT_HEIGHT/480);
        itemreove = new ArrayList<Pair>(150);
        noteflying = new ArrayList<Pair>(150);

        collector = new Rectangle();


        initialize();
    }


    private void initialize() {
        InitializeRandomValues();
        InitializeOtherObjects();
        InitializeSprite();
        createRandomObjects();
    }

    private void createRandomObjects() {
        randMultiplier = new Random();
        randFailure = new Random();
        randTime = new Random();
        randSpeed = new Random();
    }

    private void InitializeRandomValues() {
        multiplier = 1;
        startTime = 0;
        startBonus = 0;
        time = 0;
        speed = startspeed;
        disable = false;
        typeOfFailure = 0;
    }

    private void InitializeSprite() {
        sprite.setY(Constants.SquareNoteStartY);
        //  sprite.setSize(Constants.SquareNoteWidth,Constants.SquareNoteHeight);
    }

    private void InitializeOtherObjects() {
        paused = false;
        score = 0;
        redcounts = 0;
        SquareColor = 1;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getSquareColor() {
        return SquareColor;
    }

    public void setCollector(Rectangle collector) {
        this.collector = collector;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        int i = 0;
        int noteflyingsize = noteflying.size();
        //while the list of squares isn't empty draw the square and float it up with corresponding speed
        while (i < noteflyingsize) {

            DrawSprite(batch, i);
            if (!paused)
                note.y += (speed * Gdx.graphics.getDeltaTime());

            if (note.getY() + 12f > Gdx.graphics.getHeight())
            {
                if(mode && score>0 && !color.equals(RED))//If the note is red do not subtract score
                {
                    score-=2;
                }

                itemreove.add(pair);
            }

            // if gray square which used from user collect a suare check for collor
            if (note.overlaps(collector) && !disable)
                checkColors();

            i++;
        }
        //with this method remove the objects which esape from screen and objects collected by user
        clearItems();
    }


    private void DrawSprite(Batch batch, int i) {
        int padX = 120;
        int padY = 0;
        pair = noteflying.get(i);
        note = noteflying.get(i).note;
        color = noteflying.get(i).color;
        sprite.setColor(color);
        /*if (VIEWPORT_WIDTH == 1080 && VIEWPORT_HEIGHT == 720) {


            //test=note.getX()+100f;
            // note.setX(test);
            //  System.out.println(note.getX()+" "+note.getX()+100f+" test:"+test);
            padX = 100;
            padY = 22;
        }
        if (VIEWPORT_WIDTH > 1080 && VIEWPORT_HEIGHT > 720) {
            padX = 150;
        }*/
        sprite.setPosition(note.getX(), note.getY());
        sprite.draw(batch);

    }

    private void checkColors() {

        if (color.equals(RED)) {
            itemreove.add(pair);
            if (typeOfFailure != 2)
                getRandomTypeOfFailure();

            redcounts++;


        } else if (color.equals(Color.GREEN)) {
            SquareColor = 2;
            getRandomMulitiplier();

            itemreove.add(pair);


        } else if (color.equals(Color.GRAY)) {
            SquareColor = 1;
            score += 2 * multiplier;
            itemreove.add(pair);


        }
    }

    private void clearItems() {
        int size = itemreove.size();
        for (int k = 0; k < size; k++)
            noteflying.remove(itemreove.get(k));

        itemreove.clear();

    }

    public int getRedcounts() {
        return redcounts;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        checkTypeOfFailure();


    }

    public int getTypeOfFailure() {
        return typeOfFailure;
    }

    // check the type of failure when collect red squares
    private void checkTypeOfFailure() {

        switch (typeOfFailure) {

            case 0:
                break;
            case 1:
                checkTypeFailureDisable();
                break;
            case 2:
                checkTypeFailureSpeed();
                break;

        }
    }

    //define the type of failure when disabled user from collecting and check the time 
    private void checkTypeFailureDisable() {
        if (startTime == 0) {
            SquareColor = 3;
            startTime = System.currentTimeMillis();
            disable = true;
            getRandomTime();
        } else if (isElapsed(startTime)) {
            disable = false;
            startTime = 0;
            typeOfFailure = 0;
            return;
        }
    }
    //define the type of failure when speed up the squares and check the time 

    private void checkTypeFailureSpeed() {
        if (startTime == 0) {
            SquareColor = 3;
            getRandomSpeed();
            getRandomTime();
            startTime = System.currentTimeMillis();
        } else if (isElapsed(startTime)) {
            speed = startspeed;
            startTime = 0;
            typeOfFailure = 0;
            return;
        }
    }
    //check if the time from bonus or failure is elapsed

    private boolean isElapsed(long startTime) {
        if ((System.currentTimeMillis() - startTime) > time)
            return true;

        return false;
    }

    public long getTime() {
        return time;
    }

    private void getRandomMulitiplier() {
        valueMultiplier = randMultiplier.nextInt(5) + 2;

        if (startBonus == 0 && SquareColor == 2) {
            startBonus = System.currentTimeMillis();
            getRandomTime();
            multiplier = valueMultiplier;
        } else if ((System.currentTimeMillis() - startBonus) > time) {
            startBonus = 0;
            multiplier = 1;
        }

    }

    private void getRandomTypeOfFailure() {
        valueFailure = randFailure.nextInt(6);
        if (valueFailure % 2 == 0)
            typeOfFailure = 1;
        else if (valueFailure % 2 != 0)
            typeOfFailure = 2;
    }

    private void getRandomSpeed() {
        Low = speed + 10;
        High = 210*VIEWPORT_HEIGHT/480;
       /* if(VIEWPORT_HEIGHT==480 && VIEWPORT_WIDTH==800)//Different high speed for each resolution (as each resolution has different speed values to counter screen size)
        {
            High = 210;
        }
        else if(VIEWPORT_HEIGHT==720 && VIEWPORT_WIDTH==1080)
        {
            High = 300;
        }
        else
        {
            High=390;
        }*/

        valueSpeed = randSpeed.nextInt(High - Low) + Low;
        speed = valueSpeed;
    }

    private void getRandomTime() {
        LowTime = 2000;
        HighTime = 6000;
        valueTime = randTime.nextInt(HighTime - LowTime) + LowTime;
        time = valueTime;
    }

    public void StartNote(float Xaxis)
    {
        if (TimeUtils.millis() - lastFlyTime > delay) {

            noteflying.add(new Pair(Xaxis));
            lastFlyTime = TimeUtils.millis();
        }


    }

    private Color getRandomColor() {
        Random rand = new Random();
        int value = rand.nextInt(30);
        if (value == 0 || value == 3)
            return RED;
        else if (value == 10)
            return Color.GREEN;
        else
            return Color.GRAY;

    }

}

class Pair {
    Color color;
    Rectangle note;
    float Yaxis = Constants.SquareNoteStartY;
    float Xaxis;
    int padX;
    int padY;
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    public Pair(float Xaxis) {
        this.Xaxis = Xaxis;

        note = new Rectangle();
        note.x = Xaxis;
        note.y = Yaxis;
        color = getRandomColor();
    }

    private Color getRandomColor() {
        Random rand = new Random();
        int value = rand.nextInt(30);
        if (value == 0 || value == 3)
            return RED;
        else if (value == 10)
            return Color.GREEN;
        else
            return Color.GRAY;

    }

}

