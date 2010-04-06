package Game;

import java.awt.event.KeyEvent;

import Entity.Enemy;
import Entity.Entity;
import Entity.ParticleEntity;
import Entity.PlayerEntity;
import Util.Vector2;

public class GameState
{
	protected boolean hit;
	protected boolean onGround;
	protected boolean healthPickUp;
	protected boolean gameOver;
	protected boolean exit = false;
	protected boolean run = false;
	protected boolean walk = false;
	protected boolean jump = false;
	protected boolean clicked = false;
	protected boolean created = false;
	protected float lastplayed = 0;
	protected float direction = 0;
	protected int prt = 0;
	protected int numBullets = 0;
	protected Vector2 mousePos = new Vector2();
	
	private Game game;

	public GameState( Game game )
	{
		this.game = game;
	}
	
	public void update()
	{
		PlayerEntity player = (PlayerEntity) this.game.level.getPlayer();
		if( jump && onGround )
		{
			player.jump();
			onGround = false;
			jump = false;
		}
		
		if( run && walk )
		{
			player.run( direction );
		}
		
		else if( walk )
		{
			player.walk( direction );
		}
		
		if( hit )
		{
			player.hit( 100 );
			hit = false;
			if( player.HP < 0 )
			{
				game.loadLevel( "gameOver" );
			}
		}
		
		if( clicked )
		{			
			Vector2 pos = player.coll.getPos();
			Vector2 v = ( mousePos.add( player.coll.getPos() ).sub( new Vector2( 960, 700 ))).sub( player.coll.getPos() );
			v.normalize();			
			ParticleEntity bullet = ( ParticleEntity )game.entityFactory.buildParticle( "bullet" + String.valueOf( numBullets ), "bullet", pos );
			
			bullet.setVelocity( v );
			game.level.addEntity( bullet );
			numBullets++;
			clicked = false;
		}
	}
	
	public void collisionEvent( String obj1, String obj2, Vector2 normal )
	{
		PlayerEntity player = (PlayerEntity) this.game.level.getPlayer();
		if( obj1.contains( player.name ) || obj2.contains( player.name ) )
		{
			if( obj2.contains( "enemy" ) || obj1.contains( "enemy" ))
			{
				hit = true;
			}
						
			if( Math.abs( normal.y ) > .8 )
			{
				onGround = true;
			}
			
			if( obj1.contains( "Hazzard" ) || obj2.contains( "Hazzard" ))
			{
				hit = true;
			}			
			
			if( obj2.contains( "health" ))
			{
				player.health( 100 );
				game.level.removeEntity( obj2 ); 
			}			
			else if( obj1.contains( "health" ))
			{
				player.health( 100 );
				game.level.removeEntity( obj1 ); 
			}
			
			if( obj1.contains( "door" ) || obj2.contains( "door" ) )
			{
				game.isLoadLevel = true;
			}
		}	
		if(  obj1.contains( "bullet" ) || obj2.contains( "bullet" ))
		{
			if( obj1.contains( "alien" ))
			{
				
				Enemy enemy = (Enemy)game.level.getEntity( obj1 );
				enemy.hit( 20 );
				if( enemy.HP < 0 )
				{
					game.level.removeEntity( obj2 );
					game.level.removeEntity( obj1 );
				}
			}
			
			if( obj2.contains( "alien" ))
			{
				Enemy enemy = (Enemy)game.level.getEntity( obj2 );
				enemy.hit( 20 );
				if( enemy.HP < 0 )
				{
					game.level.removeEntity( obj2 );
					game.level.removeEntity( obj1 );
				}
			}
		}
		
		if( obj1.contains( "sheild" ) && obj2.contains( "ground" ) || obj2.contains( "sheild" ) && obj1.contains( "ground" ))
		{
			if(( System.nanoTime() - lastplayed ) > 100000 )
			{
				lastplayed = System.nanoTime();
				game.soundManager.play( game.coinSound );
			}
		}
		
	}
	
	public void keyPressed( int keyCode )
	{
		switch( keyCode  )
		{		
		case KeyEvent.VK_ESCAPE: 
			exit = true;
			break;
		
		case KeyEvent.VK_SHIFT:	
			run = true;
			break;	
		
		case 87: // w
			jump = true;
			break;
		
		case 83: // s
			break;
			
		case 68: // d 
			direction = 1;
			walk = true;
			break;
		
		case 65: // a
			direction = -1;
			walk = true;
			break;

		case 77: // m
			game.midiPlayer.stop();
			break;	
			
		case 75: // k			
			break;
			
		case 80: // p	
			if( clicked )
			{
				//game.level.add( new ParticleEntity( "p" +  String.valueOf( prt ), mousePos.add( game.level.player.coll.getPos() ).sub( new Vector2( 960, 700 ))));
				prt++;
			}
			break;
			
		case 79: // o
			if( !created && clicked )
			{
				Entity entity = game.entityFactory.buidGameEntity( "sheild2", "sheild", mousePos.add( game.level.getPlayer().coll.getPos() ).sub( new Vector2( 960, 700 )));
			    game.level.addEntity( entity );
			    created = true;
			}
			break;
		}		
	}
	
	public void keyReleased( int keyCode )
	{
		switch( keyCode )
		{
		case KeyEvent.VK_SHIFT:
			run = false;
			break;
		case 87: // w
			break;
		case 83: // s
			break;
		case 68: // d
			walk = false;
			break;
		case 65: // a
			walk = false;
			break;
		}		
	}
	
	public void mouseClick()
	{
		clicked = true;
	}
	
	public void mouseReleased()
	{
		clicked = false;
	}
	
	public void mouseDraged( Vector2 pos )
	{
		mousePos = pos;
	}
	
	public void mouseMoved( Vector2 pos )
	{
		mousePos = pos;
	}

}
