package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Bounce.RigidBody;
import Graphics.Sprite;

public class Enemy extends Entity
{
	private Sprite staticSprite;
	private Sprite walkSprite;
	private Sprite runSprite;
	private Sprite attackSprite;
	private AffineTransform trans;
	private float height;
	private float width;
	private AI ai = null;
	public int HP;
	
	public Enemy( String name, RigidBody coll, Sprite staticSprite, float height, float width, int HP )
	{
		super( name, coll );
	    this.height = height;
	    this.width = width;
	    this.staticSprite = staticSprite;
	    this.HP = HP;
	    this.ai = ai;
	}

	public void update( float elapsedTime )
	{
		staticSprite.update( elapsedTime );	
		if( ai != null )
		{
			ai.update( elapsedTime );
		}
	}
	
	public void draw( Graphics2D g )
	{
		trans = new AffineTransform();
		trans.translate( coll.getPos().x - width / 2, coll.getPos().y - height / 2 );

		switch( state )
		{
		case  STATIC: g.drawImage( staticSprite.getImage(), trans, null ); break;
		case  WALKING: g.drawImage( walkSprite.getImage(), trans, null ); break;
		case  RUNNING: g.drawImage( runSprite.getImage(), trans, null ); break;	
		case  ATTACKING: g.drawImage( attackSprite.getImage(), trans, null ); break;
		}
	}	
	
	public void addAI( AI ai )
	{
		this.ai = ai;
	}
	
	public void hit( int dam )
	{
		HP -= dam;
	}
}
