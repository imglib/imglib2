package mpicbg.imglib.cursor;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public abstract class AbstractLocalizableCursor< T extends Type< T > > extends AbstractCursor< T > implements LocalizableCursor< T > 
{
	final protected int[] position, dimensions;
	
	public AbstractLocalizableCursor( final Container<T> container, final Image<T> image )
	{
		super( container, image );
		
		this.position = new int[ numDimensions ];
		this.dimensions = image.getDimensions();
	}
	
	@Override
	public void localize( float[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( long[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public float getFloatPosition( final int d )
	{
		return position[ d ];
	}
	
	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }	
	@Override
	public int numDimensions(){ return numDimensions; }

	@Override
	public String getLocationAsString() { return MathLib.printCoordinates( position ); }
	
	@Override
	public String toString() { return getLocationAsString() + " = " + type(); }		
}
