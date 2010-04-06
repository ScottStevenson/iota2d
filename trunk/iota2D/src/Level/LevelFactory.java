package Level;

import java.io.FileNotFoundException;

import javax.swing.ImageIcon;

import Bounce.World;
import Bounce.RigidBody;
import Entity.EntityFactory;
import Entity.PlayerEntity;
import Game.Game;
import Sound.MidiPlayer;
import Util.Vector2;

public class LevelFactory 
{
	private EntityFactory factory;
	private Game game;
	private World physics;
	private MidiPlayer midiPlayer;
	private PlayerEntity player;
	private Level level;
	
	public LevelFactory( Game game, World physics, MidiPlayer midiPlayer )
	{
		this.game = game;
		this.physics = physics;
		this.midiPlayer = midiPlayer;	
		this.factory = new EntityFactory( physics, game.database );
	}
	
	public Level buildLevel( String levelID )
	{	
		try 
		{
			player = factory.buildPlayer( game.database.get( "levels."+ levelID, "player" ));
		}
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		level = new Level( player, physics, midiPlayer, levelID );
	
		//Set whether background should be drawn
		level.mDrawBackground = Boolean.parseBoolean( game.database.get("config", "draw_background" ));
		
		//Set level map type
		if( Boolean.parseBoolean( game.database.get( "levels." + levelID + ".map", "hasMap" )))
		{
			//System.out.print( game.database.get("levels."+ levelID + ".map", "tmxFile"));
			try
			{
				TiledMap tiledMap = new TiledMap( game.database.get( "levels."+ levelID + ".map", "tmxFile" ));
				level.addTiledMap(tiledMap);
				buildPhysicalLayer(tiledMap);
				buildEntityLayer(tiledMap);
			}
			catch(Exception ex)
			{
					System.out.print(ex.getMessage());
			}
			finally
			{
			}			
		}
			
		int numOfBackgrounds = Integer.parseInt( game.database.get( "levels." + levelID + ".backgrounds", "numOfBackgrounds" ));
		for( int i = 0; i < numOfBackgrounds; i++ )
		{
			level.addBackground( buildImageLayer( game.database.get( "levels." + levelID + ".backgrounds", "background" + i )));
		}	 
		
		int numOfForegrounds = Integer.parseInt( game.database.get( "levels." + levelID + ".foregrounds", "numOfForegrounds" ));
		for( int i = 0; i < numOfForegrounds; i++ )
		{
			level.addForeground( buildImageLayer( game.database.get( "levels." + levelID + ".foregrounds", "foreground" + i )));
		}
		
		if( Boolean.parseBoolean( game.database.get( "levels." + levelID + ".HUD", "hasHUD" )))
		{
			level.addHud( new HUD( player ));
		}
		
		level.playMusic( game.database.get( "levels."+ levelID, "midiFile" ));
		
		return level;
	}
	
	private void buildPhysicalLayer(TiledMap tiledMap) {
		
    	for(int x=0; x<tiledMap.width; x++){
    		for(int y=0; y<tiledMap.height; y++){
    			int tId = tiledMap.getTileId(x, y, 0);//Currently only supports a single layer
    			if(tiledMap.getTileProperty(tId, "isPhysical", "false").equals("true")){
	    			float xPos = tiledMap.tileWidth*x;
	    			float yPos = tiledMap.tileHeight*y;
	    			float height = tiledMap.tileHeight;
	    			float width = tiledMap.tileWidth;
	    			RigidBody obj = null;
	    			if(tiledMap.getTileProperty(tId, "isHazzard", "false").equals("true"))
	    			{	    					
	    				obj = new RigidBody( "groundHazzard", new Vector2(xPos + width / 2, yPos + height / 2), 0);
	    			}
	    			else
	    			{
	    				obj = new RigidBody( "ground", new Vector2(xPos + width / 2, yPos + height / 2), 0);
	    			}	    			
	    			obj.add( new Vector2( xPos, yPos ));
	    			obj.add( new Vector2( xPos + width, yPos ));
	    			obj.add( new Vector2( xPos + width, yPos + height ));
	    			obj.add( new Vector2( xPos, yPos + height ));
	    			obj.setRestitution( .8f );
	    			physics.add( obj );
    			}
    		}
    	}
		
	}
	
	private void buildEntityLayer(TiledMap tiledMap) {
    	int numObjects = tiledMap.getObjectCount(0);
    	for(int i=0; i<numObjects; i++){
    		
    		String name = tiledMap.getObjectName( 0, i );
			String[] type = tiledMap.getObjectType( 0, i ).split(":");
			Vector2 pos = new Vector2(tiledMap.getObjectX( 0, i ), tiledMap.getObjectY(0, i));
			System.out.println( type[0] + " " + type[1] + " " + name + " " + pos );
			
    		
			//Ghost objects
    		if( type[0].equals( "ghost" ))
    		{
    			level.addEntity( factory.buildGhost( name, type[1], pos ));
    		}
    		
    		//Object
    		else if( type[0].equals( "object" ))
    		{
    			level.addEntity( factory.buidGameEntity( name, type[1], pos ));
    		}
    		
    		//Enemies
    		else if( type[0].equals( "enemy" ))
    		{  
    			level.addEntity( factory.BuildEnemy( name, type[1], pos ));    						
    		}
    		
    		//ParticleEmitters
    		else if( type[0].equals( "emitter" ))
    		{
    			level.addEntity( factory.buildEmitter( name, type[1], pos ));
    		}
    	}
	}

	public ImageLayer buildImageLayer( String name )
	{
		ImageLayer layer = new ImageLayer( name );
		
		float x = Float.parseFloat( game.database.get( "layers.image." + name, "xoffset" ));
		float y = Float.parseFloat( game.database.get( "layers.image." + name, "yoffset" ));
		layer.setOffset( new Vector2( x, y ));
		
		int numOfImages = Integer.parseInt( game.database.get( "layers.image." + name, "numOfImages" ));
		for( int i = 0; i < numOfImages; i++ )
		{			
			layer.addImage( new ImageIcon( "content/images/" + game.database.get( "layers.image." + name, "image" + i )).getImage());
		}	
		
		return layer;
	}
}
