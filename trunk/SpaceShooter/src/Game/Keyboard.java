package Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import Graphics.ScreenManager;

public class Keyboard implements KeyListener
{
	private ScreenManager screen;
	private GameState state;
	
    public Keyboard( ScreenManager screen, GameState state )
	{
		this.screen = screen;
		this.state = state;
		this.screen.getFullScreneWindow().setFocusTraversalKeysEnabled( false );
		this.screen.getFullScreneWindow().addKeyListener( this );
	}
	
	
	public void keyPressed( KeyEvent e )
	{
		System.out.println(e.getKeyCode());
		state.keyPressed( e.getKeyCode() );
		e.consume();		
	}

	public void keyReleased( KeyEvent e )
	{
		state.keyReleased( e.getKeyCode() );
		e.consume();
	}


	public void keyTyped(KeyEvent arg0) 
	{	
	}
}
