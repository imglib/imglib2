package net.imglib2.type.mask;

public class MaskedObject< T > extends AbstractMasked< T, MaskedObject< T > >
{
	private T value;

	public MaskedObject( final T value )
	{
		this( value, 0 );
	}

	public MaskedObject( final T value, double mask )
	{
		super( mask );
		this.value = value;
	}

	@Override
	public T value()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value = value;
	}
}
