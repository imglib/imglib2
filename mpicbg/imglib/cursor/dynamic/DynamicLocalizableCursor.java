package mpicbg.imglib.cursor.dynamic;

import mpicbg.imglib.container.dynamic.DynamicContainer;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class DynamicLocalizableCursor<T extends Type<T>> extends DynamicCursor<T> implements LocalizableCursor<T>
{
	final protected int numDimensions; 	
	final protected int[] position, dimensions;

	public DynamicLocalizableCursor( final DynamicContainer<T> container, final Image<T> image, final T type )
	{
		super( container, image, type );
		
		this.numDimensions = container.getNumDimensions();

		this.position = image.createPositionArray();
		this.dimensions = container.getDimensions();
		
		// unluckily we have to call it twice, in the superclass position is not initialized yet
		reset();
	}
	
	@Override
	public void fwd()
	{ 
		++internalIndex; 
		type.updateDataArray( this );
		
		for ( int d = 0; d < numDimensions; d++ )
		{
			if ( position[ d ] < dimensions[ d ] - 1 )
			{
				position[ d ]++;
				
				for ( int e = 0; e < d; e++ )
					position[ e ] = 0;
				
				return;
			}
		}
	}
	
	@Override
	public boolean hasNext() { return internalIndex < container.getNumPixels() - 1; }
	

	@Override
	public void reset()
	{
		if ( dimensions == null )
			return;

		type.updateIndex( 0 );
		internalIndex = 0;
		type.updateDataArray( this );
		internalIndex = -1;
		isClosed = false;
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;		
	}

	@Override
	public void getPosition( int[] position )
	{
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}
	
	@Override
	public int[] getPosition(){ return position.clone(); }
	
	@Override
	public int getPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public String getPositionAsString()
	{
		String pos = "(" + position[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + position[ d ];
		
		pos += ")";
		
		return pos;
	}
	
	@Override
	public String toString() { return getPositionAsString() + " = " + getType(); }	
}
