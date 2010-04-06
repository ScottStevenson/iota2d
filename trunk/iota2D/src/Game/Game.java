package Game;

import Bounce.World;
import Data.Database;
import Entity.Entity;
import Entity.EntityFactory;
import Entity.ParticleEntity;
import Graphics.ScreenManager;
import Level.Level;
import Level.LevelFactory;
import Sound.MidiPlayer;
import Sound.Sound;
import Sound.SoundManager;
import Util.Vector2;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.sound.sampled.AudioFormat;

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
	
	protected boolean isLoadLevel = false;
	
	protected Level level;
	
	private World physics;
	
	public Database database;
	
	protected SoundManager soundManager;
	protected MidiPlayer midiPlayer;
	
	protected Sound coinSound;
	
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
			
			//Set up sound
			this.midiPlayer = new MidiPlayer();
			this.soundManager = new SoundManager( new AudioFormat( 44100.0f, 16, 2, true, false ));
			coinSound = soundManager.getSound( "content/sound/door2.wav" );
			
			levelFactory = new LevelFactory( this, physics, midiPlayer );
			entityFactory = new EntityFactory( physics, database );

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
			//level = levelFactory.buildLevel( firstLevel );
			loadLevel( firstLevel );
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
			if( System.nanoTime() / 1000000 - currTime > 14 )
			{
				elapsedTime = System.nanoTime() / 1000000 - currTime;
				currTime += elapsedTime;
				//System.out.println(elapsedTime);

				screen.update();
				
				state.update();
				
				Graphics2D g = screen.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				//g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				//g.clipRect(0, 0, 1280, 800);
				level.run( elapsedTime, g, screenWidth, screenHeight );
				
				physics.advanceSimulation( elapsedTime );				
			}
			if( isLoadLevel )
			{
				loadLevel( "lava" );
				isLoadLevel = false;
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
		float gx = Float.parseFloat( database.get( "levels." + ID, "gravityX" ));
		float gy = Float.parseFloat( database.get( "levels." + ID, "gravityY" ));
		physics.setGravity( new Vector2( gx, gy ));
		level = levelFactory.buildLevel( ID );
	}
	
	public static void main( String args[] )
	{
		new Game();
	}
}
