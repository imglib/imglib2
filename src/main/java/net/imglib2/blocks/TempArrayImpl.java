package net.imglib2.blocks;

class TempArrayImpl< T > implements TempArray< T >
{
	private final PrimitiveTypeProperties< T, ? > props;

	private T array;

	TempArrayImpl( PrimitiveTypeProperties< T, ? > props )
	{
		this.props = props;
		array = props.allocate( 0 );
	}

	@Override
	public T get( final int minSize )
	{
		if ( props.length( array ) < minSize )
			array = props.allocate( minSize );
		return array;
	}

	@Override
	public TempArray< T > newInstance()
	{
		return new TempArrayImpl<>( props );
	}
}
