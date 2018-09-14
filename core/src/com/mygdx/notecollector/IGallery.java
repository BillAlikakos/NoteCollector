package com.mygdx.notecollector;

public interface IGallery //Interface for platform-specific code
{
    public void getImagePath();
    public String getSelectedFilePath();
    public void clearSelectedPath();
}
