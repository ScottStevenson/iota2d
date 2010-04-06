package Bounce;

import Util.Vector2;

/**
 * 
 * A class to hold the attributes of a object used in a physical simulation
 *
 */
public abstract class PhysicsObject 
{		
	protected final String name;
	
	protected float mass;
	protected float inertia;
	protected float damp;
	protected float angDamp;
	protected float restitution;
		
	protected float radius;
	protected Vector2 cm;
	protected Vector2 cmold;
	protected Vector2 dcm;
	protected Vector2 dcmOld;
	protected Vector2 v;
	protected Vector2 vold;
	protected Vector2 f;
	protected float torque;
	protected float angVel;
	protected float angVelOld;
	protected float theta;
	protected float thetaold;
	
	protected boolean isStatic; // Immovable object
	protected boolean isGhost; // Not solid
	protected boolean isRotated; // Can the object rotate
	
	/**
	 *  
	 * @param name the objects name
	 * @param cm the objects center of mass
	 * @param mass the mass of the object
	 */
	public PhysicsObject( String name, Vector2 cm, float mass )
	{			
		this.name = name;
		
		this.mass = mass;
		this.inertia = 1;
		this.damp = .0001f;
		this.angDamp = .0001f;
		this.restitution = 1;		
		
		this.radius = 0;	
		this.cm = new Vector2( cm );
		this.cmold = new Vector2( cm );
		this.dcm = new Vector2( 0, 0 );
		this.dcmOld = new Vector2( 0, 0 );
		this.v = new Vector2( 0, 0 );
		this.vold = new Vector2( 0, 0 );
		this.f = new Vector2( 0, 0 );		
		this.torque = 0;
	    this.angVel = 0;
	    this.angVelOld = 0;
	    this.theta = 0;  
	    this.thetaold = 0;	    	    
		

		if( this.mass == 0 )
		{
			isStatic = true;
		}
		else
		{
			isStatic = false;
		}
		if( this.mass < 0 )
		{
			isGhost = true;
		}
		else
		{
			isGhost = false;
		}
		isRotated = false;
	}
	
	
	/**
	 *
	 * @return the objects name
	 */
	public String getName()
	{
		return new String( name );
	}
	
	/**
	 * 
	 * @return the objects position
	 */
	public Vector2 getPos()
	{
		return new Vector2( cm );
	}
	
	/**
	 * @modifies set the objects position
	 * 
	 * @param p the new position
	 */
	public void setPos( Vector2 p )
	{
		this.cm.set( p );
	}
	
	/**
	 * @modifies add a force to the object
	 * 
	 * @param f the force to add
	 */
	public void addForce( Vector2 f )
	{
		this.f.set( f );
	}
	
	/**
	 * @modifies sets the objects force
	 * 
	 * @param f the new force
	 */
	public void setForce( Vector2 f )
	{
		this.f.set( f );
	}
	
	/**
	 * @modifies sets the speed of the object
	 * 
	 * @param speed the new speed
	 */
	public void setSpeed( Vector2 speed )
	{
		this.v.set( speed );
	}
	
	/**
	 * 
	 * @return the objects orientation
	 */
	public float getRotation()
	{
		return theta;
	}
	
	/**
	 * @modifies sets the objects rotation
	 * 
	 * @param theta the new angle
	 */
	public void setRotation( float theta )
	{
		this.theta = theta; 
	}
	
	/**
	 * @modifies sets the objects coefficient of restitution
	 * 
	 * @param restitution the new coefficient of restitution
	 */
	public void setRestitution( float restitution )
	{
		this.restitution = restitution;
	}
	
	/**
	 * @modifies sets the objects dampening force
	 * 
	 * @param damp the new dampening force
	 */
	public void setDampening( float damp )
	{
		this.damp = damp;
	}
	
	/**
	 * @modifies sets the objects angular dampening force
	 * 
	 * @param angDamp the new angular dampening force
	 */
	public void setAngularDampening( float angDamp )
	{
		this.angDamp = angDamp;
	}
	
	/**
	 * @modifies sets the objects torque
	 * 
	 * @param torque the new torque
	 */
	public void setTorque( float torque )
	{
		this.torque = torque;
	}
	
	/**
	 * @modifies add torque to the object
	 * 
	 * @param torque the amount of torque to add
	 */
	public void addTorque( float torque )
	{
		this.torque += torque;
	}

	/**
	 * @modifies sets weather an object can be rotated
	 * 
	 * @param isRotated if true the object has angular affects
	 */
	public void setIsRoteted( Boolean isRotated )
	{
		this.isRotated = isRotated;
	}
}
