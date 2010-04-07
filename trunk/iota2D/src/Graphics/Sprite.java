package Graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
	
	public synchronized void addFrame( BufferedImage image, long duration )
    {
        totalDuration += duration;
        frames.add( new Frame( image, totalDuration ));
    }
	
	public synchronized void start()
	{
		animTime = 0;
		currFrameIndex = 0;
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
