package net.imglib2.blocks;

import net.imglib2.RandomAccessible;
import net.imglib2.type.NativeType;

class FallbackProperties< T extends NativeType< T > >
{
	private final T viewType;

	private final RandomAccessible< T > view;

	/**
	 * TODO: javadoc
	 *
	 * @param viewType
	 * 		pixel type of the View to copy from
	 * @param view
	 */
	FallbackProperties( final T viewType, final RandomAccessible< T > view )
	{
		this.viewType = viewType;
		this.view = view;
	}

	@Override
	public String toString()
	{
		return "FallbackProperties{" +
				"viewType=" + viewType.getClass().getSimpleName() +
				", view=" + view +
				'}';
	}

	public T getViewType()
	{
		return viewType;
	}

	public RandomAccessible< T > getView()
	{
		return view;
	}
}
