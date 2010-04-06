package Inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;





public class Objects
{
	public HashMap<String, Item> items;
	public HashMap<String, Item> weapons;
	public HashMap<String, Item> armour;
	
	public Objects()
	{
		armour = new HashMap<String, Item>();
		items = new HashMap<String, Item>();
		weapons = new HashMap<String, Item>();
		
		try
		{
			loadObjects();
		} 
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}		
	}
	
	private void loadObjects() throws FileNotFoundException
	{
		Scanner mScanner = new Scanner( new File( "content/config/objects.ini" ));
		mScanner.nextLine();
		boolean isAtEndOfFile = false;
		String name;
		String type;
		int value;
		int weight;
		int strength;
		int condition;
		String fileName;
		String desc;
		while( !isAtEndOfFile )
		{			
			
			if( mScanner.findInLine( "Item" ) != null )
			{
				System.out.print( "Found Item " );
				name = mScanner.next();
				type = mScanner.next();
				value = mScanner.nextInt();
				weight = mScanner.nextInt();
				strength = mScanner.nextInt();
				condition = mScanner.nextInt();
				fileName = mScanner.next();
				desc = mScanner.next();
				Item item = new Item( name, 1, type, value, weight, strength, condition, fileName, desc );
				if( type.equals( "Armour" ))
				{
					items.put( name, item );
				}
				if( type.equals( "Weapon" ))
				{
					weapons.put( name, item );
				}
				if( type.contains( "Item" ))
				{
					armour.put( name, new Item( name, 1, type, value, weight, strength, condition, fileName, desc ));					
				}
				System.out.print( name + " " + type + " " + desc + "\n" ); 
				mScanner.nextLine();
			}
			else
			{
				isAtEndOfFile = true;
			}						
		}		
		
	}

}
