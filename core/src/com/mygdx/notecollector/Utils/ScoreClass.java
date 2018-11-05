package com.mygdx.notecollector.Utils;

import java.util.Comparator;

/*
Helper class for score submission
 */
public class ScoreClass
{
    private boolean onSubmit;
    private String songName;
    private String score;
    private String difficulty;
    private String userName;
    private String mode;

    public ScoreClass(){}
    public ScoreClass(boolean onSubmit,String songName, String score, String difficulty,String userName,String mode)
    {
        this.onSubmit=onSubmit;
        this.difficulty=difficulty;
        this.score=score;
        this.songName=songName;
        this.userName=userName;
        this.songName=extractName();
        this.mode=mode;
    }
    public ScoreClass(String songName,String score,String difficulty,String userName,String mode)
    {
        this.difficulty=difficulty;
        this.score=score;
        this.songName=songName;
        this.userName=userName;
        this.songName=extractName();
        this.mode=mode;
    }

    public boolean OnSubmit()
    {
        return onSubmit;
    }

    public void setOnSubmit(boolean onSubmit)
    {
        this.onSubmit = onSubmit;
    }

    public String getSongName()
    {
        return songName;
    }

    public void setSongName(String songName)
    {
        this.songName = songName;
    }

    public String getScore()
    {
        return score;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    public String getDifficulty()
    {
        return difficulty;
    }

    public void setDifficulty(String difficulty)
    {
        this.difficulty = difficulty;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    private String extractName()//Creates a substring that contains only the file name
    {

        String tempName=  this.songName;
        if(tempName.contains("/"))
        {
            String fullPath = tempName;
            int index = fullPath.lastIndexOf("/");
            String fileName = fullPath.substring(index + 1);
            String finalName= fileName.substring(0, fileName.lastIndexOf('.'));
            System.out.println("FILE NAME IS: "+finalName);
            System.out.println("FULL FILE NAME: "+tempName);
            return finalName;
        }
        return songName;

    }
}
