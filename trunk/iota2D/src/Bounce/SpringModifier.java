package Bounce;

import Util.Vector2;

public class SpringModifier extends Modifier
{
	private PhysicsObject obj1;
	private PhysicsObject obj2;
	private float k;
	private float length;
	
	public SpringModifier( PhysicsObject obj1, PhysicsObject obj2, float k, float length )
	{
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.k = k;
		this.length = length;
	}
	
	public void update()
	{
		if( isActive )
		{
			Vector2 force = obj1.cm.getNormal( obj2.cm ).mul( k * length - obj1.cm.getDistance( obj2.cm ) ).mul( .0001f );
			obj1.addForce( new Vector2( force.y, -force.x ));
			obj2.addForce( new Vector2( -force.y, force.x ));
		}
	}
}
