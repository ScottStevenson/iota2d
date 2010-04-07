package Entity;

import java.awt.Graphics2D;

import Bounce.PhysicsObject;

public abstract class Entity
{
	public EntityState state;
	public String name;
	public PhysicsObject coll;
	public boolean isAlive;
	
	public Entity( String name, PhysicsObject coll )
	{
		this.name = name;
		this.coll = coll;
		isAlive = true;
		state = EntityState.STATIC;
	}
	
	public void setState( EntityState state )
	{
		this.state = state;
	}
	
	public abstract void update( float elapsedTime );
	
	public abstract void draw( Graphics2D g );
}
