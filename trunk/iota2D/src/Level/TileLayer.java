package Level;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A layer of tiles on the map
 *
 * Code adapted from SlicK game library (http://slick.cokeandcode.com/)
 * Copyright (c) 2007, Slick 2D
 */

public class TileLayer {
	/** The code used to decode Base64 encoding */
	private static byte[] baseCodes = new byte[256];

	/**
	 * Static initialiser for the codes created against Base64
	 */
	static {
		for (int i = 0; i < 256; i++)
			baseCodes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++)
			baseCodes[i] = (byte) (i - 'A');
		for (int i = 'a'; i <= 'z'; i++)
			baseCodes[i] = (byte) (26 + i - 'a');
		for (int i = '0'; i <= '9'; i++)
			baseCodes[i] = (byte) (52 + i - '0');
		baseCodes['+'] = 62;
		baseCodes['/'] = 63;
	}
	
	/** The map this layer belongs to */
	private final TiledMap map;
	/** The index of this layer */
	public int index;
	/** The name of this layer - read from the XML */
	public String name;
	/** The tile data representing this data, index 0 = tileset, index 1 = tile id */
	public int[][][] data;
	public int width;
	public int height;
	
	public Properties props;
	
	/**
	 * Create a new layer based on the XML definition
	 * 
	 * @param element The XML element describing the layer
	 * @param map The map this layer is part of
	 * @throws Exception Indicates a failure to parse the XML layer
	 */
	public TileLayer(TiledMap map, Element element) throws Exception {
		this.map = map;
		name = element.getAttribute("name");
		width = Integer.parseInt(element.getAttribute("width"));
		height = Integer.parseInt(element.getAttribute("height"));
		data = new int[width][height][3];

		// now read the layer properties
		Element propsElement = (Element) element.getElementsByTagName("properties").item(0);
		if (propsElement != null) {
			NodeList properties = propsElement.getElementsByTagName("property");
			if (properties != null) {
				props = new Properties();
				for (int p = 0; p < properties.getLength();p++) {
					Element propElement = (Element) properties.item(p);
					
					String name = propElement.getAttribute("name");
					String value = propElement.getAttribute("value");		
					props.setProperty(name, value);
				}
			}
		}

		Element dataNode = (Element) element.getElementsByTagName("data").item(0);
		String encoding = dataNode.getAttribute("encoding");
		String compression = dataNode.getAttribute("compression");
		
		if (encoding.equals("base64") && compression.equals("gzip")) {
			try {
                Node cdata = dataNode.getFirstChild();
                char[] enc = cdata.getNodeValue().trim().toCharArray();
                byte[] dec = decodeBase64(enc);
                GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(dec));
                
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int tileId = 0;
                        tileId |= is.read();
                        tileId |= is.read() <<  8;
                        tileId |= is.read() << 16;
                        tileId |= is.read() << 24;

                        if (tileId == 0) {
	                        data[x][y][0] = -1;
	                        data[x][y][1] = 0;
	                        data[x][y][2] = 0;
                        } else {
	                        TileSet set = map.findTileSet(tileId);

	                        if (set != null) {
		                        data[x][y][0] = set.index;
		                        data[x][y][1] = tileId - set.firstGID;
	                        }
	                        data[x][y][2] = tileId;
                        }
                    }
                }
			} catch (IOException e) {
				throw new Exception("Unable to decode base 64 block");
			}
		} else {
			throw new Exception("Unsupport tiled map type: "+encoding+","+compression+" (only gzip base64 supported)");
		}
	}
	
	/**
	 * Get the gloal ID of the tile at the specified location in
	 * this layer
	 * 
	 * @param x The x coorindate of the tile
	 * @param y The y coorindate of the tile
	 * @return The global ID of the tile
	 */
	public int getTileID(int x, int y) {
		return data[x][y][2];
	}
	
	/**
	 * Set the global tile ID at a specified location
	 * 
	 * @param x The x location to set
	 * @param y The y location to set
	 * @param globalId The tile value to set
	 */
	public void setTileID(int x, int y, int globalId) {
        if (globalId == 0) {
            data[x][y][0] = -1;
            data[x][y][1] = 0;
            data[x][y][2] = 0;
        } else {
            TileSet set = map.findTileSet(globalId);
            
            data[x][y][0] = set.index;
            data[x][y][1] = globalId - set.firstGID;
            data[x][y][2] = globalId;
        }
	}
	
	/**
	 * Draw this layer with the specified {@link Graphics2D} object.
	 * @param g The Graphics2D object to draw the tiles with.
	 */
	public void draw( Graphics2D g )
	{
		for( int y=0; y<this.height; y++ )
		{
			for(int x=0; x<this.width; x++){
				Image img = map.getTileImage(x, y, this.index);
				if (img!=null){
					g.drawImage(img, x*map.tileWidth, y*map.tileHeight, null);
				}		
			}
		}
	}
	
	/**
	 * Draws a rectangular section of the TileD map with the specified Graphics2D object.
	 * 
	 * @param g The graphics object to draw to
	 * @param x The x position (top left) of the rectangular area to draw 
	 * @param y The y position (top left) of the rectangular area to draw
	 * @param width The width of the rectangular area to draw
	 * @param height The height of the rectangular area to draw
	 */
	public void draw( Graphics2D g, int xPos, int yPos, int width, int height )
	{
		int yLowerBound = yPos/map.tileHeight-2;
		int yUpperBound = (yPos+height)/map.tileHeight + 2;
		int xLowerBound = xPos/map.tileWidth - 2;
		int xUpperBound = (xPos+width)/map.tileWidth + 2;
		
		if (yLowerBound<0)
			yLowerBound = 0;
		if (xLowerBound<0)
			xLowerBound = 0;
		if (yUpperBound>map.height)
			yUpperBound = map.height;
		if (xUpperBound>map.width)
			xUpperBound = map.width;
		
		//System.out.print(xLowerBound +"/"+yLowerBound+"/"+xUpperBound+"/"+yUpperBound+"\n");
		
		for( int y=yLowerBound; y<yUpperBound; y++ )
		{
			for(int x=xLowerBound; x<xUpperBound; x++){
				Image img = map.getTileImage(x, y, this.index);
				if (img!=null){
					g.drawImage(img, x*map.tileWidth, y*map.tileHeight, null);
				}		
			}
		}
	}
	
	/**
	 * Decode a Base64 string as encoded by TilED
	 * 
	 * @param data The string of character to decode
	 * @return The byte array represented by character encoding
	 */
    private byte[] decodeBase64(char[] data) {
		int temp = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || baseCodes[data[ix]] < 0) {
				--temp; 
			}
		}

		int len = (temp / 4) * 3;
		if ((temp % 4) == 3)
			len += 2;
		if ((temp % 4) == 2)
			len += 1;

		byte[] out = new byte[len];

		int shift = 0;
		int accum = 0;
		int index = 0;

		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : baseCodes[data[ix]];

			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}

		if (index != out.length) {
			throw new RuntimeException(
					"Data length appears to be wrong (wrote " + index
							+ " should be " + out.length + ")");
		}

		return out;
	}
}