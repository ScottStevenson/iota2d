package Game;

import java.awt.event.KeyEvent;

import Entity.Enemy;
import Entity.Entity;
import Entity.EntityState;
import Entity.ParticleEmitter;
import Entity.ParticleEntity;
import Entity.ShipEntity;
import Util.Vector2;

public class GameState
{
	protected boolean hit;
	protected boolean onGround;
	protected boolean healthPickUp;
	protected boolean gameOver;
	protected boolean exit = false;

	protected boolean up = false;
	protected boolean down = false;
	protected boolean left = false;
	protected boolean right = false;
	protected boolean turnRight = false;
	protected boolean turnLeft = false;
	protected boolean fire = false;

	protected boolean clicked = false;
	protected boolean created = false;
	protected float lastplayed = 0;
	protected float direction = 0;
	protected int prt = 0;
	protected int numBullets = 0;
	protected Vector2 mousePos = new Vector2();
	private float lastFired = 0;
	
	private Game game;

	public GameState( Game game )
	{
		this.game = game;
	}
	
	public void update()
	{
		ShipEntity ship = (ShipEntity) game.level.getPlayer();
		float thrustX = 0;
		float thrustY = 0;
		float torque = 0;
		Vector2 pos = ship.coll.getPos();
		
		ship.state = EntityState.STATIC;
		if( up )
		{
			thrustY += -.01;
			ship.state = EntityState.THRUST;
		}
		if( down )
		{
			thrustY += .01;
			ship.state = EntityState.THRUST;
		}
		if( left )
		{
			thrustX += -.01;
			ship.state = EntityState.THRUST;
		}
		if( right )
		{
			thrustX += .01;
			ship.state = EntityState.THRUST;
		}
		if( turnRight )
		{
			torque += 5;
		}
		if( turnLeft )
		{
			torque -= 5;
		}
	
		ship.thrust( new Vector2( thrustX, thrustY ));
		ship.turn( torque );
		
		if( hit )
		{
			ship.hit( 30 );
			hit = false;
			if( ship.HP < 0 )
			{
				ParticleEmitter explosion = game.entityFactory.buildEmitter( "explosionShip", "explosion", ship.coll.getPos() );
				game.level.addEntity( explosion );
				game.loadLevel( "space" );
			}
		}

		
		if( fire )
		{						
			Vector2 v = new Vector2( -(float)(Math.cos( ship.coll.getRotation())), -(float)(Math.sin( ship.coll.getRotation() )));
			v.normalize();			
			ParticleEntity bullet = ( ParticleEntity )game.entityFactory.buildParticle( "bullet" + String.valueOf( numBullets ), "bullet", pos );
			
			bullet.setVelocity( v );
			game.level.addEntity( bullet );
			numBullets++;
			fire = false;
		}
		
		if( System.nanoTime() - lastFired > 1000000000 &&  game.level.getEntity( "alienBaseenemy" )!=null )
		{
			Vector2 v = pos.sub( game.level.getEntity( "alienBaseenemy" ).coll.getPos() );
			v.normalize();
			ParticleEntity bullet = ( ParticleEntity )game.entityFactory.buildParticle( "beam" + String.valueOf( numBullets ), "alienBullet", game.level.getEntity( "alienBaseenemy" ).coll.getPos() );
			bullet.setVelocity( v );
			game.level.addEntity( bullet );
			numBullets++;
			lastFired = System.nanoTime();			
		}
	}
	
