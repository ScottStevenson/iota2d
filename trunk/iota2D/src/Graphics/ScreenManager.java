package Graphics;

import java.awt.DisplayMode;

import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class ScreenManager 
{
	private GraphicsDevice device;
	private AffineTransform transform;
	public float scaleX;
	public float scaleY;
	
	// set up the main window
	public ScreenManager( float width, float height )
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		DisplayMode dm = gs[0].getDisplayMode();
	    float screenWidth = dm.getWidth();
	    float screenHeight = dm.getHeight();
	    
		transform = new AffineTransform();
		 GraphicsEnvironment environment =
	            GraphicsEnvironment.getLocalGraphicsEnvironment();

		device = environment.getDefaultScreenDevice();
		scaleX = screenWidth / width;
		scaleY = screenHeight / height;
		transform.scale( scaleX, scaleY );
	}
	
	public void setFullScreen( DisplayMode displayMode )
	{
		JFrame frame = new JFrame();
		frame.setUndecorated( true );
		frame.setIgnoreRepaint( true );
		frame.setResizable( false );
		
		device.setFullScreenWindow( frame );
		if( displayMode != null && device.isDisplayChangeSupported() )
		{
			try
			{
				device.setDisplayMode( displayMode );
			}
			catch( IllegalArgumentException ex )
			{
			}
		}
		frame.createBufferStrategy( 2 );
	}
	
	public Graphics2D getGraphics()
	{
		Window window = device.getFullScreenWindow();
		if( window != null )
		{
			BufferStrategy strategy = window.getBufferStrategy();
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setTransform( transform );
			return g;
		}
		else
		{
			return null;
		}
	}
	
	public void update()
	{
		Window window = device.getFullScreenWindow();
		if( window != null )
		{
			BufferStrategy strategy = window.getBufferStrategy();
			if( !strategy.contentsLost() )
			{
				strategy.show();
			}
		}
		
	}
	
	public int getHeight()
	{
		return device.getFullScreenWindow().getHeight();
	}
	
	public int getwidth()
	{
		return device.getFullScreenWindow().getWidth();
	}
	
	public Window getFullScreneWindow()
	{
		return device.getFullScreenWindow();
	}
	
	public void closeScrene()
	{
		Window window = device.getFullScreenWindow();
		if( window != null )
		{
			window.dispose();
		}
		device.setFullScreenWindow( null );
	}
}
