package com.mygdx.notecollector;

import android.app.Activity;
import android.content.Intent;

import com.badlogic.gdx.Gdx;
import com.mygdx.notecollector.IGallery;

public class Gallery implements IGallery//Implementation of gallery interface
{
    private Activity activity;
    public static final int SELECT_IMAGE_CODE=1;
    private String currentImagePath;
    private boolean activityCancelled=false;

    public Gallery(Activity activity)
    {
        this.activity=activity;
    }
    @Override
    public void getImagePath()//Method that creates an Intent to open image specific application
    {
        System.out.println("Interface implementation code exec");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"),SELECT_IMAGE_CODE);
    }

    public void setImageResult(String path)//Sets the user selected image
    {
        currentImagePath=path;
    }

    @Override
    public String getSelectedFilePath()//Returns the path to the core module
    {
        return currentImagePath;
    }

    @Override
    public void clearSelectedPath()
    {
        currentImagePath=null;
    }

    @Override
    public boolean getState()
    {
        return activityCancelled;
    }
    @Override
    public void setState(boolean val)
    {
        this.activityCancelled=val;
    }


}
