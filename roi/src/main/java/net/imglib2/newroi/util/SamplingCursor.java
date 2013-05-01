package net.imglib2.newroi.util;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.converter.AbstractConvertedCursor;
import net.imglib2.type.BooleanType;

public class SamplingCursor< B extends BooleanType< B >, T > extends AbstractConvertedCursor< B, T >
{
	protected final RandomAccess< T > target;

	public SamplingCursor( final Cursor< B > source, final RandomAccess< T > target )
	{
		super( source );
		this.target = target;
	}

	@Override
	public T get()
	{
		target.setPosition( source );
		return target.get();
	}

	@Override
	public SamplingCursor< B, T > copy()
	{
		return new SamplingCursor< B, T >( source.copyCursor(), target.copyRandomAccess() );
	}

	@Override
	public SamplingCursor< B, T > copyCursor()
	{
		return copy();
	}
}
