package Game;

import java.awt.event.KeyEvent;
import Entity.ParticleEntity;
import Bounce.Particle;
import Util.Vector2;

public class GameState
{
	protected boolean death;
	protected boolean gameOver;
	protected boolean exit = false;
	protected boolean run = false;
	protected boolean walk = false;
	protected boolean jump = false;
	protected boolean clicked = false;
	protected boolean created = false;
	protected long lastplayed = 0;
	protected float direction = 0;
	protected int prt = 0;
	protected Vector2 mousePos = new Vector2();
	
	private Game game;
	private boolean gravUp;
	private boolean gravDown;
	private boolean gravRight;
	private boolean gravLeft;

	public GameState( Game game )
	{
		this.game = game;
	}
	
	public void update()
	{
		if( gravUp )
		{
			game.physics.setGravity(new Vector2(0, (float)-.003));
		}
		
		else if( gravDown )
		{
			game.physics.setGravity(new Vector2(0, (float).003));
		}
		
		else if( gravLeft )
		{
			game.physics.setGravity(new Vector2((float)-.003, 0));
		}
		
		else if( gravRight )
		{
			game.physics.setGravity(new Vector2((float).003, 0));
		}
		
		if( death )
		{
			game.soundManager.play(game.deathSound);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			game.loadLevel( game.curLevel );
				
				
			death = false;
		}
		
		if( clicked )
		{
			//Do nothing
		}
	}
	
	public void collisionEvent( String obj1, String obj2, Vector2 normal )
	{
		if( obj1.contains( game.level.getPlayer().name ) || obj2.contains( game.level.getPlayer().name ) )
		{
			//Ball collides with red spikey
			if( obj2.contains( "red" ) || obj1.contains( "red" ))
			{
				death = true;
			}						
			
			//Ball collides with hazard tile
			if( obj1.contains( "Hazzard" ) || obj2.contains( "Hazzard" ))
			{
				death = true;
			}			
			
			//Ball collides with exit door			
			if( obj1.contains( "door" ) || obj2.contains( "door" ) )
			{
				game.isLoadNextLevel = true;
			}
			
			//Ball collides with wall/ground
			if( obj1.contains( "ground" ))
			{
				if(( System.nanoTime() - lastplayed ) > 100000000 )
				{
					lastplayed = System.nanoTime();
					game.soundManager.play(game.beepSound);
				}
			}
		}	
		
		//Red and green spikeys collide
		if( obj1.contains( "red" ) && obj2.contains( "green" ) || obj2.contains( "red" ) && obj1.contains( "green" ))
		{
				Vector2 redPos;
				Vector2 greenPos;
				if( obj1.contains("red") ){
					redPos = game.level.getEntity(obj1).coll.getPos();
					greenPos = game.level.getEntity(obj2).coll.getPos();
				}else{
					redPos = game.level.getEntity(obj2).coll.getPos();
					greenPos = game.level.getEntity(obj1).coll.getPos();
				}
				
				////////Particle explosion effect
				for(int x=0; x<6; x++){
					for(int y=0; y<6; y++){
					Particle p = new Particle("p"+game.pNum, redPos, 1);
					game.physics.add(p);
					ParticleEntity pEnt = new ParticleEntity("p"+game.pNum, game.redParticle, p, redPos, 1000);
					pEnt.setVelocity(new Vector2((float)Math.sin((2*3.14159)*x/6)/2, (float)Math.cos((2*3.14159)*y/6)/2));
					game.level.addEntity(pEnt);
					game.pNum += (game.pNum+1)%2500;
					}
				}
				
				for(int x=0; x<6; x++){
					for(int y=0; y<6; y++){
					Particle p = new Particle("p"+game.pNum, greenPos, 1);
					game.physics.add(p);
					ParticleEntity pEnt = new ParticleEntity("p"+game.pNum, game.greenParticle, p, greenPos, 1000);
					pEnt.setVelocity(new Vector2((float)Math.sin((2*3.14159)*x/6)/2, (float)Math.cos((2*3.14159)*y/6)/2));
					game.level.addEntity(pEnt);
					game.pNum += (game.pNum+1)%2500;
					}
				}
				///////
				
				
				game.soundManager.play( game.shatterSound );
				game.level.removeEntity(obj1);
				game.level.removeEntity(obj2);
		}
		
	}
	
	public void keyPressed( int keyCode )
	{
		switch( keyCode  )
		{		
		case KeyEvent.VK_ESCAPE: 
			exit = true;
			break;
		
		case 87: // w
			gravUp = true;
			break;
		
		case 83: // s
			gravDown = true;
			break;
			
		case 68: // d 
			gravRight = true;
			break;
		
		case 65: // a
			gravLeft = true;
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
			gravUp = false;
			break;
		case 83: // s
			gravDown = false;
			break;
		case 68: // d
			gravRight = false;
			break;
		case 65: // a
			gravLeft = false;
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
