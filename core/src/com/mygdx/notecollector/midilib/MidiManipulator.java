package com.mygdx.notecollector.midilib;

import com.mygdx.notecollector.midilib.event.MidiEvent;
import com.mygdx.notecollector.midilib.event.NoteOff;
import com.mygdx.notecollector.midilib.event.NoteOn;
import com.mygdx.notecollector.midilib.event.meta.Tempo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by bill on 6/19/16.
 */
public class MidiManipulator {
    private int resolution;
    private float bpm;
    private ArrayList<MidiNote> notes;
    private ArrayList<MidiEvent> midiEvents;
    private MidiFile mf;

 /*Here computation of ticks per Msec*/
    public float getTickPerMsec() {
        return  (60000 / (bpm * resolution));
    }




    public MidiManipulator(File input) throws IOException {
        mf = null;

        try {
            mf = new MidiFile(input);
        } catch (IOException e) {
            System.err.println("Error parsing MIDI file:");
            e.printStackTrace();
            return;
        }

        midiEvents = new ArrayList<MidiEvent>();
        //get all tacks of midi file in list
        ArrayList<MidiTrack> filetracks = mf.getTracks();
        Iterator<MidiTrack> it = filetracks.iterator();
         /*If midi file has tracks continue*/
        while (it.hasNext()) {
            Iterator<MidiEvent> midiEventIterator = it.next().getEvents().iterator();
             /*If midi file track has event continue */
            while (midiEventIterator.hasNext()) {
                MidiEvent E = midiEventIterator.next();

                /*check if midi event is noteon or note off*/
                if (E.getClass().equals(NoteOn.class) || E.getClass().equals(NoteOff.class)) {
                    midiEvents.add(E);
                }
                /*check for tempo of midi track*/
                if (E.getClass().equals(Tempo.class)) {
                    Tempo tempo = (Tempo) E;
                    bpm = tempo.getBpm();
                }


            }

        }

        resolution = mf.getResolution();

        System.out.println("length "+mf.getLengthInTicks());
        System.out.println("ticks= "+getTickPerMsec());
    }

    public ArrayList<MidiEvent> getMidiEvents()
    {
        return midiEvents;
    }

    /*this function combine all notes information in one list of MidiNote eventes and sorted from beginning
time to end*/

    public ArrayList<MidiNote> GetAllNotes() {
        notes = new ArrayList<MidiNote>();


        for (MidiEvent event : midiEvents) {

            if (event.getClass().equals(NoteOn.class)) {
                NoteOn noteon = (NoteOn) event;
                /*check if note start added it to list*/
                if (noteon.getVelocity() > 0) {
                    MidiNote note = new MidiNote(noteon.getNoteValue(), noteon.getTick(), noteon.getChannel(), 0);

                    notes.add(note);

                } 
                /*check if note end use function NoteOff*/
                else if (noteon.getVelocity() == 0) {

                    NoteOff(noteon.getChannel(), noteon.getNoteValue(), noteon.getTick());
                }
            } else if (event.getClass().equals(NoteOff.class)) {
                NoteOff noteoff = (NoteOff) event;

                NoteOff(noteoff.getChannel(), noteoff.getNoteValue(), noteoff.getTick());
            }

        }

        sortnotes();
        /*for (int i = 0; i < notes.size(); i++) {
            MidiNote note = notes.get(i);
            System.out.println("Note start time = " + note.getStarttime() + " Notenumber= " + note.getNotenumber() + "note end time = " + note.getEndTime());
        }*/
            return notes;


    }

    public ArrayList<MidiNote> GetNotes(MidiTrack track)//Method to get only the notes of the selected track
    {
        notes = new ArrayList<MidiNote>();
        TreeSet<MidiEvent> events=track.getEvents();
        Iterator eventI=events.iterator();
        for(int i=0;i<track.getEventCount();i++)
        {
            MidiEvent event=(MidiEvent) eventI.next();
            //System.out.println(event.toString());
            if (event.getClass().equals(NoteOn.class)  ) {
                NoteOn noteon = (NoteOn) event;
                /*check if note start added it to list*/
                if (noteon.getVelocity() > 0) {
                    MidiNote note = new MidiNote(noteon.getNoteValue(), noteon.getTick(), noteon.getChannel(), 0);

                    notes.add(note);

                }
                /*check if note end use function NoteOff*/
                else if (noteon.getVelocity() == 0) {

                    NoteOff(noteon.getChannel(), noteon.getNoteValue(), noteon.getTick());
                }
            } else if (event.getClass().equals(NoteOff.class)) {
                NoteOff noteoff = (NoteOff) event;

                NoteOff(noteoff.getChannel(), noteoff.getNoteValue(), noteoff.getTick());
            }

        }

        sortnotes();
        /*for (int i = 0; i < notes.size(); i++) {
            MidiNote note = notes.get(i);
            System.out.println("Note start time = " + note.getStarttime() + " Notenumber= " + note.getNotenumber() + "note end time = " + note.getEndTime());
        }*/
        return notes;


    }

    public void NoteOff(int channel, int notenumber, long endtime) {
        //If the object with this channel, this notenumber and this endtime don’t have duration, for this note calculate the duration. 
        for (int i = 0; i < notes.size(); i++) {
            MidiNote note = notes.get(i);

            if (note.getChannel() == channel && note.getNotenumber() == notenumber &&
                    note.getDuration() == 0) {
                notes.get(i).CalculateDuration(endtime);
                return;
            }

        }
    }
     /*sort list of MidNote objectes. Sort based on starttime of every note */
    private void sortnotes(){


        Collections.sort(notes, new Comparator<MidiNote>() {
            @Override
            public int compare(MidiNote notefirst, MidiNote notenext) {
                if ( notefirst.getStarttime() ==  notenext.getStarttime())
                    return  (notefirst.getNotenumber() - notenext.getNotenumber());
                else
                    return (int) ( notefirst.getStarttime()- notenext.getStarttime());
            }
        });



    }

}



