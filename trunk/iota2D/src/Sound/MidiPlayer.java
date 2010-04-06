package Sound;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.*;

/**
 * A class for playing back MIDI sequences.
 */
public class MidiPlayer implements MetaEventListener {

    // End of track message. Need to receive this for looping.
    public static final int END_OF_TRACK = 47;

    private Sequencer sequencer;
    private boolean loop;
    //private boolean paused;
    
    /**
     * Constructor.
     * Opens the MidiSystem sequencer.
     * Adds self as a MetaEventListener to the sequencer.
     */
    public MidiPlayer() {
	    try {
	        sequencer = MidiSystem.getSequencer();
	        sequencer.open();
	        sequencer.addMetaEventListener(this);
	    }
	    catch ( MidiUnavailableException ex) {
	        sequencer = null;
	    }
	}
    
    
    /**
     * Returns the Sequence from the given midi file.
     * @params filename The MIDI file to get the sequence from.
     */
    public Sequence getSequence(String filename) {
    	try {
            return getSequence(new FileInputStream(filename));
        }
        catch (IOException ex) {
            ex.printStackTrace();
    		return null;
        }
    }
    
    /**
     * Returns the Sequence from the given InputStream.
     * @params is The InputStream to retrieve the sequence from.
     */
    public Sequence getSequence(InputStream is) {
        try {
            if (!is.markSupported()) {
                is = new BufferedInputStream(is);
            }
            Sequence s = MidiSystem.getSequence(is);
            is.close();
            return s;
        }
        catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Plays and optionally loops the given sequence.
     * @params sequence The sequence to playback.
     * @params loop Whether the sequence should loop.
     */
    public void play(Sequence sequence, boolean loop) {
        if (sequencer != null && sequence != null && sequencer.isOpen()) {
            try {
                sequencer.setSequence(sequence);
                sequencer.start();
                this.loop = loop;
            }
            catch (InvalidMidiDataException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Plays and optionally loops the sequence in the given MIDI file.
     * @params filename The MIDI file to play.
     * @params loop Whether the sequence should loop.
     */
    public void play(String filename, boolean loop) {
    	Sequence sequence;
    	try {
            sequence = getSequence(new FileInputStream(filename));
            this.play(sequence, loop);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }     
    }
    
    /**
     * Called by the sound system when a meta event occurs.
     * Restarts a track if it is supposed to loop and an END_OF_TRACK
     * message is received.
     */
    public void meta(MetaMessage event) {
	    if (event.getType() == END_OF_TRACK) {
	        if (sequencer != null && sequencer.isOpen() && loop) {
	            sequencer.setMicrosecondPosition(0);
	        	sequencer.start();
	        }
	    }
    }
    
    /**
     * Stops the sequencer and sets the tick position to 0 us.
     */
    public void stop() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.stop();
            sequencer.setMicrosecondPosition(0);
        }
   }
    
    /**
     * Closes the sequencer.
     */
    public void close() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.close();
        }
   }
}
   
