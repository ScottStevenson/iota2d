package Entity;

import Bounce.PhysicsObject;
import Util.Vector2;

public class AlienAI extends AI
{
	float time;
	
	public AlienAI( PhysicsObject coll )
	{
		super( coll );
		time = 0;
	}

	public void update( float elapsedTime )
	{
		time += elapsedTime;
		if( time < 1000 )
		{
			coll.addForce( new Vector2( -.01f, 0 ));
		}
		else
		{
			if( time > 2000 )
			{
				time = 0;
			}
			else
			{
				coll.addForce( new Vector2( .01f, 0 ));
			}
		}
	}

}
