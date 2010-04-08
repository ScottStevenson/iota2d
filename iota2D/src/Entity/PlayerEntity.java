package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Bounce.RigidBody;
import Graphics.Sprite;
import Util.Vector2;

public class PlayerEntity extends CharacterEntity
{		
	public int HP = 1;
	public int MP = 1;
	public int AP = 1;
	
	//Sprite's
	private Sprite walkSprite;
	private Sprite runSprite;
	private Sprite standSprite;
	private AffineTransform transform;
	
	//Physics Information
	private float height;
	private float width;
	
	//Character Attributes
	public String name = "";
	
	public PlayerEntity( String name, RigidBody coll, 
            			 Sprite standSprite, Sprite walkSprite, Sprite runSprite,
            			 float height, float width,
            			 int HP, int MP, int AP )
	{
		super( name, coll );
		this.HP = HP;
		this.MP = MP;
		this.AP = AP;
		this.name = name;
		this.standSprite = standSprite;
		this.walkSprite = walkSprite;
		this.runSprite = runSprite;
		state = EntityState.STATIC;
		this.height = height;
		this.width = width;
	}
	
	public void walk( float direction )
	{
		state = EntityState.WALKING;
		coll.addForce( new Vector2( .1f * direction, 0 ));
	}
	
	public void run( float direction )
	{
		state = EntityState.RUNNING;
		coll.addForce( new Vector2( .2f * direction, 0 ));
	}
	
	public void jump()
	{
		state = EntityState.JUMPING;
		coll.setSpeed( new Vector2( 0, -3 ));
		System.out.println("jump");
	}
	
	public void hit( int dam )
	{
		HP -= dam;
	}
	
	public void health( int health )
	{
		HP += health;
	}
	
	public void update( float elapsedTime )
	{		
		switch( state )
		{
		case  STATIC: standSprite.update( elapsedTime ); break;
		case  WALKING: walkSprite.update( elapsedTime ); break;
		case  RUNNING: runSprite.update( elapsedTime ); break;	
		default: standSprite.update( elapsedTime ); break;
		}		
	}
		
	public void draw( Graphics2D g )
	{
		
		transform = new AffineTransform();
		transform.translate( coll.getPos().x - width/2, coll.getPos().y - height/2 );

		switch( state )
		{		
		case  STATIC: g.drawImage( standSprite.getImage(), transform, null ); break;
		case  WALKING: g.drawImage( walkSprite.getImage(), transform, null ); break;
		case  RUNNING: g.drawImage( runSprite.getImage(), transform, null ); break;	
		default: g.drawImage( standSprite.getImage(), transform, null ); break;
		}
	}
}
