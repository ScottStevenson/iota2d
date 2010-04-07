package Entity;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import Bounce.Particle;
import Bounce.World;
import Bounce.PhysicsObject;
import Bounce.RigidBody;
import Data.Database;
import Graphics.Sprite;
import Util.Vector2;

public class EntityFactory
{
	private World physics;
	private Database database;
	
	public EntityFactory( World physics, Database database )
	{
		this.physics = physics;
		this.database = database;
	}
	
	public Enemy BuildEnemy( String name, String type, Vector2 pos )
	{
		float height = Integer.parseInt( database.get( "entities.enemies." + type, "height" ));
		float width = Integer.parseInt( database.get( "entities.enemies." + type, "width" ));
		int HP = Integer.parseInt( database.get( "entities.enemies." + type, "HP" ));
		
		Sprite sprite = new Sprite();
		
		int numOfFrames = Integer.parseInt( database.get( "entities.enemies." + type + ".sprite" , "numOfFrames" ));
		for( int i = 0; i < numOfFrames; i++ )
		{
			String file =  "content/sprites/" + database.get( "entities.enemies." + type + ".sprite" , "image" + i );
			long length = Long.parseLong( database.get( "entities.enemies." + type + ".sprite" , "length" + i ));
			sprite.addFrame( getBufferedImage( file ), length );
		}
				
		RigidBody coll = new RigidBody( name + "enemy", pos, 10 );
		coll.add( new Vector2( pos.x - width / 2, pos.y - height / 2 ));
		coll.add( new Vector2( pos.x + width / 2, pos.y - height / 2 ));
		coll.add( new Vector2( pos.x + width / 2, pos.y + height / 2 ));
		coll.add( new Vector2( pos.x - width / 2, pos.y + height / 2 ));
		physics.add( coll );
		
		return new Enemy( name + "enemy", coll, sprite, height, width, HP );
	}
	
	public GameEntity buidGameEntity( String name, String type, Vector2 pos )
	{
		float height = Integer.parseInt( database.get( "entities.gameObjects." + type, "height" ));
		float width = Integer.parseInt( database.get( "entities.gameObjects." + type, "width" ));
		float mass = Integer.parseInt( database.get( "entities.gameObjects." + type, "mass" ));
        Sprite sprite = new Sprite();
        int numOfFrames = Integer.parseInt( database.get( "entities.gameObjects." + type + ".sprite", "numOfFrames" ));
		for( int i = 0; i < numOfFrames; i++ )
		{
			String file = "content/sprites/" + database.get( "entities.gameObjects." + type + ".sprite", "image" + i );
			long length = Long.parseLong( database.get( "entities.gameObjects." + type + ".sprite", "length" + i ));
			sprite.addFrame( getBufferedImage( file ), length );
		}
		
		RigidBody coll = new RigidBody( name, pos, 1 );
		for( int i = 0; i <= 7; i++ )
		{
			float x = ( float )( Math.cos( 2 * Math.PI * i / 8 ) * width / 2 ) + pos.x;
			float y = ( float )( Math.sin( 2 * Math.PI * i / 8 ) * width / 2 ) + pos.y;
			coll.add( new Vector2( x, y ));
		}
		coll.setIsRoteted( true );
		physics.add( coll );
		 
		return new GameEntity( name, coll, sprite, height, width );
	}	
	
