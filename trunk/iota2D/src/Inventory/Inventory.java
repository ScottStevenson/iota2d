package Inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;




public class Inventory 
{
	public int mCarryingCapacity;
	public int mLoad; 
	private Objects mObjects;
	private HashMap<String, Item> mInventory;
	
	public Inventory( int carryingCapacity, Objects objects )
	{
		mObjects = objects;
		mCarryingCapacity = carryingCapacity;
		mInventory = new HashMap<String, Item>();
		try 
		{
			loadInventory();
		} 
		catch( FileNotFoundException e ) 
		{
			e.printStackTrace();
		}
	}
	
	private void loadInventory() throws FileNotFoundException
	{
		boolean isFilled = false;
		Scanner mScanner = new Scanner( new File( "content/config/character.ini" ));
		mScanner.nextLine();
		mScanner.nextLine();
		while( !isFilled )
		{
			if( mScanner.findInLine( "Item" ) != null )
			{
				System.out.println( "Adding" );
				add( mScanner.next(), mScanner.next(), mScanner.nextInt() );	
				mScanner.nextLine();
				if(	mScanner.hasNext( "EndInventory" ))
				{
					isFilled = true;
				}
			}
			else
			{
				isFilled = true;				
			}
		}
	}
	
	public void saveInventory() throws FileNotFoundException
	{		
	}
	
	// Adds a item to the inventory if it doesn't exceed the carrying capacity
	public String add( String name, String type, int num )
	{
		System.out.print( "adding " + name + " " + type + "\n" );
		Item item = null;

		if( type.equals( "Armour" ) )
		{
			item = new Item( mObjects.armour.get( name ));
		}
		if( type.equals( "Item" ))
		{
			item = new Item( mObjects.items.get( name ));
		}
		if( type.equals( "Weapon" ))
		{
			item = new Item( mObjects.weapons.get( name ));
		}		

		if( item != null )
		{
			if(( mLoad + item.mWeight ) > mCarryingCapacity )
			{
				return "Overloaded";
			}
			else
			{
				if( mInventory.containsKey( name ))
				{
					mInventory.get( name ).mNumOfItems += num;
				}
				else
				{
					item.mNumOfItems = num;
					mInventory.put( name, item  );
				}
				return "Added " + item.mName;
			}
		}
		else 
		{
			return "Not Added";
		}

	}
	
	// Removes a item from the map if present else returns null
	public Item remove( String name )
	{
		Item item = mInventory.remove( name );
		if( item.mNumOfItems > 1 )
		{
			item.mNumOfItems = item.mNumOfItems - 1;
			mInventory.put( name, item );
			item.mNumOfItems = 1;
		}
		return item;
	}
	
	public void draw()
	{
		
	}
	


}