	public void collisionEvent( String obj1, String obj2, Vector2 normal )
	{
		ShipEntity ship = (ShipEntity) game.level.getPlayer();
		if( obj1.equals( ship.name ) || obj2.equals( ship.name ) )
		{
			if( obj2.contains( "alienShip" ) && game.level.getEntity( obj2 )!= null )
			{
				ParticleEmitter explosion = game.entityFactory.buildEmitter( "explosionShip", "explosion", game.level.getEntity( obj2 ).coll.getPos() );
				game.level.removeEntity( obj2 );
				game.level.addEntity( explosion );
				hit = true;
			}
			else if( obj2.contains( "alienShip" ) && game.level.getEntity( obj2 )!= null  )
			{
				ParticleEmitter explosion = game.entityFactory.buildEmitter( "explosionShip", "explosion", game.level.getEntity( obj1 ).coll.getPos() );
				game.level.removeEntity( obj1 );
				game.level.addEntity( explosion );
				hit = true;
			}
			
			if( obj1.contains( "alienBase" ) || obj2.contains( "alienBase" ))
			{
				hit = true;
			}
			
			if( obj2.contains( "asteroid" ) || obj2.contains( "asteroid" ))
			{
				hit = true;
			}
			
			if( obj1.contains( "ground" ) || obj2.contains( "ground" ))
			{
				hit = true;
			}			
			
		}	
		if( obj1.contains( "beam" ) || obj2.contains( "beam" ))
		{
			if( obj1.equals( ship.name ) )
			{
				hit = true;
				game.level.removeEntity( obj2 );
			}
			else if( obj2.equals( ship.name ))
			{
				hit = true;
				game.level.removeEntity( obj1 ); 
			}
			
			if( obj2.contains( "asteroid" ))
			{
				game.level.removeEntity( obj1 );
			}
			else if( obj1.contains("asteroid" ))
			{
				game.level.removeEntity( obj2 );
			}
		}
		
		if(  obj1.contains( "bullet" ) || obj2.contains( "bullet" ))
		{
			
			if( obj1.contains( "alienShip" ))
			{
				System.out.print("ship");
				Enemy enemy = (Enemy)game.level.getEntity( obj1 );
				enemy.hit( 50 );
				game.level.removeEntity( obj2 );
				ship.points += 200;
				if( enemy.HP < 0 )
				{		
					ParticleEmitter explosion = game.entityFactory.buildEmitter( "explosionShip", "explosion", game.level.getEntity( obj1 ).coll.getPos() );
					game.level.removeEntity( obj1 );
					game.level.addEntity( explosion );
				}
			}			
			else if( obj2.contains( "alienShip" ))
			{
				Enemy enemy = (Enemy)game.level.getEntity( obj2 );
				if( enemy != null )
				{
					enemy.hit( 50 );
					game.level.removeEntity( obj1 );
					ship.points += 200;
					if( enemy.HP < 0 )
					{
						ParticleEmitter explosion = game.entityFactory.buildEmitter( "explosionShip", "explosion", game.level.getEntity( obj2 ).coll.getPos() );
						game.level.removeEntity( obj2 );
						game.level.addEntity( explosion );
					}
				}
			}
			if( obj1.contains( "alienBase" ))
			{
				Enemy enemy = (Enemy)game.level.getEntity( obj1 );
				enemy.hit( 50 );
				ship.points += 200;
				if( enemy.HP < 0 )
				{		
					ParticleEmitter explosion = game.entityFactory.buildEmitter( "explosionShip", "explosion", game.level.getEntity( obj1 ).coll.getPos() );
					game.level.removeEntity( obj1 );
					game.level.addEntity( explosion );
				}
			}			
			else if( obj2.contains( "alienBase" ))
			{
				Enemy enemy = (Enemy)game.level.getEntity( obj2 );
				if( enemy != null )
				{
					enemy.hit( 50 );
					game.level.removeEntity( obj1 );
					ship.points += 200;
					if( enemy.HP < 0 )
					{
						ParticleEmitter explosion = game.entityFactory.buildEmitter( "explosionShip", "explosion", game.level.getEntity( obj2 ).coll.getPos() );
						game.level.removeEntity( obj2 );
						game.level.addEntity( explosion );
					}
				}
			}
			if( obj2.contains("asteroid" ))
			{
				game.level.removeEntity( obj1 );
			}
			else if( obj1.contains("asteroid" ))
			{
				game.level.removeEntity( obj2 );
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
			break;	
		
		case 87: // w
			up = true;
			break;
		
		case 83: // s
			down = true;
			break;
			
		case 68: // d 
			right = true;
			break;
		
		case 65: // a
			left = true;
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
			break;
		case 38: // up	
			fire = true;
			break;
		case 40: // down			
			break;
		case 37: // left	
			turnLeft = true;
			break;
		case 39: // right
			turnRight = true;
			break;
		}		
	}
	
	public void keyReleased( int keyCode )
	{
		switch( keyCode )
		{
		case KeyEvent.VK_SHIFT:
			break;
		case 87: // w
			up = false;
			break;
		case 83: // s
			down = false;
			break;
		case 68: // d
			right = false;
			break;
		case 65: // a
			left = false;
			break;
		case 38: // up	
			fire = false;
			break;
		case 40: // down			
			break;
		case 37: // left	
			turnLeft = false;
			break;
		case 39: // right
			turnRight = false;
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