	public ShipEntity buildShip( String name ) throws FileNotFoundException
	{
		System.out.println( name );
		int xPos = Integer.parseInt( database.get( "entities." + name, "xPos" ));
		int yPos = Integer.parseInt( database.get( "entities." + name, "yPos" ));
		int height = Integer.parseInt( database.get( "entities." + name, "height" ));
		int width = Integer.parseInt( database.get( "entities." + name, "width" ));
		int HP = Integer.parseInt( database.get( "entities." + name, "HP" ));
		int weight = Integer.parseInt( database.get( "entities." + name, "weight" ));
		
		RigidBody coll = new RigidBody( name, new Vector2( xPos, yPos ), weight );
		coll.add( new Vector2( xPos - width / 2, yPos - height / 2 ));
		coll.add( new Vector2( xPos + width / 2, yPos - height / 2 ));
	    coll.add( new Vector2( xPos + width / 2, yPos + height / 2 ));
		coll.add( new Vector2( xPos - width / 2, yPos + height / 2 ));
		coll.setIsRoteted( true );
		coll.setRestitution( .8f );
		coll.setAngularDampening( .0005f );
		
		Sprite staticSprite = new Sprite();
		Sprite thrustSprite = new Sprite();
		int numOfStaticFrames = Integer.parseInt( database.get( "entities." + name + ".staticSprite", "numOfFrames" ));
		for( int i = 0; i < numOfStaticFrames; i++ )
		{
			staticSprite.addFrame( getBufferedImage( "content/sprites/" + database.get( "entities." + name + ".staticSprite", "image" + i )), 100 );
			staticSprite.addFrame( getBufferedImage( "content/sprites/ship.png" ), 100 );
		}
		int numOfThrustFrames = Integer.parseInt( database.get( "entities." + name + ".thrustSprite", "numOfFrames" ));
		for( int i = 0; i < numOfThrustFrames; i++ )
		{
			thrustSprite.addFrame( getBufferedImage( "content/sprites/" + database.get( "entities." + name + ".thrustSprite", "image" + i )), 100 );
			thrustSprite.addFrame( getBufferedImage( "content/sprites/shipThrust.png" ), 100 );
		}	
		physics.add( coll );
		return new ShipEntity( name, coll, staticSprite, thrustSprite, height, width, HP );
	}
		
	public PlayerEntity buildPlayer( String name ) throws FileNotFoundException
	{
		int xPos = Integer.parseInt( database.get( "entities.player", "xPos" ));
		int yPos = Integer.parseInt( database.get( "entities.player", "yPos" ));
		int height = Integer.parseInt( database.get( "entities.player", "height" ));
		int width = Integer.parseInt( database.get( "entities.player", "width" ));
		int HP = Integer.parseInt( database.get( "entities.player", "HP" ));
		int MP = Integer.parseInt( database.get( "entities.player", "MP" ));
		int AP = Integer.parseInt( database.get( "entities.player", "AP" ));
		int weight = Integer.parseInt( database.get( "entities.player", "weight" ));
		
		RigidBody coll = new RigidBody( name, new Vector2( xPos, yPos ), weight );
		coll.add( new Vector2( xPos - width / 2, yPos - height / 2 ));
		coll.add( new Vector2( xPos + width / 2, yPos - height / 2 ));
	    coll.add( new Vector2( xPos + width / 2, yPos + height / 2 ));
		coll.add( new Vector2( xPos - width / 2, yPos + height / 2 ));
		coll.setRestitution( .8f );
		
		Sprite standSprite = new Sprite();
		Sprite walkSprite = new Sprite();
		Sprite runSprite = new Sprite();
		
		int numOfStandFrames = Integer.parseInt( database.get( "entities.player.standSprite", "numOfFrames" ));
		for( int i = 0; i < numOfStandFrames; i++ )
		{
			standSprite.addFrame( getBufferedImage( "content/sprites/" + database.get( "entities.player.standSprite", "image" + i ) ), 100 );
		}
		
		int numOfWalkFrames = Integer.parseInt( database.get( "entities.player.walkSprite", "numOfFrames" ));
		for( int i = 0; i < numOfWalkFrames; i++ )
		{
			walkSprite.addFrame( getBufferedImage( "content/sprites/" + database.get( "entities.player.walkSprite", "image" + i ) ), Integer.parseInt( database.get( "entities.player.walkSprite", "length" + i )));
		}
		
		int numOfRunFrames = Integer.parseInt( database.get( "entities.player.walkSprite", "numOfFrames" ));
		for( int i = 0; i < numOfRunFrames; i++ )
		{
			runSprite.addFrame( getBufferedImage( "content/sprites/" + database.get( "entities.player.runSprite", "image" + i ) ), Integer.parseInt( database.get( "entities.player.runSprite", "length" + i )));
		}
		
		physics.add( coll );
		return new PlayerEntity( name, coll, standSprite, standSprite, standSprite, height, width, HP, MP, AP );
	}
	
