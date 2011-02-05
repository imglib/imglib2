package mpicbg.imglib.container.newcell;

import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.AbstractNativeContainer;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.NativeType;

final public class CellContainer< T extends NativeType< T >, A extends DataAccess, C extends ArrayOfCells< A > > extends AbstractNativeContainer< T, A >
{
	final private T type;

	protected C cellArray;
	
	public CellContainer( final T type, final C cellArray, final long[] dimensions, final int[] cellDimensions, int entitiesPerPixel )
	{
		super( dimensions, entitiesPerPixel );
		this.type = type;
		this.cellArray = cellArray;
	}

	@Override
	public A update( Object updater )
	{
		return cellArray.getDataAccessForCell( 0 );
	}

	@Override
	public ImgRandomAccess<T> integerRandomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImgRandomAccess<T> integerRandomAccess(
			OutOfBoundsFactory<T, Img<T>> factory )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImgCursor<T> cursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImgCursor<T> localizingCursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImgFactory<T> factory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T createVariable()
	{
		return type.createVariable();
	}

	@Override
	public boolean equalIterationOrder( IterableRealInterval<?> f )
	{
		// TODO Auto-generated method stub
		return false;
	}

}
