package com.mygdx.notecollector.midilib;

/**
 * Created by bill on 6/19/16.
 */
public class MidiNote {
    private long starttime;
    private int channel;
    private int notenumber;
    private long duration;

    public MidiNote(int notenumber, long starttime, int channel, long duration) {
        this.notenumber = notenumber;
        this.starttime = starttime;
        this.channel = channel;
        this.duration = duration;
    }

    public long getStarttime() {
        return starttime;
    }


    public int getNotenumber() {
        return notenumber;
    }


    public int getChannel() {
        return channel;
    }


    public long getDuration() {
        return duration;
    }

    public long getEndTime(){
        return starttime + duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void CalculateDuration(long endtime){

        duration = endtime - starttime;


    }


}
