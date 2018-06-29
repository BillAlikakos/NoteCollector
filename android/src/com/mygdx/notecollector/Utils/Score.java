package com.mygdx.notecollector.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by bill on 7/25/16.
 */
public class Score  {

// class for writing score and get recorded scores
    private Preferences score;
    private ArrayList<String> numbers;

    public Score() {

        score = Gdx.app.getPreferences("Scores");
        numbers = new ArrayList<>();

    }


    public void WriteScore(String name, int ScoreNumber, String trackName,String Difficulty)
    {

        scoreObj sc=new scoreObj(ScoreNumber,name,trackName,Difficulty);
        sc.setTrackName(sc.extractName());

        boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();//Check if Local Storage is available
        if(isLocAvailable)
        {
            FileHandle file = Gdx.files.local("scores.csv");
            file.writeString(  ScoreNumber+","+name+","+sc.getTrackName()+","+Difficulty+"\n", true);



        }
        else
        {
            System.err.println("Local storage unavailable.");
        }

    }


    public ArrayList<scoreObj> getScore()//Fix sorting and overhaul the display system
    {
        FileHandle file = Gdx.files.local("scores.csv");
        ArrayList<scoreObj> scores = new ArrayList<scoreObj>();
        if(!file.exists())
        {
            return scores;
        }
        //scoreObj temp=new scoreObj();
        String text = file.readString();
        //System.out.println(text);
        String tmp=text;
        String lines[] = tmp.split("\\r?\\n");//Regex to split string on newlines
        System.out.println("Length:"+lines.length);

        for(int i=0;i<lines.length;i++)
        {
            String[] fields=lines[i].split(",");
            //for(int j=0;j<fields.length;j++)
            {
                scoreObj score=new scoreObj(Integer.parseInt(fields[0]),fields[1],fields[2],fields[3]);
                scores.add(score);
                //System.out.print(" "+fields[j]);
            }
            System.out.println(" ");
        }
        Collections.sort(scores, new Comparator<scoreObj>()
        {
            @Override
            public int compare(scoreObj score2, scoreObj score1)
            {

                return  score1.getScore().compareTo(score2.getScore());
            }
        });
        for(int i=0; i<scores.size();i++)
        {
            scoreObj score=scores.get(i);
            System.out.println(score.getName()+" "+score.getScore()+" "+score.getTrackName()+" "+score.getDifficulty());
        }
        return scores;
    }



}
