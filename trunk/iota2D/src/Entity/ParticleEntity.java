package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import Bounce.Particle;
import Graphics.Sprite;
import Util.Vector2;

public class ParticleEntity extends Entity
{
	private long life;
	private long timeCreated;
	private Sprite sprite;
	private Vector2 pos;
	
	public static final float[] BLUR3x3 = {
        0.005f, 0.001f, 0.005f,    // low-pass filter kernel
        0.05f, 0.3f, 0.05f,
        0.5f, 0.5f, 0.5f
    };
	
	ConvolveOp cop = new ConvolveOp( new Kernel( 3, 3, BLUR3x3 ), ConvolveOp.EDGE_NO_OP, null );
	
	public ParticleEntity( String name, Sprite sprite, Particle coll, Vector2 pos, long life )
	{	
		super( name, coll );
		this.life = life;
		this.timeCreated = System.nanoTime() / 1000000;
		this.pos = new Vector2( pos );
		this.sprite = sprite;
	}
	
	public void setVelocity( Vector2 v )
	{
		coll.setSpeed( v );
	}

	@Override
	public void draw( Graphics2D g ) 
	{
	//	g.drawImage( sprite.getImage(), cop, (int)pos.x, (int)pos.y );
		g.drawImage( sprite.getImage(), (int)pos.x, (int)pos.y, null );
	}

	@Override
	public void update( float elapsedTime ) 
	{	
		if( life < System.nanoTime() / 1000000 - timeCreated )
		{
			this.isAlive = false;
		}
		else
		{
			this.pos.set( coll.getPos() );
		}
	}
}
