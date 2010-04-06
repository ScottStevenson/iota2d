package Level;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A class for parsing Tiled Maps (http://mapeditor.org/)
 * Code adapted from SlicK game library (http://slick.cokeandcode.com/)
 * Copyright (c) 2007, Slick 2D
 */
public class TiledMap {

	protected int width;
	protected int height;
	protected int tileWidth;
	protected int tileHeight;
	
	/** The location prefix where we can find tileset images */
	protected String tilesLocation;
	
	/** properties of the map */
	protected Properties props;
	
	protected ArrayList tileSets = new ArrayList();
	protected ArrayList layers = new ArrayList();
    protected ArrayList objectGroups = new ArrayList();

	/** True if we want to load tilesets - including their image data */
	private boolean loadTileSets = true;
	
	/**
	 * Create a new tile map based on a given TMX file
	 * 
	 * @param ref The location of the tile map to load
	 * @throws Exception Indicates a failure to load the tilemap
	 */
	public TiledMap(String ref) throws Exception {
		this(ref, true);
	}

	/**
	 * Create a new tile map based on a given TMX file
	 * 
	 * @param ref The location of the tile map to load
	 * @param loadTileSets True if we want to load tilesets - including their image data
	 * @throws Exception Indicates a failure to load the tilemap
	 */
	public TiledMap(String ref, boolean loadTileSets) throws Exception {
		this.loadTileSets = loadTileSets;
		ref = ref.replace('\\','/');
		load(new FileInputStream(new File(ref)), ref.substring(0,ref.lastIndexOf("/")));
	}
	
	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in The input stream to load from
	 * @throws SlickException Indicates a failure to load the tilemap
	 */
	public TiledMap(InputStream in) throws Exception {
		load(in, "");
	}

	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in The input stream to load from
	 * @param tileSetsLocation The location at which we can find tileset images
	 * @throws SlickException Indicates a failure to load the tilemap
	 */
	public TiledMap(InputStream in, String tileSetsLocation) throws Exception {
		load(in, tileSetsLocation);
	}
	
	
	/**
     * Get the index of the layer with given name
     * 
     * @param name The name of the tile to search for
     * @return The index of the layer or -1 if there is no layer with given name
     */
   public int getLayerIndex(String name) {
      int idx = 0;
      
      for (int i=0;i<layers.size();i++) {
         TileLayer layer = (TileLayer) layers.get(i);
         
         if (layer.name.equals(name)) {
            return i;
         }
      }
      
      return -1;
   }
   
   /**
    * Gets the Image used to draw the tile at the given x and y coordinates.
    * 
    * @param x The x coordinate of the tile whose image should be retrieved
    * @param y The y coordinate of the tile whose image should be retrieved
    * @param layerIndex The index of the layer on which the tile whose image should be retrieve exists
    * @return The image used to draw the specified tile or null if there is no image for the
    * specified tile.
    */
   public Image getTileImage(int x, int y, int layerIndex) {
      TileLayer layer = (TileLayer) layers.get(layerIndex);
      
      int tileSetIndex = layer.data[x][y][0];
      if ((tileSetIndex >= 0) && (tileSetIndex < tileSets.size())) {
    	  TileSet tileSet = (TileSet) tileSets.get(tileSetIndex);
    	  
          int sheetX = tileSet.getTileX(layer.data[x][y][1]);
          int sheetY = tileSet.getTileY(layer.data[x][y][1]);
          
          return tileSet.tiles.getSubImage(sheetX, sheetY);
      }
      
      return null;
   } 
   
   public Image[][] getTileImageArray(int layerIndex) {
	   	Image[][] tileArray = new Image[width][height];
	    TileLayer layer = (TileLayer) layers.get(layerIndex);
	      
	    for(int x=0; x<width; x++) {
	    	for(int y=0; y<height; y++) {
	    		int tileSetIndex = layer.data[x][y][0];
	    		if ((tileSetIndex >= 0) && (tileSetIndex < tileSets.size())) {
	    			TileSet tileSet = (TileSet) tileSets.get(tileSetIndex);
		    	  
	    			int sheetX = tileSet.getTileX(layer.data[x][y][1]);
	    			int sheetY = tileSet.getTileY(layer.data[x][y][1]);
		          
	    			tileArray[x][y] = tileSet.tiles.getSubImage(sheetX, sheetY);
		      }
	    	}
	    }

	   return tileArray;
   }
   
	/**
	 * Get the width of the map
	 * 
	 * @return The width of the map (in tiles)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the map
	 * 
	 * @return The height of the map (in tiles)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the height of a single tile
	 * 
	 * @return The height of a single tile (in pixels)
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Get the width of a single tile
	 * 
	 * @return The height of a single tile (in pixels)
	 */
	public int getTileWidth() {
		return tileWidth;
	}
	   
	/**
	 * Get the global ID of a tile at specified location in the map
	 * 
	 * @param x
	 *            The x location of the tile
	 * @param y
	 *            The y location of the tile
	 * @param layerIndex
	 *            The index of the layer to retrieve the tile from
	 * @return The global ID of the tile
	 */
	public int getTileId(int x,int y,int layerIndex) {
		TileLayer layer = (TileLayer) layers.get(layerIndex);
		return layer.getTileID(x,y);
	}
	
	/**
	 * Set the global ID of a tile at specified location in the map
	 * @param x
	 *            The x location of the tile
	 * @param y
	 *            The y location of the tile
	 * @param layerIndex
	 *            The index of the layer to set the new tileid
	 * @param tileid
	 *            The tileid to be set
	 */
	public void setTileId(int x, int y, int layerIndex, int tileid) {
		TileLayer layer = (TileLayer) layers.get(layerIndex);
		layer.setTileID(x, y, tileid);
	}
	
	/**
	 * Get a property given to the map. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param propertyName The name of the property of the map to retrieve
	 * @param def The default value to return
	 * @return The value assigned to the property on the map (or the default value if none is supplied)
	 */
	public String getMapProperty(String propertyName, String def) {
		if (props == null)
			return def;
		return props.getProperty(propertyName, def);
	}
	
	/**
	 * Get a property given to a particular layer. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param layerIndex The index of the layer to retrieve
	 * @param propertyName The name of the property of this layer to retrieve
	 * @param def The default value to return
	 * @return The value assigned to the property on the layer (or the default value if none is supplied)
	 */
	public String getLayerProperty(int layerIndex, String propertyName, String def) {
		TileLayer layer = (TileLayer) layers.get(layerIndex);
		if (layer == null || layer.props == null)
			return def;
		return layer.props.getProperty(propertyName, def);
	}
	
	
	/**
	 * Get a propety given to a particular tile. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param tileID The global ID of the tile to retrieve
	 * @param propertyName The name of the property to retireve
	 * @param def The default value to return
	 * @return The value assigned to the property on the tile (or the default value if none is supplied)
	 */
	public String getTileProperty(int tileID, String propertyName, String def) {
		if (tileID == 0) {
			return def;
		}
		
		TileSet set = findTileSet(tileID);
		
		Properties props = set.getProperties(tileID);
		if (props == null) {
			return def;
		}
		return props.getProperty(propertyName, def);
	}
	
	/**
	 * Draw the entire TileD map with the specified Graphics2D object.
	 * 
	 * @param g The graphics object to draw with.
	 */
	public void draw( Graphics2D g) {
		for (int i=0;i<layers.size();i++) {
			TileLayer layer = (TileLayer) layers.get(i);
			layer.draw(g);
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
	public void draw( Graphics2D g, int x, int y, int width, int height) {
		for (int i=0;i<layers.size();i++) {
			TileLayer layer = (TileLayer) layers.get(i);
			layer.draw(g, x, y, width, height);
		}
	}
	
	/**
	 * Retrieve a count of the number of layers available
	 * 
	 * @return The number of layers available in this map
	 */
	public int getLayerCount() {
		return layers.size();
	}
	
	/**
	 * Load a TilED map
	 * 
	 * @param in The input stream from which to load the map
	 * @param tileSetsLocation The location from which we can retrieve tileset images
	 * @throws Exception Indicates a failure to parse the map or find a tileset
	 */
	private void load(InputStream in, String tileSetsLocation) throws Exception {
		tilesLocation = tileSetsLocation;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {					
					return new InputSource(new ByteArrayInputStream(new byte[0]));
				}
			});
			
			Document doc = builder.parse(in);
			Element docElement = doc.getDocumentElement();
			
			String orient = docElement.getAttribute("orientation");
			if (!orient.equals("orthogonal")) {
				throw new Exception("Only orthogonal maps supported, found: "+orient);
			}
			
			width = Integer.parseInt(docElement.getAttribute("width"));
			height = Integer.parseInt(docElement.getAttribute("height"));
			tileWidth = Integer.parseInt(docElement.getAttribute("tilewidth"));
			tileHeight = Integer.parseInt(docElement.getAttribute("tileheight"));
			
			// now read the map properties
			Element propsElement = (Element) docElement.getElementsByTagName("properties").item(0);
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
			
			if (loadTileSets) {
				TileSet tileSet = null;
				TileSet lastSet = null;
				
				NodeList setNodes = docElement.getElementsByTagName("tileset");
				for (int i=0;i<setNodes.getLength();i++) {
					Element current = (Element) setNodes.item(i);
					
					tileSet = new TileSet(this, current, true);
					tileSet.index = i;
					
					if (lastSet != null) {
						lastSet.setLimit(tileSet.firstGID-1);
					}
					lastSet = tileSet;
					
					tileSets.add(tileSet);
				}
			}
			
			NodeList layerNodes = docElement.getElementsByTagName("layer");
			for (int i=0;i<layerNodes.getLength();i++) {
				Element current = (Element) layerNodes.item(i);
				TileLayer layer = new TileLayer(this, current);
				layer.index = i;
				
				layers.add(layer);
			}
			
			// acquire object-groups
			NodeList objectGroupNodes = docElement.getElementsByTagName("objectgroup");
			     
			for (int i=0;i<objectGroupNodes.getLength();i++) {
				Element current = (Element) objectGroupNodes.item(i);
				ObjectGroup objectGroup = new ObjectGroup(current);
				objectGroup.index = i;
			        
				objectGroups.add(objectGroup);
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			throw new Exception("Failed to parse tilemap", e);
		}
	}
	
	/**
	 * Retrieve the number of tilesets available in this map
	 * 
	 * @return The number of tilesets available in this map
	 */
	public int getTileSetCount() {
		return tileSets.size();
	}
	
	/**
	 * Get a tileset at a particular index in the list of sets for this map
	 * 
	 * @param index The index of the tileset. 
	 * @return The TileSet requested
	 */
	public TileSet getTileSet(int index) {
		return (TileSet) tileSets.get(index);
	}
	
	/**
	 * Get the location of the tile images specified
	 * 
	 * @return The location of the tile images specified as a resource reference prefix
	 */
	public String getTilesLocation() {
		return tilesLocation;
	}
	
	/**
	 * Find a tile for a given global tile id
	 * 
	 * @param gid The global tile id we're looking for
	 * @return The tileset in which that tile lives or null if the gid is not defined
	 */
	public TileSet findTileSet(int gid) {
		for (int i=0;i<tileSets.size();i++) {
			TileSet set = (TileSet) tileSets.get(i);
			
			if (set.contains(gid)) {
				return set;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the number of object-groups defined in the map.
	 * @return Number of object-groups on the map
	 */
	public int getObjectGroupCount() {
		return objectGroups.size();
	}
	
	/**
	 * Returns the number of objects of a specific object-group.
	 * @param groupID The index of this object-group
	 * @return Number of the objects in the object-group or -1, when error occurred.
	 */
	public int getObjectCount(int groupID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			return grp.objects.size();
		}
		return -1;
	}
	
	/**
	 * Return the name of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The name of an object or null, when error occurred
	 */
	public String getObjectName(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.name;
			}
		}
		return null;
	}
	
	/**
	 * Return the type of an specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The type of an object or null, when error occurred
	 */
	public String getObjectType(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.type;
			}
		}
		return null;
	}
	
	/**
	 * Returns the x-coordinate of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The x-coordinate of an object, or -1, when error occurred
	 */
	public int getObjectX(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.x;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the y-coordinate of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The y-coordinate of an object, or -1, when error occurred
	 */
	public int getObjectY(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.y;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the width of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The width of an object, or -1, when error occurred
	 */
	public int getObjectWidth(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.width;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the height of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The height of an object, or -1, when error occurred
	 */
	public int getObjectHeight(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.height;
			}
		}
		return -1;
	}
	
	/**
	 * Retrieve the image source property for a given object
	 * 
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The image source reference or null if one isn't defined
	 */
	public String getObjectImage(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				
				if (object == null) {
					return null;
				}
				
				return object.image;
			}
		}
		
		return null;
	}
	
	/**
	 * Looks for a property with the given name and returns it's value. If no property is found,
	 * def is returned.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @param propertyName Name of a property
	 * @param def default value to return, if no property is found
	 * @return The value of the property with the given name or def, if there is no property with that name.
	 */
	public String getObjectProperty(int groupID, int objectID,
			String propertyName, String def) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				
				if (object == null) {
					return def;
				}
				if (object.props == null) {
					return def;
				}
				
				return object.props.getProperty(propertyName, def);
			}
		}
		return def;
	}
	
	/**
	 * A group of objects on the map (objects layer)
	 *
	 * @author kulpae
	 */
	protected class ObjectGroup {

	  public int index;
	  public String name;
	  public ArrayList objects;
	  public int width;
	  public int height;
	  
	  /** the properties of this group */
	  public Properties props;
	  
	  /**
	   * Create a new group based on the XML definition
	   *
	   * @param element The XML element describing the layer
	   * @throws Exception Indicates a failure to parse the XML group
	   */
	  public ObjectGroup(Element element) throws Exception {
			name = element.getAttribute("name");
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));
			objects = new ArrayList();

			// now read the layer properties
			Element propsElement = (Element) element.getElementsByTagName(
					"properties").item(0);
			if (propsElement != null) {
				NodeList properties = propsElement
						.getElementsByTagName("property");
				if (properties != null) {
					props = new Properties();
					for (int p = 0; p < properties.getLength(); p++) {
						Element propElement = (Element) properties.item(p);

						String name = propElement.getAttribute("name");
						String value = propElement.getAttribute("value");
						props.setProperty(name, value);
					}
				}
			}

			NodeList objectNodes = element.getElementsByTagName("object");
			for (int i = 0; i < objectNodes.getLength(); i++) {
				Element objElement = (Element) objectNodes.item(i);
				GroupObject object = new GroupObject(objElement);
				object.index = i;
				objects.add(object);
			}
		}
	}
	
	/**
	 * An object from a object-group on the map
	 * 
	 * @author kulpae
	 */
	protected class GroupObject {
	  /** The index of this object */
	  public int index;
	  /** The name of this object - read from the XML */
	  public String name;
	  /** The type of this object - read from the XML */
	  public String type;
	  /** The x-coordinate of this object */
	  public int x;
	  /** The y-coordinate of this object */
	  public int y;
	  /** The width of this object */
	  public int width;
	  /** The height of this object */
	  public int height;
	  /** The image source */
	  private String image;
	  
	  /** the properties of this group */
	  public Properties props;
	
	 /**
	 * Create a new group based on the XML definition
	 *
	 * @param element The XML element describing the layer
	 * @throws Exception Indicates a failure to parse the XML group
	 */
	 public GroupObject(Element element) throws Exception {
			name = element.getAttribute("name");
			type = element.getAttribute("type");
			x = Integer.parseInt(element.getAttribute("x"));
			y = Integer.parseInt(element.getAttribute("y"));
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));

			Element imageElement = (Element) element.getElementsByTagName(
					"image").item(0);
			if (imageElement != null) {
				image = imageElement.getAttribute("source");
			}
			
			// now read the layer properties
			Element propsElement = (Element) element.getElementsByTagName(
					"properties").item(0);
			if (propsElement != null) {
				NodeList properties = propsElement
						.getElementsByTagName("property");
				if (properties != null) {
					props = new Properties();
					for (int p = 0; p < properties.getLength(); p++) {
						Element propElement = (Element) properties.item(p);

						String name = propElement.getAttribute("name");
						String value = propElement.getAttribute("value");
						props.setProperty(name, value);
					}
				}
			}
		}
	}
	   
}
