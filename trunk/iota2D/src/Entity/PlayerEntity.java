package Entity;

import Bounce.RigidBody;
import Graphics.Sprite;
import Util.Vector2;

public class PlayerEntity extends CharacterEntity
{		
	public int HP = 1;
	public int MP = 1;
	public int AP = 1;
	
	public PlayerEntity( String name, RigidBody coll, 
            			 Sprite standSprite, Sprite walkSprite, Sprite runSprite,
            			 float height, float width,
            			 int HP, int MP, int AP )
	{
		super( name, coll, standSprite, walkSprite, runSprite, height, width );
		this.HP = HP;
		this.MP = MP;
		this.AP = AP;
	}
	
	public void walk( float direction )
	{
		state = EntityState.WALKING;
		coll.addForce( new Vector2( .1f * direction, 0 ));
	}
	
	public void run( float direction )
	{
		state = EntityState.RUNNING;
		coll.addForce( new Vector2( .2f * direction, 0 ));
	}
	
	public void jump()
	{
		state = EntityState.JUMPING;
		coll.setSpeed( new Vector2( 0, -1 ));
	}
	
	public void hit( int dam )
	{
		HP -= dam;
	}
	
	public void health( int health )
	{
		HP += health;
	}
}
