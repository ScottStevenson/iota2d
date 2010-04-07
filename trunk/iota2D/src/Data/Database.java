package Data;

import org.w3c.dom.Document;
import javax.xml.xpath.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import java.io.File;

/**
 * A class for reading and writing to a proprietary XML-based database.
 */
public class Database {
	/* The XML Doc */
	protected Document mDoc;
	
	/**
	 * Constructs a new Database object from the given file.
	 * @param databaseFile Path to the XML database file to read.
	 */
	public Database(String databaseFile){
		File file = new File(databaseFile);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			mDoc = (Document) db.parse(file);
		}catch(Exception e)
		{	
			System.out.print(e.getMessage());
			e.printStackTrace();
			System.out.print("Could not parse XML databse\n");
		}
	}
	
	/**
	 * Returns the value of the specified object's property
	 * 
	 * @param objectPath The path to the object in the database. ie: "levels.level1"
	 * @param property The property to retrieve from the specified object.
	 * @return The String result of the query. If the property was not found
	 * null is returned. If multiple values are found, the first is returned.
	 */
	public String get(String objectPath, String property){
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		String[] objArray = objectPath.split("\\.");
		String xpathStr = new String("//");
		
		for (String s : objArray) {
			xpathStr = xpathStr + "object[@name=\'" + s + "\']/";
		}
		xpathStr = xpathStr + "property[@name=\'"+ property +"\']/text()";
		
		//System.out.print(s+"\n");
		
		
		
		try {
			String result = (String) xpath.evaluate(xpathStr, mDoc, XPathConstants.STRING);
			return result;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
