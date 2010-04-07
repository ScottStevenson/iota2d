package Game;

import Bounce.CollisionListener;
import Util.Vector2;

public class PhysicsListener extends CollisionListener
{
	private GameState state;
	
	public PhysicsListener( GameState state )
	{
		this.state = state;
	}

	public void collisionEvent( String obj1, String obj2, Vector2 normal )
	{
		state.collisionEvent( obj1, obj2, normal );
	}

}
