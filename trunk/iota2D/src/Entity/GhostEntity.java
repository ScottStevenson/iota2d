package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Bounce.RigidBody;
import Graphics.Sprite;

public class GhostEntity extends Entity
{
	private Sprite sprite;
	private AffineTransform trans;
	private float width;
	private float height;
	
	public GhostEntity( String name, Sprite sprite, RigidBody coll, float width, float height )
	{
		super( name, coll );
		this.sprite = sprite;
		this.coll = coll;
		this.trans = new AffineTransform();
		this.width = width;
		this.height = height;
	}

	public void draw( Graphics2D g )
	{
		g.drawImage( sprite.getImage(), (int)coll.getPos().x,(int)coll.getPos().y, null );
	}

	public void update( float elapsedTime )
	{
		sprite.update( elapsedTime );
		trans.translate( coll.getPos().x - width / 2, coll.getPos().y - height / 2 );
	}

}
