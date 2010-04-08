package Graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 
 * This class holds a series of frames of images that can be updated and retrieved
 *
 */
public class Sprite 
{
	private ArrayList<Frame> frames;
	private int currFrameIndex;
    private long animTime;
    private long totalDuration;
	
	public Sprite()
	{
		frames = new ArrayList<Frame>();
		totalDuration = 0;
	}
	
	/**\
	 * adds a frame to the sprite
	 * @param image the image to be used in the sprite.
	 * @param duration the length to play the frame
	 */
	public synchronized void addFrame( BufferedImage image, long duration )
    {
        totalDuration += duration;
        frames.add( new Frame( image, totalDuration ));
    }
	
	public void update( float elapsedTime )
	{
		if( frames.size() > 1 )
		{
			animTime += elapsedTime;
			
			if( animTime >= totalDuration )
			{
				animTime = animTime % totalDuration;
				currFrameIndex = 0;
			}
			
			while( animTime > getFrame( currFrameIndex ).playLength )
			{
				currFrameIndex++;
			}
		}
	}
	
	public int getWidth()
	{
		return getImage().getWidth( null );
	}
	
	public int getHeight()
	{
		return getImage().getHeight( null );
	}
	
	public BufferedImage getImage()
	{
		if( frames.size() == 0 )
		{
			return null;
		}
		else
		{
			return getFrame( currFrameIndex ).image;
		}
	}
	
	private Frame getFrame( int i )
	{
		return frames.get( i );
	}
	
	/** 
	 * 
	 * a container for holding an image and playLenth.
	 *
	 */
	private class Frame
	{
		BufferedImage image;
		long playLength;
		
		public Frame( BufferedImage image, long playLength )
		{
			this.image = image;
			this.playLength = playLength;
		}
	}
}
