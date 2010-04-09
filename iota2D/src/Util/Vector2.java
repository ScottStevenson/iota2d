package Util;

import java.awt.geom.Point2D;

public class Vector2 extends Point2D.Float
{
	
	public Vector2( float x, float y )
	{
		super( x, y );
	}
	
	public Vector2( Vector2 v )
	{
		super( v.x, v.y );
	}
	
	public Vector2()
	{
		super();
	}
	
	/**
	 * @modifies sets Vector2 to a new value
	 */
	public void set( Vector2 v )
	{
		this.x = v.x;
		this.y = v.y;
	}
	
	/**
	 * @modifies sets Vector2 to a new value
	 */
	public void set( float x, float y )
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @modifies normalizes the vector so its length is one
	 */
	public void normalize()
	{
		float mag = getLength();
		this.x /= mag;
		this.y /= mag;
	}
	
	/**
	 * 
	 * finds the dot product between two vectors
	 * @return the dot product
	 */
	public float dotProduct( Vector2 v )
	{
		return this.x * v.x + this.y * v.y;
	}
	
	/**
	 * gets the perpendicular vector to this vector
	 * @return the perpendicular vector
	 */
	public Vector2 getPerpendicular()
	{
		return new Vector2( -this.y, this.x );	
	}
	
	/**
	 * gets the normal between the first vector and the second vector
	 * @return the normal vector
	 */
	public Vector2 getNormal( Vector2 v )
	{
		return new Vector2( ( v.y - y ) / getDistance( v ), ( x -v.x ) / getDistance( v ) );
	}
	
	/**
	 * gets the normal between the vector and the origin
	 * @return the normal vector
	 */
	public Vector2 getNormal()
	{
		return new Vector2( y / getLength( ), x / getLength() );
	}
	
	private float getLength()
	{
		return (float) Math.sqrt( x * x + y * y );
	}
	
	/**
	 * gets the distance between the two vectors
	 * @return the distance between the two vectors
	 */
	public float getDistance( Vector2 v )
	{
		float dx = x - v.x;
		float dy = y - v.y;
		return (float) Math.sqrt( dx * dx + dy * dy );
	}
	
	/**
	 * takes the x and y components of one vector and raises it to the power of the second Vector
	 * @return the result of the operation
	 */
	public Vector2 getPower( Vector2 v )
	{
		return new Vector2( (float)Math.pow(x, v.x), (float)Math.pow(y, v.y));
	}
	
	/**
	 * adds two Vector2's
	 * @return the result of the addition
	 */
	public Vector2 add( Vector2 v )
	{
		return new Vector2( x + v.x, y + v.y );
	}
	/**
	 * subtracts two Vector2's
	 * @return the result of the subtraction
	 */
	public Vector2 sub( Vector2 v )
	{
		return new Vector2( this.x - v.x, this.y - v.y );
	}
	
	/**
	 * multiplies two Vector2's
	 * @return the result of the multiplication
	 */
	public Vector2 mul( Vector2 v )
	{
		return new Vector2( x * v.x, y * v.y );
	}
	
	/**
	 * multiplies both values of a Vector2 by a float
	 * @return the result of the multiplication
	 */
	public Vector2 mul( float f )
	{
		return new Vector2( x * f, y * f );
	}
	
	/**
	 * divides a Vector2 by another Vector2
	 * @return the result of the division
	 */
	public Vector2 div( Vector2 v )
	{
		return new Vector2( this.x / v.x, this.y / v.y );
	}
	
	/**
	 * divides both values of a Vector2 by a float
	 * @return the result of the division
	 */
	public Vector2 div( float f )
	{
		return new Vector2( this.x / f, this.y / f );
	}
	
	
}
