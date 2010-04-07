package Bounce;

import java.util.LinkedList;

import Util.Vector2;

/**
 * 
 * A class to hold a convex-hull mesh as an physics object
 *
 */
public class RigidBody extends PhysicsObject
{
	protected LinkedList<Vector2> verticies;
	protected LinkedList<Vector2> originalVerticies;
	//protected float[] vectors;
	//protected float[] angles;

	/**
	 * 
	 * @param name the objects name
	 * @param cm the objects center of mass
	 * @param mass the objects mass
	 */
	public RigidBody( String name, Vector2 cm, float mass ) 
	{
		super( name, cm, mass );
		this.verticies = new LinkedList<Vector2>();
		this.originalVerticies = new LinkedList<Vector2>();
		//this.vectors = new float[32];
		//this.angles = new float[32];
	}

	public void undoVertices()
	{
		for( int i = 0; i < this.verticies.size(); i++)
		{
			verticies.get( i ).set( originalVerticies.get( i ));
	    }
	}
	
	public void add( Vector2 p )
	{
		int i = verticies.size();
		if( i < 32 )
		{
			verticies.add( new Vector2( p ) );
			originalVerticies.add( new Vector2( p ));
			float length = p.getDistance( cm );
			if( length > radius )
			{
				radius = length;
			}
			//vectors[i] = length;  // Distance between the center of mass and vertex
			//angles[i] = (float)Math.atan2( p.x - cm.x, p.y - cm.y ); // the angle of the
			inertia = mass * radius * radius / 2;
		}
	}
	
	public void calcVertices()
	{
		for( int i = 0; i < verticies.size(); i++ )
		{			
			originalVerticies.get( i ).set( verticies.get( i ));
			if( isRotated )
			{
				//float x = this.origVerts.get( i ).x + (float) Math.cos( theta + this.angles[i] ) * this.vectors[i];
				//float y = this.origVerts.get( i ).y + (float) Math.sin( theta + this.angles[i] ) * this.vectors[i];
				//System.out.println( theta + "  " + x + ", " + y );
				//verticies.get( i ).set( verticies.get( i ).add( new Vector2( x, y ) )); 
			}
			
			//float hyp = vectors[ i ];
			//float angle = theta;// + angles[ i ];			
			//if( angle > 2 * Math.PI )
			//{
			///	angle -= 2 * Math.PI;
			//}
			//if( angle < 0 )
			//{
			//	angle += 2 * Math.PI;
			//}
			//float x1 = (float) Math.cos( angles[i] ) * hyp;
			//float y1 = (float) Math.sin( angles[i] ) * hyp;
			//float x2 = (float) Math.cos( theta ) * hyp;
			//float y2 = (float) Math.sin( theta ) * hyp;
			
			verticies.get( i ).set( verticies.get( i ).add( dcm )); 
			
		}
	}
}
