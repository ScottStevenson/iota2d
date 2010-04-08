package Bounce;

import java.util.LinkedList;
import java.util.List;

import Util.Vector2;

// scale 100 unit = 1 meter
// time in milliseconds
public class World
{
	private List<RigidBody> physObjs;
	private List<RigidBody> staticPhysObjs;
	private List<Particle> particles;
	private List<Modifier> modifers;
    private CollisionState state;
	private CollisionListener listener;
	private Vector2 g;
	private float maxTimeStep;
	
	/**
	 * 
	 */
	public World()
	{
		physObjs = new LinkedList<RigidBody>();
		staticPhysObjs = new LinkedList<RigidBody>();
		particles = new LinkedList<Particle>();
		modifers = new LinkedList<Modifier>();
		state = CollisionState.CLEAR;
		listener = null;
		g = new Vector2();
		maxTimeStep = 16;
	}
	
	/**
	 * @modifies advances the simulation by a time step
	 * 
	 * @param elapsedTime the time to advance the simulation
	 */
	public void advanceSimulation( float elapsedTime )
	{
		if( elapsedTime > maxTimeStep )
		{
			elapsedTime = maxTimeStep;
		}
		
		float time = 0;
		float targetTime = elapsedTime;
		while( time < elapsedTime && ( targetTime - time ) > .0001 )
		{	
			integrate( targetTime - time );
			
			calculateVertices();
			
			checkCollisions();
		
			if( state == CollisionState.PENETRATING ) // if a penetration is detected the simulation steps back
			{
				stepBack();
				targetTime = ( time + targetTime ) / 2;	
			}
			else
			{
				time = targetTime;
				targetTime = elapsedTime;
				setIntegrate();
			}				
		}
	}
	
	private void integrate( float time )
	{
		for( int i = 0; i < modifers.size(); i++ )
		{
			modifers.get( i ).update();
		}
		
		RigidBody obj;
		for( int i = 0; i < physObjs.size(); i++ )
		{
			obj = physObjs.get( i );
			if( !obj.isGhost )
			{
				obj.f = obj.f.add( g.mul( obj.mass ));
			}
			obj.dcm.set( obj.v.mul( time ));
			obj.cm.set( obj.cm.add( obj.dcm ));
			obj.v.set( obj.v.add( obj.f.mul( time ).div( obj.mass )));
				
			if( obj.isRotated )
			{
				obj.angVel += obj.torque * time / obj.inertia;
				obj.theta += obj.angVel * time;
				obj.angVel += obj.torque * time / obj.inertia;
				obj.torque = obj.angVel * time * -obj.angDamp * obj.inertia;
			}
			obj.f.set( obj.v.mul( time * -obj.damp * obj.mass ));
		}
		
		Particle part;
		for( int p = 0; p < particles.size(); p++ )
		{			
			part = particles.get( p );
			if( !part.isGhost )
			{
				part.f = part.f.add( g.mul( part.mass ));
			}			
			part.cm.set( part.cm.add( part.v.mul( time )));
		}
	}
	
	private void stepBack()
	{
		RigidBody obj;
		for( int i = 0; i < physObjs.size(); i++ )
		{
			obj = physObjs.get( i );
			obj.cm.set( obj.cmold );
			obj.v.set( obj.vold );
			obj.theta = obj.thetaold;
			for( int j = 0; j < obj.verticies.size() && j < obj.originalVerticies.size(); j++ )
			{
				obj.verticies.get(j).set( obj.originalVerticies.get( j ));
			}
		}		
	}
	
	private void setIntegrate()
	{
		RigidBody obj;
		for( int i = 0; i < physObjs.size(); i++ )
		{
			obj = physObjs.get( i );
			obj.cmold.set( obj.cm );
			obj.vold.set( obj.v );
			obj.thetaold = obj.theta;
			for( int j = 0; j < obj.verticies.size(); j++ )
			{
				obj.originalVerticies.get( j ).set( obj.verticies.get( j ));
			}
		}
	}
	
	private void calculateVertices()
	{
		for( int i = 0; i < physObjs.size(); i++ )
		{
			physObjs.get( i ).calcVertices();		
		}		
	}
	
