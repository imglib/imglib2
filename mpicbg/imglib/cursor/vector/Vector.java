package mpicbg.imglib.cursor.vector;

public class Vector extends AbstractVector<Vector>
{
	final int[] vector;
	
	public Vector( final Dimensionality dim )
	{
		this( dim.getNumDimensions() );
	}
	
	public Vector( final int numDimensions )
	{
		super( numDimensions );
		this.vector = new int[ numDimensions ];
	}
	
	public int getPosition( final int dimension ) { return vector[ dimension ]; }
	public void setPosition( final int dimension, final int value ) { vector[ dimension ] = value; }
	
	public void add( final Vector vector2 )
	{
		for ( int d = 0; d < numDimensions; ++d )
			vector[ d ] += vector2.vector[ d ];
	}

	public void add( final int value )
	{
		for ( int d = 0; d < numDimensions; ++d )
			vector[ d ] += value;
	}

	public void sub( final Vector vector2 )
	{
		for ( int d = 0; d < numDimensions; ++d )
			vector[ d ] -= vector2.vector[ d ];
	}

	public void sub( final int value )
	{
		for ( int d = 0; d < numDimensions; ++d )
			vector[ d ] -= value;
	}
	
	
}
