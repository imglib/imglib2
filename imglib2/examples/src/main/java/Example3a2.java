public Example3a()
{
	// it will work as well on a normal ArrayList
	ArrayList< FloatType > list = new ArrayList< FloatType >();

	// put values 0 to 10 into the ArrayList
	for ( int i = 0; i <= 10; ++i )
		list.add( new FloatType( i ) );

	// create two empty variables
	FloatType min = new FloatType();
	FloatType max = new FloatType();

	// compute min and max of the ArrayList
	computeMinMax( list, min, max );

	System.out.println( "minimum Value (arraylist): " + min );
	System.out.println( "maximum Value (arraylist): " + max );
}
