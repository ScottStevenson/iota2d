package Entity;

import Bounce.PhysicsObject;

public abstract class AI 
{
	PhysicsObject coll;
	
	public AI( PhysicsObject coll ) 
	{
		this.coll = coll;
	}
	
	public abstract void update( float elapsedTime );

}
