package Game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import Util.Vector2;

import Graphics.ScreenManager;

public class MouseMotion implements MouseMotionListener
{
	private ScreenManager screen; 
	private GameState state;
	private float scaleX;
	private float scaleY;
	
	public MouseMotion( ScreenManager screen, GameState state )
	{
		this.screen = screen;
		this.scaleX = screen.scaleX;
		this.scaleY = screen.scaleY;
		this.state = state;
		screen.getFullScreneWindow().addMouseMotionListener( this );
	}
	
	public void mouseDragged( MouseEvent e ) 
	{
		state.mouseDraged( new Vector2( e.getX() / scaleX, e.getY() / scaleY ));
	}

	public void mouseMoved( MouseEvent e ) 
	{ 
		state.mouseMoved( new Vector2( e.getX() / scaleX, e.getY() / scaleY ));
	}

}
