package net.imglib2.type.mask;

public class MaskedObj< T > extends AbstractMasked< T, MaskedObj< T > >
{
	private T value;

	public MaskedObj( T value )
	{
		this( value, 0 );
	}

	public MaskedObj( T value, double mask )
	{
		super( mask );
		this.value = value;
	}

	@Override
	public T value()
	{
		return value;
	}

	public void setValue( T value )
	{
		this.value = value;
	}
}
