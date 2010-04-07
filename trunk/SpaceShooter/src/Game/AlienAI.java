package Game;

import java.util.Random;

import Bounce.PhysicsObject;
import Entity.AI;
import Util.Vector2;

public class AlienAI implements AI
{
	private Random rand;
	PhysicsObject coll;
	
	public AlienAI( PhysicsObject coll )
	{
		this.coll = coll;
		rand = new Random();
	}

	public void update( float elapsedTime )
	{
		if( coll != null )
		{
			this.coll.addForce( new Vector2( (float) (.005f * rand.nextGaussian()), (float) (.005f * rand.nextGaussian())));
		}
	}
}
