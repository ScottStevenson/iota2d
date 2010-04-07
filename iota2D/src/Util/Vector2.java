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
	
	public void set( float x, float y )
	{
		this.x = x;
		this.y = y;
	}
	
	public void normalize()
	{
		float mag = getLength();
		this.x /= mag;
		this.y /= mag;
	}
	
	public float dotProduct( Vector2 v )
	{
		return this.x * v.x + this.y * v.y;
	}
	
	public Vector2 getPerpendicular()
	{
		return new Vector2( -this.y, this.x );	
	}
	
	public Vector2 getNormal( Vector2 v )
	{
		return new Vector2( ( v.y - y ) / getDistance( v ), ( x -v.x ) / getDistance( v ) );
	}
	
	public Vector2 getNormal()
	{
		return new Vector2( y / getLength( ), x / getLength() );
	}
	
	public float getLength()
	{
		return (float) Math.sqrt( x * x + y * y );
	}
	
	public float getDistance( Vector2 v )
	{
		float dx = x - v.x;
		float dy = y - v.y;
		return (float) Math.sqrt( dx * dx + dy * dy );
	}
	
	public Vector2 getPower( Vector2 v )
	{
		return new Vector2( (float)Math.pow(x, v.x), (float)Math.pow(y, v.y));
	}
	//public Vector2 getNormal( Vector2 v )
	//{
	//	return new Vector2( x / getLength(), y / getLength() );
	//}
	
	public Vector2 add( Vector2 v )
	{
		return new Vector2( x + v.x, y + v.y );
	}
	
	public Vector2 sub( Vector2 v )
	{
		return new Vector2( this.x - v.x, this.y - v.y );
	}
	
	public Vector2 mul( Vector2 v )
	{
		return new Vector2( x * v.x, y * v.y );
	}
	
	public Vector2 mul( float f )
	{
		return new Vector2( x * f, y * f );
	}
	
	public Vector2 div( Vector2 v )
	{
		return new Vector2( this.x / v.x, this.y / v.y );
	}
	
	public Vector2 div( float f )
	{
		return new Vector2( this.x / f, this.y / f );
	}
	
	public void set( Vector2 v )
	{
		this.x = v.x;
		this.y = v.y;
	}
}
