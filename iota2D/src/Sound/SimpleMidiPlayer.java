package Sound;

import java.io.FileInputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/* 
 *  Simply plays a midi file. Does not Loop.
 */
public class SimpleMidiPlayer {
	 
	  public static void play(String midiFile) {
	      try {
	          Sequencer sequencer = MidiSystem.getSequencer();
	          if (sequencer == null)
	              throw new MidiUnavailableException();
	          sequencer.open();
	          FileInputStream is = new FileInputStream(midiFile);
	          Sequence mySeq = MidiSystem.getSequence(is);
	          sequencer.setSequence(mySeq);
	          sequencer.start();
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	  }
}
