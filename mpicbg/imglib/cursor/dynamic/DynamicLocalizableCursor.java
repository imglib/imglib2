package mpicbg.imglib.cursor.dynamic;

import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.container.dynamic.DynamicContainer;
import mpicbg.imglib.container.dynamic.DynamicContainerAccessor;
import mpicbg.imglib.cursor.AbstractLocalizableIterableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class DynamicLocalizableCursor< T extends Type< T > >
		extends AbstractLocalizableIterableCursor< T >
		implements DynamicStorageAccess
{
	/* the type instance accessing the pixel value the cursor points at */
	protected final T type;
	
	/* a stronger typed pointer to Container< T > */
	protected final DynamicContainer< T, ? extends DataAccess > container;
	
	/* access proxy */
	protected final DynamicContainerAccessor accessor;

	protected int internalIndex;
	
	public DynamicLocalizableCursor(
			final DynamicContainer< T, ? > container,
			final Image< T > image,
			final T type )
	{
		super( container, image );
		
		this.type = type;
		this.container = container;
	
		accessor = container.createAccessor();
		
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
	public DynamicContainer< T, ? > getContainer(){ return container; }
	
	@Override
	public T type() { return type; }

	@Override
	public DynamicContainerAccessor getAccessor() { return accessor; }

	@Override
	public int getInternalIndex() { return internalIndex; }
}
