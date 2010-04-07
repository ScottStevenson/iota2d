package Sound;

/**
	A container class for sound samples.
*/
public class Sound {

	private byte[] samples;
	
	
	public Sound(byte[] samples) {
	    this.samples = samples;
	}
	
	
	public byte[] getSamples() {
	    return samples;
	}

}
