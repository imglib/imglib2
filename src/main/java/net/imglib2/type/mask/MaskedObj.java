package net.imglib2.type.mask;

public class MaskedObj< T > extends AbstractMasked< T, MaskedObj< T > >
{
	private T value;

	public MaskedObj( final T value )
	{
		this( value, 0 );
	}

	public MaskedObj( final T value, double mask )
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
