package mpicbg.imglib.view;

import mpicbg.imglib.outofbounds.AbstractOutOfBoundsRandomAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;

public class ViewOutOfBoundsRandomAccess< T > extends AbstractOutOfBoundsRandomAccess< T >
{
	public <F> ViewOutOfBoundsRandomAccess( final View< T > view, final OutOfBoundsFactory< T, View< T > > outOfBoundsFactory ) 
	{
		super( view.numDimensions(), outOfBoundsFactory.create( view ) );
	}
}
