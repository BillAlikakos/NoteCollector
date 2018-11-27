package com.mygdx.notecollector;

public interface IGallery //Interface for platform-specific code
{
    void getImagePath();
    String getSelectedFilePath();
    void clearSelectedPath();
    boolean getState();
    void setState(boolean val);
}
