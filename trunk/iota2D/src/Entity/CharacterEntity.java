package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Bounce.RigidBody;
import Graphics.Sprite;

public class CharacterEntity extends Entity
{	
	public EntityState state;
		
	public CharacterEntity( String name, RigidBody coll )
	{
		super( name, coll );		
	}

	@Override
	public void draw( Graphics2D g ) 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void update( float elapsedTime ) 
	{
		// TODO Auto-generated method stub	
	}
	
	
}