	public Entity buildGhost( String name, String type, Vector2 pos )
	{
		float height = Integer.parseInt( database.get( "entities.gameObjects." + type, "height" ));
		float width = Integer.parseInt( database.get( "entities.gameObjects." + type, "width" ));

		RigidBody coll = new RigidBody( name, pos, -1 );
		coll.add( new Vector2( pos.x - width / 2, pos.y - height / 2 ));
		coll.add( new Vector2( pos.x + width / 2, pos.y - height / 2 ));
		coll.add( new Vector2( pos.x + width / 2, pos.y + height / 2 ));
		coll.add( new Vector2( pos.x - width / 2, pos.y + height / 2 ));
		physics.add( coll );
		
		Sprite sprite = new Sprite();
		
		int numOfFrames = Integer.parseInt( database.get( "entities.gameObjects." + type + ".sprite", "numOfFrames" ));
		for( int i = 0; i < numOfFrames; i++ )
		{
			String file = "content/sprites/" + database.get( "entities.gameObjects." + type + ".sprite", "image" + i );
			long length = Long.parseLong( database.get( "entities.gameObjects." + type + ".sprite", "length" + i ));
			sprite.addFrame( getBufferedImage( file ), length );
		}	
		return new GameEntity( name, coll, sprite, width, height );
	}
	
	public Entity buildParticle( String name, String type, Vector2 pos )
	{
		float life = Float.parseFloat( database.get( "entities.particles." + type, "life" ));		
		Particle part = new Particle( name, pos, 1 );		
		physics.add( part );
		
		Sprite sprite = new Sprite();
	    int numOfFrames = Integer.parseInt( database.get( "entities.particles." + type + ".sprite", "numOfFrames" ));
		for( int i = 0; i < numOfFrames; i++ )
		{
			String file = "content/sprites/" + database.get( "entities.particles." + type + ".sprite", "image" + i );
			long length = Long.parseLong( database.get( "entities.particles." + type + ".sprite", "length" + i ));
			sprite.addFrame( getBufferedImage( file ), length );
		}
		
		return new ParticleEntity( name, sprite, part, pos, life );
	}
	
	public ParticleEmitter buildEmitter( String name, String type, Vector2 pos )
	{
		Particle part = new Particle( name, pos, 1 );		
		
		float life = Float.parseFloat( database.get( "entities.particleEmitters." + type, "life" ));
		float randomAmount = Float.parseFloat( database.get( "entities.particleEmitters." + type, "randomAmount" ));
	    float vX = Float.parseFloat( database.get( "entities.particleEmitters." + type, "vX" ));
	    float vY = Float.parseFloat( database.get( "entities.particleEmitters." + type, "vY" ));
	    float spawnRate = Float.parseFloat( database.get( "entities.particleEmitters." + type, "spawnRate" ));
	    physics.add( part );	
	    
	    String particleType = database.get( "entities.particleEmitters." + type, "particleType" );
	    
	    Sprite sprite = new Sprite();
	    int numOfFrames = Integer.parseInt( database.get( "entities.particles." + particleType + ".sprite", "numOfFrames" ));
		for( int i = 0; i < numOfFrames; i++ )
		{
			String file = "content/sprites/" + database.get( "entities.particles." + particleType + ".sprite", "image" + i );
			long length = Long.parseLong( database.get( "entities.particles." + particleType + ".sprite", "length" + i ));
			sprite.addFrame( getBufferedImage( file ), length );
		}
		
		return new ParticleEmitter( name, part, sprite, life, type, physics, database, randomAmount, new Vector2( vX, vY ), spawnRate  );
	}
	
	private BufferedImage getBufferedImage( String file )
	{
		BufferedImage img = null;
		try 
		{
			img = ImageIO.read( new File( file ));
		}
		catch( IOException e )
		{
		}
		return img;
	}
	
}
