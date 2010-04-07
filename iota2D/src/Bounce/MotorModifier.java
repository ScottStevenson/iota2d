package Bounce;

import Util.Vector2;

public class MotorModifier extends Modifier
{

	public float torque;
	public Vector2 offset;
	PhysicsObject obj;
	
	public MotorModifier( PhysicsObject obj, float torque, Vector2 offset )
	{
		this.obj = obj;
		this.torque = torque;
		this.offset = offset;
	}
	
	public void update() 
	{
		 obj.torque = torque;
	}
	
	public void setTorque( float torque )
	{
		
	}

}
