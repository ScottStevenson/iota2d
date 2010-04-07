package Level;

import java.awt.Graphics2D;
import java.util.LinkedList;

import Util.Vector2;

import Graphics.Sprite;

public class SpriteLayer implements Layer
{
	private LinkedList<Sprite> sprites;
	private LinkedList<Vector2> offsets;
	String name;
	
	public SpriteLayer( String name )
	{
		this.name = name;
		this.sprites = new LinkedList<Sprite>();
		this.offsets = new LinkedList<Vector2>();
	}
	
	@Override
	public void draw( Graphics2D g ) 
	{
		for( int i = 0; i < sprites.size(); i++ )
		{
			int x = (int) offsets.get( i ).x;
			int y = (int) offsets.get( i ).y;			
			g.drawImage( sprites.get( i ).getImage(), x, y, null );
		}
	}
	
	public void add( Sprite sprite, Vector2 offset )
	{
		sprites.add( sprite );
		offsets.add( offset );
	}

}
