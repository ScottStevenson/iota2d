package Level;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.LinkedList;

import Util.Vector2;

//A class to hold and draw a series of images
public class ImageLayer implements Layer
{
	private LinkedList<Image> images;// list if images
	private String name;// the layer name
	private Vector2 offset;// Where to draw the images
	
	public ImageLayer( String name )
	{
		this.name = name;
		images = new LinkedList<Image>();
	}
	
	// draws all images in the layer
	public void draw( Graphics2D g )
	{
		for( int i = 0; i < images.size(); i++ )
		{
			g.drawImage( images.get(i), (int)offset.x, (int)offset.y, null );
		}
	}
	
	// adds a image to the layer
	public void addImage( Image image )
	{
		images.add( image );
	}
	
	// sets the offset for the layer
	public void setOffset( Vector2 v )
	{
		this.offset = v;
	}
}
