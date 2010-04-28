package mpicbg.imglib.cursor.dynamic;

import mpicbg.imglib.container.dynamic.DynamicContainer;
import mpicbg.imglib.cursor.LocalizableIterableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class DynamicLocalizableCursor<T extends Type<T>> extends DynamicIterableCursor<T> implements LocalizableIterableCursor<T>
{
	final protected int numDimensions; 	
	final protected int[] position, dimensions;

	public DynamicLocalizableCursor( final DynamicContainer<T,?> container, final Image<T> image, final T type )
	{
		super( container, image, type );
		
		this.numDimensions = container.numDimensions();

		this.position = image.createPositionArray();
		this.dimensions = container.getDimensionsInt();
		
		// unluckily we have to call it twice, in the superclass position is not initialized yet
		reset();
	}
	
	@Override
	public void fwd()
	{ 
		++internalIndex; 
		accessor.updateIndex( internalIndex );
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			if ( position[ d ] < dimensions[ d ] - 1 )
			{
				position[ d ]++;
				
				for ( int e = 0; e < d; e++ )
					position[ e ] = 0;
				
				break;
			}
		}
		
		linkedIterator.fwd();
	}

	@Override
	public void jumpFwd( final long steps )
	{ 
		for ( long j = 0; j < steps; ++j )
			fwd();
	}
	
	@Override
	public boolean hasNext() { return internalIndex < container.getNumPixels() - 1; }
	

	@Override
	public void reset()
	{
		if ( dimensions != null )
		{
			type.updateIndex( 0 );
			internalIndex = 0;
			type.updateContainer( this );
			accessor.updateIndex( internalIndex );
			internalIndex = -1;
			
			position[ 0 ] = -1;
			
			for ( int d = 1; d < numDimensions; d++ )
				position[ d ] = 0;		
		}
		
		linkedIterator.reset();
	}

	
	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	
	@Override
	public float getFloatPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public double getDoublePosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }
	

	@Override
	public String getLocationAsString()
	{
		String pos = "(" + position[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + position[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getLocationAsString() + " = " + type(); }
	
	@Override
	public int numDimensions(){ return numDimensions; }
}
