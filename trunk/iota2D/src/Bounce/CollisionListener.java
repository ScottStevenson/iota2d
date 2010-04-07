package Bounce;

import Util.Vector2;

// Can be extended to report collisions outside Physics package
public abstract class CollisionListener
{
	/**
	 * 
	 * @param obj1 the name of the first object that collided
	 * @param obj2 the name of the second object that collided
	 * @param normal the normal vector of the collision
	 */
	public abstract void collisionEvent( String obj1, String obj2, Vector2 normal );
}
