package Graphics;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


/**
 * A sheet of sprites that can be drawn individually
 * 
 * @author Kevin Glass
 */
public class SpriteSheet {
	private int tw;
	private int th;
	private int margin = 0;    
	private int spacing = 0;

	private Image[][] subImages;

	private BufferedImage target;

	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param image The image to based the sheet of
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 * @param spacing The spacing between tiles
	 * @param margin The magrin around the tiles
	 */
	
	public void SpriteSheet(BufferedImage image, int tw, int th, int spacing, int margin) {
		
		this.target = image;
		this.tw = tw;
		this.th = th;
		this.spacing = spacing;
		this.margin = margin; 

		initImpl();
	}

	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param image The image to based the sheet of
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 * @param spacing The spacing between tiles
	 */
	public SpriteSheet(String imageFile, int tw, int th, int spacing, int margin) {
		BufferedImage image = null;
		try {
		    image = ImageIO.read(new File(imageFile));
		} catch (IOException e) {
			throw new RuntimeException("Image file not found: " + imageFile);
		}
		
		SpriteSheet(image, tw, th, spacing, margin);
	}
	
	public SpriteSheet(String imageFile, int tw, int th, int spacing) {
		BufferedImage image = null;
		try {
		    image = ImageIO.read(new File(imageFile));
		} catch (IOException e) {
			throw new RuntimeException("Image file not found: " + imageFile);
		}
		
		SpriteSheet(image, tw, th, spacing, 0);
	}

	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param ref The location of the sprite sheet to load
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 */
	public SpriteSheet(String imageFile, int tw, int th) {
		BufferedImage image = null;
		try {
		    image = ImageIO.read(new File(imageFile));
		} catch (IOException e) {
			throw new RuntimeException("Image file not found: " + imageFile);
		}
		
		SpriteSheet(image, tw, th, 0, 0);
	}

	
	/**
	 * @see org.newdawn.slick.Image#initImpl()
	 */
	protected void initImpl() {
		if (subImages != null) {
			return;
		}
		
		int tilesAcross = ((target.getWidth()-(margin*2) - tw) / (tw + spacing)) + 1;
		int tilesDown = ((target.getHeight()-(margin*2) - th) / (th + spacing)); 
		if ((target.getHeight() - th) % (th+spacing) != 0) {
			tilesDown++;
		}
		
		//ge.getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(tw, th);
		
		subImages = new Image[tilesAcross][tilesDown];
		for (int x=0;x<tilesAcross;x++) {
			for (int y=0;y<tilesDown;y++) {
				subImages[x][y] = getSprite(x,y);
			    BufferedImage bi = new BufferedImage(tw, th, BufferedImage.TYPE_3BYTE_BGR);
			    bi.createGraphics().drawImage((BufferedImage) subImages[x][y], null, 0, 0);
			    subImages[x][y] = bi;
				
			}
		}
	}

	/**
	 * Get the sub image cached in this sprite sheet
	 * 
	 * @param x The x position in tiles of the image to get
	 * @param y The y position in tiles of the image to get
	 * @return The subimage at that location on the sheet
	 */
	public Image getSubImage(int x, int y) {
		//initImpl();
		
		if ((x < 0) || (x >= subImages.length)) {
			throw new RuntimeException("SubImage out of sheet bounds: "+x+","+y);
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			throw new RuntimeException("SubImage out of sheet bounds: "+x+","+y);
		}
		
		return subImages[x][y];
	}
	
	/**
	 * Get a sprite at a particular cell on the sprite sheet
	 * 
	 * @param x The x position of the cell on the sprite sheet
	 * @param y The y position of the cell on the sprite sheet
	 * @return The single image from the sprite sheet
	 */
	public Image getSprite(int x, int y) {
		//initImpl();

		if ((x < 0) || (x >= subImages.length)) {
			throw new RuntimeException("SubImage out of sheet bounds: "+x+","+y);
		}
		if ((y < 0) || (y >= subImages[0].length)) {
			throw new RuntimeException("SubImage out of sheet bounds: "+x+","+y);
		}

		return target.getSubimage(x*(tw+spacing) + margin, y*(th+spacing) + margin,tw,th); 
	}
	
	/**
	 * Get the number of sprites across the sheet
	 * 
	 * @return The number of sprites across the sheet
	 */
	public int getHorizontalCount() {
		initImpl();
		
		return subImages.length;
	}
	
	/**
	 * Get the number of sprites down the sheet
	 * 
	 * @return The number of sprite down the sheet
	 */
	public int getVerticalCount() {
		initImpl();
		
		return subImages[0].length;
	}
}
