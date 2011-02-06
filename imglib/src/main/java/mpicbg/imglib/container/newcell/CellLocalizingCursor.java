package mpicbg.imglib.container.newcell;

import mpicbg.imglib.Interval;
import mpicbg.imglib.container.AbstractImgLocalizingCursor;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.type.NativeType;

public class CellLocalizingCursor< T extends NativeType< T >, A extends DataAccess > extends AbstractImgLocalizingCursor< T >
{
	protected final CellContainer< T, A > container;

	public CellLocalizingCursor( final CellContainer< T, A > container )
	{
		super( container );

		this.container = container;
		this.type = container.createLinkedType();
		this.lastIndex = ( int )container.size() - 1;

		reset();
	}

	@Override
	public T get()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fwd()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasNext()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CellContainer< T, ? > getImg()
	{
		return container;
	}
}
