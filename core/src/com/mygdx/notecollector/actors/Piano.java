package com.mygdx.notecollector.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.notecollector.Utils.Assets;
import com.mygdx.notecollector.Utils.Constants;
import com.mygdx.notecollector.midilib.MidiNote;

import java.util.ArrayList;

/**
 * Created by bill on 5/31/16.
 */
public class Piano extends  GameActor {

    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;
    private Sprite spritewhite, spriteblack;
    private SquareNotes squareNotes;
    private Texture textureWhite, textureBlack, textureWhitepressed, textureBlackpressed,whiteBig,blackBig,whitePBig,blackPBig;
    private boolean pressed;
    private int octave, notescale,octave2, notescale2;
    private float[] MultipleNotes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private ArrayList<MidiNote> notes;
    private Batch batch;
    private int i = 0, j = 0;
    private Sprite spriteblackPressed,spritewhitePressed,blackPressed,whitePressed,white,black;

    private int lastIndex,notesize;
    private int startTime,endTime,nextNote;
    private int numberSameNotes;
    private int start,end;
    private int left,right;
    private int pad;
    private int sub;
    private boolean paused;

    public Piano(Body body, Assets AssetsManager) {
        super(body,AssetsManager);
        //initiallize textures for black and white keys
        textureWhite = AssetsManager.assetManager.get(Constants.WhiteKey);
        textureBlack = AssetsManager.assetManager.get(Constants.BlackKey);
        textureWhitepressed = AssetsManager.assetManager.get(Constants.WhitePressedKey);
        textureBlackpressed = AssetsManager.assetManager.get(Constants.BlackPressedKey);
        //initiallize boolean value for pressed key
        pressed = false;
        spriteblack = new Sprite(textureBlack);
        pad=0;

        spriteblackPressed = new Sprite(textureBlackpressed);

        spritewhitePressed = new Sprite(textureWhitepressed);

        spritewhite = new Sprite(textureWhite);
        sub=0;
        System.out.println(spriteblack.getScaleX());
        System.out.println(spriteblack.getScaleY());
        spriteblack.setScale(1f*VIEWPORT_WIDTH/800,1f*VIEWPORT_HEIGHT/480);
        spriteblackPressed.setScale(1f*VIEWPORT_WIDTH/800,1f*VIEWPORT_HEIGHT/480);
        spritewhitePressed.setScale(1f*VIEWPORT_WIDTH/800,1f*VIEWPORT_HEIGHT/480);
        spritewhite.setScale(1f*VIEWPORT_WIDTH/800,1f*VIEWPORT_HEIGHT/480);
        // boolean value for pause detection
        paused =false;

    }



    @Override
    public void draw(Batch batch, float parentAlpha) {


       // pad=0;
        i=21;
        j=0;
        this.batch = batch;
        //draw while i smaller than number 109 find octave of note number and notescale of note number.
        while (i< 109) {
            octave = (i/ 12) -1;
            notescale = i% 12;


           //With notescale and octave of note check if is not black key  and draw it.     
            if(!checkBlackPosition(i))
                createwhitekeys(pad);

            i++;
        }
        i=21;

        while (i< 109) {
            octave = (i/ 12) -1;
            notescale = i% 12;
           //With notescale and octave of note check if is not white key  and draw it.     

            if(checkBlackPosition(i))
                createblackkeys(pad-sub);
            i++;
        }

    }

