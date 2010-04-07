package Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
The LoopingByteInputStream is a ByteArrayInputStream that
loops indefinitly. The looping stops when the close() method
is called.
*/
public class LoopingByteInputStream extends ByteArrayInputStream {

	private boolean closed;
	
	/**
	    Creates a new LoopingByteInputStream with the specified
	    byte array. 
	    
	    @param buffer The byte array to loop
	*/
	public LoopingByteInputStream(byte[] buffer) {
	    super(buffer);
	    closed = false;
	}
	
	
	/**
	    Reads length bytes from the array. Starts reading from the beginning
	    if the end is reached.
	    
	    @param buffer The buffer to read into.
	    @param offset Offset from the current position in the array to
	    start reading from.
	    @param length The number of bytes to read.
	    @return Returns -1 is the stream is closed, returns the number
	    of bytes read if the read was successful.
	    
	*/
	public int read(byte[] buffer, int offset, int length) {
	    if (closed) {
	        return -1;
	    }
	    int totalBytesRead = 0;
	
	    while (totalBytesRead < length) {
	        int numBytesRead = super.read(buffer,
	            offset + totalBytesRead,
	            length - totalBytesRead);
	
	        if (numBytesRead > 0) {
	            totalBytesRead += numBytesRead;
	        }
	        else {
	            reset();
	        }
	    }
	    return totalBytesRead;
	}
	
	
	/**
	    Closes the stream.
	*/
	public void close() throws IOException {
	    super.close();
	    closed = true;
	}

}
