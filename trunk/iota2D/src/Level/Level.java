package Level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import Bounce.World;
import Entity.AI;
import Entity.CharacterEntity;
import Entity.Enemy;
import Entity.Entity;
import Entity.PlayerEntity;
import Sound.MidiPlayer;
import Util.Vector2;

// A object to hold layers entities and 
public class Level implements Layer
{
	private CharacterEntity player;
	private Layer hud = null;
	private World physics;
	
	private LinkedList<Layer> background;
	private LinkedList<Layer> foreground;
	
	private LinkedList<String> removeEntities;
	
	private MidiPlayer midiPlayer;

	private HashMap<String, Entity> entities;
	public Boolean mDrawBackground = true; // debug
	
	private Vector2 cameraPos;
	
	private TiledMap tiledMap = null;
	
	private Font font = new Font( "Ariel", Font.BOLD, 30 );
	
    public Level( CharacterEntity player2, World physics, MidiPlayer midiPlayer, String levelId )
	{
		this.cameraPos = new Vector2( 960, 700 );
		
		this.physics = physics;

		this.entities = new HashMap<String, Entity>();
		this.player = player2;	
				
		this.midiPlayer = midiPlayer;
		
		this.background = new LinkedList<Layer>();
		this.foreground = new LinkedList<Layer>();
		this.removeEntities = new LinkedList<String>();
	}
    
    public void playMusic( String file )
    {
    	midiPlayer.play("content/sound/"+file, true);
    }
    
    public void addTiledMap( TiledMap tiledMap )
    {
    	this.tiledMap = tiledMap;
    }
    
    public void addBackground( Layer bg )
    {
    	background.add( bg );
    }
    
    public void addForeground( Layer fg )
    {
    	background.add( fg );
    }
    
    public void addHud( Layer hud )
    {
    	this.hud = hud;
    }   
  
	public void addEntity( Entity entity )
	{
		entities.put( entity.name, entity );
	}
	
	public void removeEntity( String name )
	{
		if( entities.get( name ) != null )
		{
			physics.remove( entities.get( name ).coll );
			entities.remove( name );
		}				
	}
	
	/**
	 * Advance the level and draw a rectangular portion of it on the screen.
	 * 
	 * @param elapsedTime The time elapsed since the last update.
	 * @param g The graphics object to draw to.
	 * @param width The width of the rectangle to draw.
	 * @param height The height of the rectangle to draw.
	 */
	public void run( float elapsedTime, Graphics2D g, int width, int height )
	{			
		//Upper left pixel of area to draw
		int xPos =  (int)player.coll.getPos().x-width/2;
		int yPos = (int)player.coll.getPos().y-height/2;
		if (xPos<0)
			xPos=0;
		if (yPos<0)
			yPos=0;
		
		update( elapsedTime );
		g.setColor( Color.white );
		draw( g, xPos, yPos, width, height);
		g.dispose();
	}
	
	// updates the objects in the level by the elapsed time
	// @param elapsedTime the time elapsed since the last update
	public void update( float elapsedTime )
	{	
		player.update( elapsedTime );
		Iterator<String> entityIter = entities.keySet().iterator();
		while( entityIter.hasNext() )
		{
			Entity entity = entities.get( entityIter.next() );
			entity.update( elapsedTime );
			if( !entity.isAlive )
			{
				removeEntities.add( entity.name );
			}
		}	
		
		for( int i = 0; i < removeEntities.size(); i++ )
		{
			removeEntity( removeEntities.get(i) );
		}
		
		removeEntities.clear();
	}
	
	public LinkedList<Entity> getEntities( String type )
	{
		LinkedList<Entity> tempEntities = new LinkedList<Entity>();
		Iterator<String> entityIter = entities.keySet().iterator();
		while( entityIter.hasNext() )
		{
			Entity entity = entities.get( entityIter.next() );
			if( entity.name.contains( type ))
			{
				tempEntities.add(entity);
			}
		}	
		return tempEntities;
	}
	
	public void addAI( AI ai, String type )
	{
		
	}
	
	/**
	 * Draws the entire level to the given graphics object.
	 * 
	 * @param g The graphics object to draw to
	 */
	public void draw( Graphics2D g )
	{
		AffineTransform trans = g.getTransform();
		AffineTransform transOld = new AffineTransform( trans );
		
		if( mDrawBackground )
		{
			for( int i = 0; i < background.size(); i++ )
			{
				background.get( i ).draw( g );
			}
		}
		else
		{
			g.setBackground( Color.darkGray );
			g.clearRect(0, 0, 1920, 1080);
		}

		//debug
		//g.setFont( font );
		//g.drawString( "FPS " + fps, 150, 50 );
		
		
		
		trans.translate( -player.coll.getPos().x + cameraPos.x, -player.coll.getPos().y + cameraPos.y );
		
		g.setTransform( trans );

		// Draw map
		if( tiledMap != null )//simple
		{
			tiledMap.draw(g);		
		}

		//Draw entities
		Iterator<String> entityIter = entities.keySet().iterator();;
		while( entityIter.hasNext() )
		{
			entities.get( entityIter.next() ).draw( g );
		}
		
		player.draw( g );
		
		g.setTransform( transOld );
		for( int i = 0; i < foreground.size(); i++ )
		{
			foreground.get( i ).draw( g );
		}
		
		if( hud != null )
		{
			hud.draw( g );	
		}
	}
	
	/**
	 * Draws a rectangular section of the level to the main Graphics 2D object.
	 * 
	 * @param g The graphics object to draw to
	 * @param x The x position (top left) of the rectangular area to draw 
	 * @param y The y position (top left) of the rectangular area to draw
	 * @param width The width of the rectangular area to draw
	 * @param height The height of the rectangular area to draw
	 */
	public void draw( Graphics2D g, int x, int y, int width, int height) {
		if( mDrawBackground )
		{
			for( int i = 0; i < background.size(); i++ )
			{
				background.get( i ).draw( g );
			}
		}
		else
		{
			g.setBackground(Color.darkGray);
			g.clearRect(0, 0, 1920, 1080);
		}

		//debug
		//g.setFont( font );
		//g.drawString( "FPS " + fps, 150, 50 );
		
		
		AffineTransform trans = g.getTransform();
		AffineTransform transOld = new AffineTransform( trans );
		
		trans.translate( -player.coll.getPos().x + cameraPos.x, -player.coll.getPos().y + cameraPos.y );
		
		g.setTransform( trans );

		// Draw map
		if( tiledMap != null )//simple
		{
			tiledMap.draw(g, x, y, width, height);		
		}

		//Draw entities
		Iterator<String> entityIter = entities.keySet().iterator();;
		while( entityIter.hasNext() )
		{
			entities.get( entityIter.next() ).draw( g );
		}
		
		player.draw( g );
		
		g.setTransform( transOld );
		for( int i = 0; i < foreground.size(); i++ )
		{
			foreground.get( i ).draw( g );
		}
		
		if( hud != null )
		{
			hud.draw( g );	
		}
	}
	
	public CharacterEntity getPlayer()
	{
		return this.player;
	}
	
	public Entity getEntity( String name )
	{
		return entities.get( name );
	}
}
