package Game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Graphics.ScreenManager;

public class MouseClick implements MouseListener
{
	ScreenManager screen;
	GameState state;

	public MouseClick( ScreenManager screen, GameState state )
	{
		this.screen = screen;
		this.screen.getFullScreneWindow().addMouseListener( this );
		this.state = state;
	}
	
	public void mouseClicked(MouseEvent arg0) 
	{
		state.mouseClick();
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) 
	{
		state.mouseClick();
	}

	public void mouseReleased(MouseEvent arg0) 
	{
		state.mouseReleased();		
	}

}
