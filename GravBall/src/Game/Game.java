package Game;

import Data.Database;
import Entity.Entity;
import Entity.EntityFactory;
import Entity.ParticleEntity;
import Entity.PlayerEntity;
import Graphics.ScreenManager;
import Graphics.Sprite;
import Level.Level;
import Level.LevelFactory;
import Bounce.Particle;
import Bounce.World;
import Sound.MidiPlayer;
import Sound.Sound;
import Sound.SoundManager;
import Util.Vector2;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioFormat;
import javax.swing.ImageIcon;

//import sun.tools.tree.ThisExpression;

public class Game
{
	//Screen Information
	private int screenWidth;
	private int screenHeight;
	private int colorDepth;
	
	private LevelFactory levelFactory;
	protected EntityFactory entityFactory;
	
	private ScreenManager screen;
	
	private GameState state;
	private Keyboard keyboard;
	private PhysicsListener physicsListener;
	private MouseMotion mouseMotion;
	private MouseClick mouseClick;
	
	private PlayerEntity player;
	
	protected boolean isLoadNextLevel = false;
	
	protected Level level;
	
	public World physics;
	
	public Database database;
	
	public SoundManager soundManager;
	
	//Sounds
	protected Sound shatterSound;
	public Sound deathSound;
	public Sound beepSound;
	
	protected MidiPlayer midiPlayer;
	
	
	//Particle ids
	int pNum;
	
	Sprite redParticle;
	Sprite greenParticle;
	String nextLevel;
	String curLevel;
	
	// create a new game
	public Game()
	{
		try
		{		
			//Load database
			database = new Database( "content/config/database.xml" );
			
			//The game logic state
			state = new GameState( this );
			
			//Create the physics world
			physics = new World();
			//Gets collision events from physics
			physicsListener = new PhysicsListener( state );
			physics.addListener( physicsListener );
			float gX = Float.parseFloat( database.get("config", "gravityX" ));
			float gY = Float.parseFloat( database.get("config", "gravityY" ));
			physics.setGravity( new Vector2(gX, gY));
			
			//Set up sound
			this.midiPlayer = new MidiPlayer();
			this.soundManager = new SoundManager( new AudioFormat( 44100.0f, 16, 2, true, false ));
			shatterSound = soundManager.getSound( "content/sound/shatter.wav" );
			deathSound = soundManager.getSound( "content/sound/death.wav");
			beepSound = soundManager.getSound("content/sound/beep.wav");
			Sound music = soundManager.getSound("content/sound/music3.wav");
			soundManager.play(music, true);
			
			//Create level and entity factories
			entityFactory = new EntityFactory( physics, database );
			levelFactory = new LevelFactory( database, physics, midiPlayer, entityFactory);
			
			//Create player
			try {
				this.player = entityFactory.buildPlayer("ball");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Set up the screen
			screenWidth = Integer.parseInt(database.get("config.screen", "width"));
			screenHeight = Integer.parseInt(database.get("config.screen", "height"));
			colorDepth = Integer.parseInt(database.get("config.screen", "colorDepth"));
			DisplayMode displayMode = new DisplayMode( screenWidth, screenHeight, colorDepth, DisplayMode.REFRESH_RATE_UNKNOWN );
			screen = new ScreenManager( screenWidth, screenHeight );
			screen.setFullScreen( displayMode );
			
			//Set up the inputs
			keyboard = new Keyboard( screen, state );
			mouseMotion = new MouseMotion( screen, state );
			mouseClick = new MouseClick( screen, state );
			
			//Create game level
			String firstLevel = database.get( "config.", "first_level" );
			level = levelFactory.buildLevel( firstLevel, this.player );
			curLevel = firstLevel;
			nextLevel = database.get( "levels." + curLevel + ".map", "nextLevel" );
			
			//Load sprites
			redParticle = new Sprite();
			redParticle.addFrame((BufferedImage) new ImageIcon("content/sprites/redparticle.png").getImage(), 0);
			greenParticle = new Sprite();
			greenParticle.addFrame((BufferedImage) new ImageIcon("content/sprites/greenparticle.png").getImage(), 0);
			
			//Run the game
			run();
		}
		finally
		{
			screen.closeScrene();
		}
	}
	
	private void run()
	{
		long currTime = System.nanoTime() / 1000000;
		long elapsedTime = 0;
		
		while( !state.exit )
		{			
			if( System.nanoTime() / 1000000 - currTime > 40 )
			{
				elapsedTime = System.nanoTime() / 1000000 - currTime;
				currTime += elapsedTime;

				screen.update();
				
				state.update();
				
				//Particle trail
				Particle p = new Particle("p"+pNum, level.getPlayer().coll.getPos(), 1);
				p.setSpeed(new Vector2((float)1, (float)0));
				physics.add(p);
				ParticleEntity pEnt = new ParticleEntity("p"+pNum, greenParticle, p, level.getPlayer().coll.getPos(), 2000);
				level.addEntity(pEnt);
				pNum += (pNum+1)%2500;//Maximum particle id of 2500
				
				//Get graphics object
				Graphics2D g = screen.getGraphics();
				//Use simple interpolation to improve performance
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				
				level.run( elapsedTime, g, screenWidth, screenHeight );
				
				physics.advanceSimulation( elapsedTime );				
			}
			if( isLoadNextLevel )
			{
				loadLevel( nextLevel );
				isLoadNextLevel = false;
			}
		}
		//Clean-up
		this.midiPlayer.close();
		this.soundManager.close();
		System.exit(0);		
	}
	
	protected void loadLevel( String ID )
	{
		physics.clear();
		midiPlayer.stop();
		
		level = levelFactory.buildLevel( ID, this.player );
		
		curLevel = ID;
		nextLevel = database.get( "levels." + ID + ".map", "nextLevel" );
	}
	
	public static void main( String args[] )
	{
		new Game();
	}
}
