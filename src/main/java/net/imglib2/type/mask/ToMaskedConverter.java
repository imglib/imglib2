package net.imglib2.type.mask;

import net.imglib2.converter.Converter;

/**
 * Converts a {@code T} to a {@code Masked<T>}. It just sets the {@code value()}
 * of the {@code Masked} to the input value, leaving the mask untouched.
 * <p>
 * For example, this is useful to mask a {@code RandomAccessibleInterval} with
 * constant 1. The result can then be extended with a constant value mask 0. The
 * result is a {@code RandomAccessible} that is fully opaque inside the original
 * interval and fully transparent outside.
 */
public class ToMaskedConverter< A, B extends Masked< A > > implements Converter< A, B >
{
	@Override
	public void convert( final A input, final B output )
	{
		output.setValue( input );
	}
}
