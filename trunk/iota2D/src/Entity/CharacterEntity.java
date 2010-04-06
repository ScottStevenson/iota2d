package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Bounce.RigidBody;
import Graphics.Sprite;

public class CharacterEntity extends Entity
{	
	public EntityState state;
	
	//Character Attributes
	public String name = "";

	//Sprite's
	private Sprite walkSprite;
	private Sprite runSprite;
	private Sprite standSprite;
	private AffineTransform transform;
	
	//Physics Information
	private float height;
	private float width;
		
	public CharacterEntity( String name, RigidBody coll,
			                Sprite standSprite, Sprite walkSprite, Sprite runSprite,
			                float height, float width )
	{
		super( name, coll );
		this.name = name;
		this.standSprite = standSprite;
		this.walkSprite = walkSprite;
		this.runSprite = runSprite;
		state = EntityState.STATIC;
		this.height = height;
		this.width = width;
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
