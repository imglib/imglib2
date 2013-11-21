package net.imglib2.type;

public enum NativeTypeId
{
	Other           ( "Other", -1 ),

	Byte            ( "Byte",         0 ),
	UnsignedByte    ( "UnsignedByte", 1 ),
	Int             ( "Int",          2 ),
	UnsignedInt     ( "UnsignedInt",  3 ),
	Float           ( "Float",        4 ),
	Double          ( "Double",       5 );

	private final String name;

	private final int intId;

	public String getName()
	{
		return name;
	}

	public int getIntegerId()
	{
		return intId;
	}

	private NativeTypeId( final String name, final int intId )
	{
		this.name = name;
		this.intId = intId;
	}
}
