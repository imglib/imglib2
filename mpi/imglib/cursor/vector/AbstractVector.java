package mpi.imglib.cursor.vector;

public abstract class AbstractVector<V extends AbstractVector<V>>
{
	final int numDimensions;
	
	public AbstractVector( final Dimensionality dim )
	{
		this( dim.getNumDimensions() );
	}
	
	public AbstractVector( final int numDimensions )
	{
		this.numDimensions = numDimensions;
	}
	
	public abstract int getPosition( final int dimension );
	public abstract void setPosition( final int dimension, final int value );

	public abstract void add( final int value );
	public abstract void sub( final int value );
	
	public abstract void add( final V vector );
	public abstract void sub( final V vector );
}
