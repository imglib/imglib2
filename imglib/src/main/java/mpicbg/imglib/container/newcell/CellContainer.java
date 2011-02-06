package mpicbg.imglib.container.newcell;

import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.container.AbstractNativeContainer;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.container.list.ListContainer;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.sampler.cell.CellStorageAccess;
import mpicbg.imglib.type.NativeType;

final public class CellContainer<
		T extends NativeType< T >,
		A extends DataAccess>
						extends AbstractNativeContainer< T, A >
{
	protected ListContainer< Cell< T , A > > cells;
	
	final int[] cellDimensions;
	
	public CellContainer( final T type, final Cell< T, A > cellType, final long[] dimensions, final int[] cellDimensions, int entitiesPerPixel )
	{
		super( dimensions, entitiesPerPixel );
		this.cells = new ListContainer< Cell< T, A > >( dimensions, cellType );
		this.cellDimensions = cellDimensions;
	}

	@Override @SuppressWarnings("unchecked")
	public A update( final Object cursor )
	{
		/* Currently, Cell<T,A>.update() is Array<T,A>.update().
		 * It will not know what to do with a CellCursor, however,
		 * it is not using it's parameter anyway.
		 */
		return ((CellAccess< T, A >)cursor).getCell().update( cursor );
	}

	@Override
	public ImgRandomAccess<T> randomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImgRandomAccess<T> randomAccess(
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
	public boolean equalIterationOrder( IterableRealInterval<?> f )
	{
		// TODO Auto-generated method stub
		return false;
	}

}
