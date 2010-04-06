package Level;

import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import Entity.CharacterEntity;
import Entity.PlayerEntity;

public class HUD implements Layer
{
	private PlayerEntity character;
	private Image bar;
	private Image hp;
	private Image mp;
	private Image ap;
	
	public HUD( PlayerEntity character )
	{
		this.character = character;		
		load();
	}
	
	private void load()
	{
		bar =  new ImageIcon( "content/sprites/bar.png" ).getImage();
		hp  =  new ImageIcon( "content/sprites/hp.png" ).getImage();
		mp  =  new ImageIcon( "content/sprites/mp.png" ).getImage();
		ap  =  new ImageIcon( "content/sprites/ap.png" ).getImage();
	}
	
	public void draw( Graphics2D g )
	{
		g.drawImage( hp, 10, 900, null );
		g.drawImage( mp, 10, 945, null );
		g.drawImage( ap, 10, 990, null );
		for( int i = character.HP; i > 0 ; i -= 10)
		{
			g.drawImage( bar, 80 + i, 920, null );
		}
		for( int i = character.MP; i > 0 ; i -= 10)
		{
			g.drawImage( bar, 80 + i, 965, null );
		}
		for( int i = character.AP; i > 0 ; i -= 10)
		{
			g.drawImage( bar, 80 + i, 1010, null );
		}
	}	
}
