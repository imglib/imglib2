package net.imglib2.blocks;

import java.lang.ref.WeakReference;

class TempArrayImpl< T > implements TempArray< T >
{
	private final PrimitiveTypeProperties< T, ? > props;

	private WeakReference< T > arrayRef;

	TempArrayImpl( PrimitiveTypeProperties< T, ? > props )
	{
		arrayRef = new WeakReference<>( null );
		this.props = props;
	}

	@Override
	public T get( final int minSize )
	{
		T array = arrayRef.get();
		if ( array == null || props.length( array ) < minSize )
		{
			array = props.allocate( minSize );
			arrayRef = new WeakReference<>( array );
		}
		return array;
	}

	@Override
	public TempArray< T > newInstance()
	{
		return new TempArrayImpl<>( props );
	}
}
