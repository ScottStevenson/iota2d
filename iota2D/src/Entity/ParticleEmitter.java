package Entity;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import Bounce.Particle;
import Bounce.World;
import Bounce.PhysicsObject;
import Data.Database;
import Graphics.Sprite;
import Util.Vector2;

// An entity capable of emiting particles
public class ParticleEmitter extends Entity
{
	private float timeCreated;
	private float life;
	
	private World physics;
	private Database database;
	private EntityFactory factory;
	
	private String name;
	private String particleType;
	private float randomAmount;
	private Vector2 v;
	private float spawnRate;
	private Random rand;
	
	private Sprite sprite;
	private float partLife;
		
	private LinkedList<String> removeParticles;
	
	private HashMap<String, ParticleEntity> particles;
	
	private float lastAdded;
	
	private int num = 0;
	
	/**
	 * 
	 * @param name name of emitter
	 * @param coll the physics object for the emitter
	 * @param life the length of time the particle exists
	 * @param type the type of emitter
	 * @param physics the physics simulation
	 * @param database
	 * @param randomAmount the randomness if the particles
	 * @param v the velocity of the particles
	 * @param spawnRate how fast the particles are created
	 */
	public ParticleEmitter( String name, PhysicsObject coll, Sprite sprite, float life,
			                String type, World physics, Database database, float randomAmount, 
			                Vector2 v, float spawnRate )
	{
		super( name, coll );		
		this.physics = physics;
		this.life = life;
		this.timeCreated = System.nanoTime() / 1000000;
		this.name = name;
		this.randomAmount = randomAmount;
		this.v = v;
		this.spawnRate = spawnRate;
		this.lastAdded = System.nanoTime() / 1000000;
		this.database = database;
		this.factory = new EntityFactory( physics, database );	
		this.rand = new Random();
		this.particles = new HashMap<String, ParticleEntity>();
		this.particleType = database.get( "entities.particleEmitters." + type, "particleType" );
		this.removeParticles = new LinkedList<String>();
		this.sprite = sprite;
		this.partLife = Float.parseFloat( database.get( "entities.particles." + particleType, "life" ));
	}
	
	private void add()
	{
		Particle part = new Particle( name, coll.getPos(), 1 );		
		physics.add( part );
		ParticleEntity p = new ParticleEntity( name + num , sprite, part, coll.getPos(), partLife );
		Vector2 randomVel = new Vector2( (float)( randomAmount * rand.nextGaussian() ), (float)( randomAmount * rand.nextGaussian() ));
		p.setVelocity( v.add( randomVel ));
		particles.put( p.name, p );
		num++;
	}
	
	private void remove( String name )
	{
		if( particles.get( name ) != null )
		{
			physics.remove( particles.get( name ).coll );
			particles.remove( name );
		}
	}
	
	public void update( float elapsedTime )
	{
		float time = System.nanoTime() / 1000000;
		if( time - lastAdded > spawnRate )
		{
			add();
			lastAdded = time;
		}
		Iterator<String> entityIter = particles.keySet().iterator();
		while( entityIter.hasNext() )
		{
			Entity entity = particles.get( entityIter.next() );
			entity.update( elapsedTime );
			if( !entity.isAlive )
			{
				removeParticles.add( entity.name );
			}
		}
		
		for( int i = 0; i < removeParticles.size(); i++ )
		{
			remove( removeParticles.get( i ));
		}
		removeParticles.clear();
		
		if( System.nanoTime() / 1000000 - timeCreated > life )
		{
			Iterator<String> iter = particles.keySet().iterator();
			while( iter.hasNext() )
			{
				physics.remove( particles.get( iter.next() ).coll );				
			}
			this.isAlive = false;
		}
		
	}

	@Override
	public void draw( Graphics2D g )  
	{
		Iterator<String> entityIter = particles.keySet().iterator();
		while( entityIter.hasNext() )
		{
			Entity entity = particles.get( entityIter.next() );
			entity.draw( g );
		}	
	}	
}