	// order n^2
	private void checkCollisions()
	{
		RigidBody obj = null;
		state = CollisionState.CLEAR;
		
		// Check for collisions between all objects
		// If penetrating stop and step back
		for( int i = 0; i < physObjs.size() && state != CollisionState.PENETRATING; i++ )
		{
			state = CollisionState.CLEAR;
			obj = physObjs.get( i );
		
			for( int k = 0; k < staticPhysObjs.size() && state != CollisionState.PENETRATING; k++ )
			{		
				Collision( staticPhysObjs.get( k ), obj );
			}	
			for( int j = i + 1; j < physObjs.size() && state != CollisionState.PENETRATING; j++ )
			{		
				state = CollisionState.CLEAR;
				Collision( obj, physObjs.get( j ));
			}
		}	
		for( int p = 0; p < particles.size(); p++ )
		{
			for( int k = 0; k < physObjs.size() && state != CollisionState.PENETRATING; k++ )
			{
				triangleParticleCollision( physObjs.get( k ), particles.get( p ));
			}				
		}
	}
	
	private void triangleParticleCollision( RigidBody obj, Particle part )
	{
		if( obj.cm.getDistance( part.cm ) > ( obj.radius + 5 ))
		{
		}
		else
		{
			if( state == CollisionState.CLEAR )
			{
				Vector2 A;
				Vector2 B;
				Vector2 C; 
				Vector2 P;
			
				Vector2 v0;
				Vector2 v1;
				Vector2 v2;
				
				float dot00;
				float dot01;
				float dot02;
				float dot11;
				float dot12;
				
				float u;
				float v;
								
				for( int i = 0; i < obj.verticies.size(); i++ )
				{		
					C = new Vector2( obj.cm );
					A = new Vector2( obj.verticies.get( i ));
					if( i == 0 )
					{
						B = obj.verticies.getLast();
					}
					else
					{
						B = obj.verticies.get( i - 1 );
					}
					v0 = C.sub( A );
					v1 = B.sub( A );
					P = new Vector2( part.cm );
					v2 = P.sub( A );
						
				    dot00 = v0.dotProduct( v0 );
					dot01 = v0.dotProduct( v1 );
					dot02 = v0.dotProduct( v2 );
					dot11 = v1.dotProduct( v1 );
					dot12 = v1.dotProduct( v2 );
					
					// u is the distance from the line formed by A and B
					u = ( dot11 * dot02 - dot01 * dot12 ) / ( dot00 * dot11 - dot01 * dot01 );
					v = ( dot00 * dot12 - dot01 * dot02 ) / ( dot00 * dot11 - dot01 * dot01 );
					if( u > 0 && v > 0 && ( u + v ) < 1 )
					{
						if( !listener.equals( null ) )
						{
							listener.collisionEvent( part.name, obj.name, A.sub( C ).getNormal( B.sub( C )));
						}
					}
				}
			}
		}
	}
	
