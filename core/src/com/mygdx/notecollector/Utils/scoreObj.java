package com.mygdx.notecollector.Utils;

public class scoreObj
{
    private int score;
    private String name;
    private String trackName;
    private String difficulty;

    public Integer getScore()
    {
        return ((Integer) score);
    }

    public String getName()
    {
        return name;
    }

    public String getDifficulty()
    {
        return difficulty;
    }

    public scoreObj(int score, String name, String trackName, String difficulty)
    {
        this.score = score;
        this.name = name;
        this.trackName = trackName;
        this.difficulty=difficulty;
    }

    public String getTrackName()
    {
        return trackName;
    }

    public void setTrackName(String trackName)
    {
        this.trackName = trackName;
    }

    public String extractName()//Creates a substring that contains only the file name
    {

        String tempName=  this.trackName;
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
        return trackName;

    }

}

