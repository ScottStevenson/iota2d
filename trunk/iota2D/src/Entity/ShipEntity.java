package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Graphics.Sprite;
import Bounce.RigidBody;
import Util.Vector2;

public class ShipEntity extends CharacterEntity
{		
	public int HP = 1;
	public int points = 0;
	private Sprite staticSprite;
	private Sprite thrustSprite;
	float width;
	float height;
	public EntityState state = EntityState.STATIC;
	
	public ShipEntity( String name, RigidBody coll, 
            			 Sprite staticSprite, Sprite thrustSprite,
            			 float height, float width,
            			 int HP )
	{
		super( name, coll );
		this.staticSprite = staticSprite;
		this.thrustSprite = thrustSprite;
		this.HP = HP;
		this.width = width;
		this.height = height;
	}
	
	public void turn( float torque )
	{
		this.coll.addTorque( torque );
	}
	
	public void thrust( Vector2 thrust )
	{
		this.coll.setForce( thrust );
	}
	
	public void hit( int dam )
	{
		HP -= dam;
	}
	
	public void health( int health )
	{
		HP += health;
	}

	@Override
	public void draw( Graphics2D g ) 
	{
		AffineTransform transform = new AffineTransform();
		transform.translate( coll.getPos().x - width / 2, coll.getPos().y - height / 2 );
		transform.rotate( coll.getRotation(), width / 2, height / 2 );
		if( state == EntityState.STATIC )
		{
			g.drawImage( staticSprite.getImage(), transform, null );
		}
		else if( state == EntityState.THRUST )
		{
			g.drawImage( thrustSprite.getImage(), transform, null );
		}
	}

	@Override
	public void update( float elapsedTime )
	{
		this.staticSprite.update( elapsedTime );
	}
}
