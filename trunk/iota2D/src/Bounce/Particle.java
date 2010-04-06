package Bounce;

import Util.Vector2;

public class Particle extends PhysicsObject
{
	/**
	 * 
	 * @param name the particles name
	 * @param pos the position to create the particle
	 * @param mass the particles mass
	 */
	public Particle( String name, Vector2 pos, float mass )
	{
		super( name, pos, mass );
	}
}
