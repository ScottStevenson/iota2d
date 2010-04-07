package Bounce;

//Object to modify the properties of one or more PhysicsObjects
public abstract class Modifier 
{
	public boolean isActive = false;

	public abstract void update();
}
