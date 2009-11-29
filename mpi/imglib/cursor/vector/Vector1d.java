package mpi.imglib.cursor.vector;

public class Vector1d extends AbstractVector<Vector1d>
{
	int x;
	
	public Vector1d( final int numDimensions )
	{
		super( 1 );
		x = 0;
	}
	
	public int getPosition( final int dimension ) { return x; }
	public void setPosition( final int dimension, final int value ) 
	{ 
		x = value;
	}
	
	public void add( final Vector1d vector2 )
	{
		x += vector2.x;
	}

	public void sub( final Vector1d vector2 )
	{
		x -= vector2.x;
	}

}
