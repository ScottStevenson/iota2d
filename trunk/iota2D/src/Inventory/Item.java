package Inventory;

public class Item 
{
	public String mName;
	public int mNumOfItems;
	public String mType;
	public int mValue;
	public int mWeight;
	public int mStrength;
	public int mCondition;
	public String mFileName;
	public String mDescription;
		
	public Item( String name, int numOfItems, String type, int value, int weight, int strength, int condition, String fileName, String desc )
	{
		mName = name;
		mNumOfItems = numOfItems;
		mType = type;
		mValue = value;
		mWeight = weight;
		mStrength = strength;
		mCondition = condition;
		mFileName = fileName;
		mDescription = desc;
		
	}
	
	public Item( Item item )
	{
		if( item != null )
		{
			this.mName = item.mName;
			this.mNumOfItems = item.mNumOfItems;
			this.mType = item.mType;
			this.mValue = item.mValue;
			this.mWeight = item.mWeight;
			this.mStrength = item.mStrength;
			this.mCondition = item.mCondition;
			this.mFileName = item.mFileName;
			this.mDescription = item.mDescription;
		}
		else
		{
			System.out.println( "Error null item" );
		}
	}
}
