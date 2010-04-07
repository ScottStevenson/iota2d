package Sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import Util.LoopingByteInputStream;

import Util.ThreadPool;

/**
 * Extends ThreadPool for playback of simultaneous sounds.
 * Original code (c) David Brackeen (BSD-style license)
 * Modified code by Scott Stevenson
 */
public class SoundManager extends ThreadPool {

	private AudioFormat playbackFormat;
    private ThreadLocal localLine;
    private ThreadLocal localBuffer;
    private Object pausedLock;
    private boolean paused;
    
    
    public SoundManager(AudioFormat audioFormat) {
        this(audioFormat,
            getMaxSimultaneousSounds(audioFormat));
    }


    /**
        Creates a new SoundManager with the specified maximum
        number of simultaneous sounds.
    */
    public SoundManager(AudioFormat playbackFormat,
        int maxSimultaneousSounds)
    {
        super(Math.min(maxSimultaneousSounds,
            getMaxSimultaneousSounds(playbackFormat)));
        this.playbackFormat = playbackFormat;
        localLine = new ThreadLocal();
        localBuffer = new ThreadLocal();
        pausedLock = new Object();
        
        synchronized (this) {
            notifyAll();
        }
    }
    
    /**
     * Returns the max number of simultaneous sounds from the mixer.
     * @param audioFormat The format to playback.
     * @return Max number of playable sounds.
     */
    public static int getMaxSimultaneousSounds(
            AudioFormat audioFormat)
    {
        DataLine.Info lineInfo = new DataLine.Info(
            SourceDataLine.class, audioFormat);
        Mixer mixer = AudioSystem.getMixer(null);
        int maxLines = mixer.getMaxLines(lineInfo);
        if (maxLines == AudioSystem.NOT_SPECIFIED) {
            maxLines = 32;
        }
        return maxLines;

    }

    /**
    	Close the mixer and the ThreadPool.
    */
	public void close() {
	    setPaused(false);
	
	    Mixer mixer = AudioSystem.getMixer(null);
	    if (mixer.isOpen()) {
	        mixer.close();
	    }
	    
	    super.close();
	}
	
	public void join() {
	    setPaused(false);
	    
		Mixer mixer = AudioSystem.getMixer(null);
	    if (mixer.isOpen()) {
	        mixer.close();
	    }
	    
        super.join();
    }
	
	/**
	 * Sets the pause state of the SoundManager
	 * @param pause The desired pause state.
	 */
	public void setPaused(Boolean pause) {
		if (this.paused != pause) {
            synchronized (pausedLock) {
                this.paused = pause;
                if (!pause) {
                    // restart sounds
                    pausedLock.notifyAll();
                }
            }
        }
	}
	
	/**
	 * @return Whether the SoundManager is currently paused.
	 */
	public boolean isPaused() {
        return paused;
    }
	
	/**
	 * Returns a Sound from the given sound file.
	 * @param filename The filepath of the sound to retrieve.
	 * @return The Sound retrieved from the file. Returns null if an error occurs.
	 */
	public Sound getSound(String filename) {
	    return getSound(getAudioInputStream(filename));
	}
	
	/**
	 * Returns a Sound from the given InputStream.
	 * @param is InputStream containing the audio.
	 * @return The retrieved Sound.
	 */
	public Sound getSound(InputStream is) {
	    return getSound(getAudioInputStream(is));
	}
	
	
	/**
	 * Returns a sound from the given AudioInputStream.
	 * @param audioStream The AudioInputStream to retrieve the sound from.
	 * @return The Sound from the given AudioInputStream.
	 */
	public Sound getSound(AudioInputStream audioStream) {
	    if (audioStream == null) {
	        return null;
	    }
	
	    //get the number of bytes to read
	    int length = (int)(audioStream.getFrameLength() *
	        audioStream.getFormat().getFrameSize());
	
	    //read in the entire stream
	    byte[] samples = new byte[length];
	    DataInputStream is = new DataInputStream(audioStream);
	    try {
	        is.readFully(samples);
	        is.close();
	    }
	    catch (IOException ex) {
	        ex.printStackTrace();
	    }
	
	    return new Sound(samples);
	}
	
	
	/**
	 * Creates an AudioInputStream from a file. 
	 * @param filename The file to retrieve audio from.
	 * @return The new AudioInputStream.
	 */
	public AudioInputStream getAudioInputStream(String filename) {
	    try {
	        return getAudioInputStream(
	            new FileInputStream(filename));
	    }
	    catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}
	
	
	/**
	 * Creates an AudioInputStream from an InputStream.
	 * @param is The InputStream to read in.
	 * @return The new AudioInputStream.
	 */
	public AudioInputStream getAudioInputStream(InputStream is) {
	
	    try {
	        if (!is.markSupported()) {
	            is = new BufferedInputStream(is);
	        }
	        // open the source stream
	        AudioInputStream source =
	            AudioSystem.getAudioInputStream(is);
	
	        // convert to playback format
	        return AudioSystem.getAudioInputStream(
	            playbackFormat, source);
	    }
	    catch (UnsupportedAudioFileException ex) {
	        ex.printStackTrace();
	    }
	    catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    catch (IllegalArgumentException ex) {
	        ex.printStackTrace();
	    }
	
	    return null;
	}
	
