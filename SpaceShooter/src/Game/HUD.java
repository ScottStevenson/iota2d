package Game;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import Entity.ShipEntity;
import Level.Layer;

public class HUD implements Layer
{
	private ShipEntity character;
	private Image bar;
	private Image hp;
	private Font font = new Font( "Ariel", Font.BOLD, 30 );
	
	public HUD( ShipEntity character )
	{
		this.character = character;		
		load();
	}
	
	private void load()
	{
		bar =  new ImageIcon( "content/sprites/bar.png" ).getImage();
		hp  =  new ImageIcon( "content/sprites/hp.png" ).getImage();
	}
	
	public void draw( Graphics2D g )
	{
		g.drawImage( hp, 10, 900, null );

		for( int i = character.HP; i > 0 ; i -= 10)
		{
			g.drawImage( bar, 80 + i, 920, null );
		}
		g.setFont(font);
		g.drawString( "Points  " + character.points, 1500, 100 );
	}	
}