	private void Collision( RigidBody obj1, RigidBody obj2 )
	{
		if( obj1.cm.getDistance( obj2.cm ) > ( obj1.radius + obj2.radius ))
		{
			// Broad-Phase collision
		}
		else
		{
			// Check for vertex / vertex collision
			for( int i = 0; i < obj1.verticies.size(); i++ )
			{
				for( int j = 0; j < obj2.verticies.size(); j++ )
				{
					Vector2 dist = obj1.verticies.get( i ).sub( obj2.verticies.get( j ));
					if( Math.abs( dist.x ) < .001 && Math.abs( dist.y ) < .001 )
					{
						Vector2 normal = obj1.cm.getNormal( obj2.cm );
						if( !listener.equals( null ) )
						{
							listener.collisionEvent( obj1.name, obj2.name, normal );
						}		
						if( !obj1.isGhost && !obj2.isGhost )
						{
							state = CollisionState.COLLIDING;
							this.resolveCollisions( obj1, obj2, normal, obj1.verticies.get(i) );
						}						
					}
				}
			}
			if( state == CollisionState.CLEAR )
			{
				Vector2 A;
				Vector2 B;
				Vector2 C; 
				Vector2 P;
			
				Vector2 v0;
				Vector2 v1;
				Vector2 v2;
				
				float dot00;
				float dot01;
				float dot02;
				float dot11;
				float dot12;
				
				float u;
				float v;
								
				for( int i = 0; i < obj1.verticies.size() && state == CollisionState.CLEAR; i++ )
				{		
					C = new Vector2( obj1.cm );
					A = new Vector2( obj1.verticies.get( i ));
					if( i == 0 )
					{
						B = obj1.verticies.getLast();
					}
					else
					{
						B = obj1.verticies.get( i - 1 );
					}
					v0 = C.sub( A );
					v1 = B.sub( A );
					for( int j = 0; j < obj2.verticies.size() && state == CollisionState.CLEAR; j++ )
					{
						P = new Vector2( obj2.verticies.get( j ));
						v2 = P.sub( A );
						
						dot00 = v0.dotProduct( v0 );
						dot01 = v0.dotProduct( v1 );
						dot02 = v0.dotProduct( v2 );
						dot11 = v1.dotProduct( v1 );
						dot12 = v1.dotProduct( v2 );
					
						// u is the distance from the line formed by A and B
						u = ( dot11 * dot02 - dot01 * dot12 ) / ( dot00 * dot11 - dot01 * dot01 );
						v = ( dot00 * dot12 - dot01 * dot02 ) / ( dot00 * dot11 - dot01 * dot01 );
						if( u > 0 && v > 0 && ( u + v ) < 1 )
						{
							Vector2 normal = A.sub( C ).getNormal( B.sub( C ));
							if( !listener.equals( null ) )
							{
								listener.collisionEvent( obj1.name, obj2.name, normal );
							}
							
							if( !obj1.isGhost && !obj2.isGhost )
							{
								if( u < .00005 )
								{
									state = CollisionState.COLLIDING;
									this.resolveCollisions( obj1, obj2, normal, new Vector2( P ));
								}
								else
								{
									state = CollisionState.PENETRATING;
								}
							}
						}
					}
					
					for( int k = 0; k < obj2.verticies.size() && state == CollisionState.CLEAR; k++ )
					{	
						C = new Vector2( obj2.cm );
						A = new Vector2( obj2.verticies.get( k ));
						if( k == 0 )
						{
							B = obj2.verticies.getLast();
						}
						else
						{
							B = obj2.verticies.get( k - 1 );
						}
						v0 = C.sub( A );
						v1 = B.sub( A );
						for( int j = 0; j < obj1.verticies.size() && state == CollisionState.CLEAR; j++ )
						{
							P = new Vector2( obj1.verticies.get( j ));
							v2 = P.sub( A );
							
							dot00 = v0.dotProduct( v0 );
							dot01 = v0.dotProduct( v1 );
							dot02 = v0.dotProduct( v2 );
							dot11 = v1.dotProduct( v1 );
							dot12 = v1.dotProduct( v2 );
						
							u = ( dot11 * dot02 - dot01 * dot12 ) / ( dot00 * dot11 - dot01 * dot01 );
							v = ( dot00 * dot12 - dot01 * dot02 ) / ( dot00 * dot11 - dot01 * dot01 );
							
							if( u > 0 && v > 0 && ( u + v ) < 1 )
							{
								Vector2 normal = A.sub( C ).getNormal( B.sub( C ));
								
								if( !listener.equals( null ) )
								{
									listener.collisionEvent( obj1.name, obj2.name, normal );
								}
								
								if( !obj1.isGhost && !obj2.isGhost )
								{								
									if( u < .00005 )
									{
										state = CollisionState.COLLIDING;
										this.resolveCollisions( obj2, obj1, normal, new Vector2( P ));
									}
									else
									{
										state = CollisionState.PENETRATING;
									}
								}
							}
						}
					}
				}				
			}
		}
	}
	
	
	private void resolveCollisions( RigidBody collBody1, RigidBody collBody2, Vector2 collisionNormal, Vector2 collisionPoint )
	{
		float restitution = collBody1.restitution * collBody2.restitution;
		
		Vector2 cm2CornerPrep1 = collisionPoint.sub( collBody1.cm ).getPerpendicular();
		Vector2 cm2CornerPrep2 = collisionPoint.sub( collBody2.cm ).getPerpendicular();
		Vector2 v1 = collBody1.v;//.add( cm2CornerPrep1.mul( collBody1.angVel ));
		Vector2 v2 = collBody2.v;//.add( cm2CornerPrep2.mul( collBody2.angVel ));
		if( collBody1 != null && collBody2 != null )
		{
			if( collBody1.isStatic )
			{				
				float jNum = -( 1 + restitution ) * v2.dotProduct( collisionNormal );
				float prep2 = cm2CornerPrep2.dotProduct( collisionNormal ) / collBody2.inertia;
				float jDen = collisionNormal.dotProduct( collisionNormal ) * ( 1 / collBody2.mass ) + prep2 * prep2;
				float impulse = jNum / jDen;
				collBody2.v = collBody2.v.add( collisionNormal.mul( impulse / collBody2.mass )); 
				if( collBody2.isRotated )
				{
					collBody2.angVel += collisionNormal.mul( impulse ).dotProduct( cm2CornerPrep2 ) / collBody2.inertia;
				}
			}
			else
			{
				if( collBody2.isStatic )
				{
					float jNum = -( 1 + restitution ) * v1.dotProduct( collisionNormal );
					float prep1 = cm2CornerPrep1.dotProduct( collisionNormal ) / collBody1.inertia;
					float jDen = collisionNormal.dotProduct( collisionNormal ) * ( 1 / collBody1.mass ) + prep1 * prep1;
					float impulse = jNum / jDen;
					collBody1.v = collBody1.v.add( collisionNormal.mul( impulse / collBody1.mass ));
					if( collBody1.isRotated )
					{
						collBody1.angVel += collisionNormal.mul( impulse ).dotProduct( cm2CornerPrep1 ) / collBody1.inertia;
					}
				}
				else
				{
					Vector2 relVel = v1.sub( v2 );		
		
					float jNum = -( 1 + restitution ) * relVel.dotProduct( collisionNormal );
					float prep1 = cm2CornerPrep1.dotProduct( collisionNormal ) / collBody1.inertia;
					float prep2 = cm2CornerPrep2.dotProduct( collisionNormal ) / collBody2.inertia;
					float jDen = collisionNormal.dotProduct( collisionNormal ) * ( 1 / collBody2.mass + 1 / collBody1.mass ) + prep2 * prep2 + prep1 * prep1;
					float impulse = jNum / jDen;	
					
					collBody1.v = collBody1.v.add( collisionNormal.mul( impulse / collBody1.mass )); 
					collBody2.v = collBody2.v.add( collisionNormal.mul( -impulse / collBody2.mass ));
					if( collBody1.isRotated )
					{
						collBody1.angVel += collisionNormal.mul( impulse ).dotProduct( cm2CornerPrep1 ) / collBody1.inertia;
					}
					if( collBody2.isRotated )
					{
						collBody2.angVel += collisionNormal.mul( impulse ).dotProduct( cm2CornerPrep2 ) / collBody2.inertia;
					}
				}
			}
		}
	}
	