	/**
	 * Plays the given sound on a thread. Returns immediately.
	 * @param sound The Sound to play.
	 * @return The InputStream
	 */
	public InputStream play(Sound sound) {
	    return play(sound, false);
	}
	
	
	/**
	    Plays a sound with an optional SoundFilter, and optionally
	    looping. This method returns immediately.
	*/
	public InputStream play(Sound sound, boolean loop)
	{
	    InputStream is;
	    if (sound != null) {
	        if (loop) {
	            is = new LoopingByteInputStream(
	                sound.getSamples());
	        }
	        else {
	            is = new ByteArrayInputStream(sound.getSamples());
	        }
	
	        return play(is);
	    }
	    return null;
	}
	
	
	/**
	    Plays a sound from an InputStream. This method
	    returns immediately.
	*/
	public InputStream play(InputStream is) {
	    if (is!=null)
	    	runTask(new SoundPlayer(is));
	    return is;
	}
	
	
	/**
	    Signals that a PooledThread has started. Creates the
	    Thread's line and buffer.
	*/
	protected void threadStarted() {
	    // wait for the SoundManager constructor to finish
	    synchronized (this) {
	        try {
	            wait();
	        }
	        catch (InterruptedException ex) { }
	    }
	
	    // use a short, 100ms (1/10th sec) buffer for filters that
	    // change in real-time
	    int bufferSize = playbackFormat.getFrameSize() *
	        Math.round(playbackFormat.getSampleRate() / 10);
	
	    // create, open, and start the line
	    SourceDataLine line;
	    DataLine.Info lineInfo = new DataLine.Info(
	        SourceDataLine.class, playbackFormat);
	    try {
	        line = (SourceDataLine)AudioSystem.getLine(lineInfo);
	        line.open(playbackFormat, bufferSize);
	    }
	    catch (LineUnavailableException ex) {
	        // end this thread
	        Thread.currentThread().interrupt();
	        return;
	    }
	
	    line.start();
	
	    // create the buffer
	    byte[] buffer = new byte[bufferSize];
	
	    // set this thread's locals
	    localLine.set(line);
	    localBuffer.set(buffer);
	}
	
	
	/**
	    Signals that a PooledThread has stopped. Drains and
	    closes the Thread's Line.
	*/
	protected void threadStopped() {
	    SourceDataLine line = (SourceDataLine)localLine.get();
	    if (line != null) {
	        line.drain();
	        line.close();
	    }
	}
	
	/**
	 * 
	 * Runnable that actually plays sounds from an InputStream.
	 */
	protected class SoundPlayer implements Runnable {

        private InputStream source;

        public SoundPlayer(InputStream source) {
            this.source = source;
        }

        public void run() {
            // get line and buffer from ThreadLocals
            SourceDataLine line = (SourceDataLine)localLine.get();
            byte[] buffer = (byte[])localBuffer.get();
            if (line == null || buffer == null) {
                // the line is unavailable
                return;
            }

            // copy data to the line
            try {
                int numBytesRead = 0;
                while (numBytesRead != -1) {
                    // if paused, wait until unpaused
                    synchronized (pausedLock) {
                        if (paused) {
                            try {
                                pausedLock.wait();
                            }
                            catch (InterruptedException ex) {
                                return;
                            }
                        }
                    }
                    // copy data
                    numBytesRead =
                        source.read(buffer, 0, buffer.length);
                    if (numBytesRead != -1) {
                        line.write(buffer, 0, numBytesRead);
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }
}
	
	
	
	
	
	