    public void setSquareNotes(SquareNotes squareNotes) {
        this.squareNotes = squareNotes;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private void createblackkeys(int pad)
    {

        //check if the key pressed draw pressed key else draw normal key. Also check if key have pressede before  
        if (noteposition[octave][notescale] == MultipleNotes[j] && pressed == true)
        {
            float y=20f*VIEWPORT_HEIGHT/480;
            spriteblackPressed.setPosition(MultipleNotes[j]*VIEWPORT_WIDTH/800, y);

            spriteblackPressed.draw(batch);
                squareNotes.StartNote(noteposition[octave][notescale]*VIEWPORT_WIDTH/800);

            j++;
        } else
        {
            float y=20f*VIEWPORT_HEIGHT/480;
            spriteblack.setPosition(noteposition[octave][notescale]*VIEWPORT_WIDTH/800, y);
            spriteblack.draw(batch);


        }

    }
    private void createwhitekeys(int pad)
    {
        float y=5f*VIEWPORT_HEIGHT/480;
        //the same function of method createblackkeys in this method 
        if (noteposition[octave][notescale] == MultipleNotes[j] && pressed == true)
        {
            spritewhitePressed.setPosition( MultipleNotes[j]*VIEWPORT_WIDTH/800, y);
            spritewhitePressed.draw(batch);
            squareNotes.StartNote(noteposition[octave][notescale]*VIEWPORT_WIDTH/800);

            j++;
        }
        else
        {
            spritewhite.setPosition(noteposition[octave][notescale]*VIEWPORT_WIDTH/800, y);

            spritewhite.draw(batch);
        }

    }

    //method of checking if note number is black key
    private boolean checkBlackPosition(int i){

        if(notescale == 1 || notescale == 3 || notescale == 6 || notescale == 8 || notescale == 10  )
            return true;
        return false;

    }
    //method for getting notenumber of music track
    public void setNotes(ArrayList<MidiNote> notes) {
        this.notes = notes;
    }

    @Override
    public void act(float delta) {

        super.act(delta);

    }

    public void DrawNotes(int currentTcik, int prevTick){
        notesize = notes.size();
        lastIndex = findClosestStartTime(prevTick);
        //search in list of notenumbers the specific note number in the current time
        for (int i =lastIndex;i<notesize;i++){
             startTime = (int) notes.get(i).getStarttime();
             endTime = (int) notes.get(i).getEndTime();
             nextNote = NextStartTime(i);
            sameTicks(startTime,lastIndex);

              //if  the time is bigger than tick  break
            if (startTime > prevTick && startTime > currentTcik) {
                break;
            }

            //if the start time is same or between next and end time is smaller than current tick break
            if ((startTime <=currentTcik) && (startTime <nextNote)
                    && (currentTcik <endTime)
                    && (startTime <= prevTick) &&(prevTick < nextNote)
                    && (prevTick < endTime) ) {
                break;
            }
            //if the start time is between current tick and end tick of note number, push the key
            if((startTime <=currentTcik) && (startTime < endTime)){
                pressed =true;

            }else if((startTime <= prevTick) && (prevTick <endTime)){
                pressed = false;

            }

        }


    }

    //check if note is pressed
    private void sameTicks(int tick,int index){
        numberSameNotes=0;

        for (int i=index;i<notesize;i++){
            if(notes.get(i).getStarttime()==tick){
                octave2 = (notes.get(i).getNotenumber() / 12) -1;
                notescale2 = notes.get(i).getNotenumber() % 12;
                MultipleNotes[numberSameNotes] = noteposition[octave2][notescale2];
                numberSameNotes++;
            }

        }

    }


//method for finding next  note
    private int NextStartTime(int i) {
        start = (int) notes.get(i).getStarttime();
         end = (int) notes.get(i).getEndTime();

        while (i < notesize) {
            if (notes.get(i).getStarttime() > start) {
                return (int) notes.get(i).getStarttime();
            }
            end = (int) Math.max(end, notes.get(i).getEndTime());
            i++;
        }
        return end;
    }
    //method for finding the last note which is pressed
    private int findClosestStartTime(long tick){
         left =0;
         right = notesize -1;

        while(right -left> 1) {
            int i = (right + left) / 2;
            if (notes.get(i).getStarttime() == tick)
                break;
            else if (notes.get(i).getStarttime() <= tick)
                left = i;
            else
                right = i;
        }
        while(left >=1 && notes.get(left-1).getStarttime() == notes.get(left).getStarttime()) {
            left--;
        }
        return left;
    }


}
