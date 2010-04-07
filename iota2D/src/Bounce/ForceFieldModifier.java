package Bounce;

import java.util.LinkedList;

import Util.Vector2;

public class ForceFieldModifier extends Modifier
{
	private LinkedList<PhysicsObject> objects;
	private Vector2 force;

	public ForceFieldModifier( Vector2 force )
	{
		objects = new LinkedList<PhysicsObject>();
		this.force = force;
	}
	
	public void add( PhysicsObject obj )
	{
		objects.add( obj );
	}
	
	@Override
	public void update() 
	{		
		for( int i = 0; i < objects.size(); i++ )
		{
			objects.get( i ).f = objects.get( i ).f.add( force );
		}
	}
	

}