	/**
	 * @modifies sets the gravity in the world
	 * 
	 * @param g the force of gravity
	 */
	public void setGravity( Vector2 g )
	{
		this.g = g;
	}
	
	/**
	 * @modifies sets the max time step
	 * 
	 * @param maxTimeStep the maximum amount of time 
	 *        that the simulation can be advanced in one time step
	 */
	public void setMaxTimeStep( float maxTimeStep )
	{
		this.maxTimeStep = maxTimeStep;
	}

	/**
	 * @modifies adds a CollisionListener to the world
	 * 
	 * @param listener the new CollisionListener
	 */
	public void addListener( CollisionListener listener )
	{
		this.listener = listener;
	}

	/**
	 * @modifies adds a Rigid Body to the world
	 * 
	 * @param obj the Rigid body to add
	 */
	public void add( RigidBody obj )
	{
		if( obj.isStatic == true )
		{
			staticPhysObjs.add( obj );
		}
		else
		{			
			physObjs.add( obj );
		}
	}		
	
	/**
	 * @modifies adds a particle to the world
	 * 
	 * @param part the particle to add
	 */
	public void add( Particle part )
	{
		particles.add( part );
	}
	
	/**
	 * @modifies adds a modifier to the world
	 * 
	 * @param mod the modifier to add
	 */
	public void add( Modifier mod )
	{
		modifers.add( mod );
	}
	
	/**
	 * @modifies removes an object from the simulation
	 * 
	 * @param obj 
	 */
	public void remove( PhysicsObject obj )
	{
		if( obj.isStatic )
		{
			staticPhysObjs.remove( obj );
		}
		else
		{
			physObjs.remove( obj );
		}
		
		particles.remove( obj );
	}
	
	/**
	 * @modifies removes all Physics Objects and Modifiers from the World
	 */
	public void clear()
	{
		physObjs = new LinkedList<RigidBody>();
		staticPhysObjs = new LinkedList<RigidBody>();
		particles = new LinkedList<Particle>();
		modifers = new LinkedList<Modifier>();
		state = CollisionState.CLEAR;
	}

}



