package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import Bounce.RigidBody;
import Graphics.Sprite;
import Util.Vector2;

public class GameEntity extends Entity
{	
	private Sprite sprite;
	private AffineTransform transform;
    private float height;
	private float width;
	private float rot;
	
	public GameEntity( String name, RigidBody coll, Sprite sprite, float height, float width )
	{
		super( name, coll );
		this.sprite = sprite;
		this.width = width;
		this.height = height;
	}
	
	public void update( float elapsedTime )
	{		
		sprite.update( elapsedTime );
		rot += coll.getRotation() * elapsedTime;
	}
	
	public void draw( Graphics2D g )
	{
		transform = new AffineTransform();
		transform.translate( coll.getPos().x - width / 2, coll.getPos().y - height / 2 );
		transform.rotate( coll.getRotation(), width / 2, height / 2 );
		g.drawImage( sprite.getImage(), transform, null );
		
	}
}