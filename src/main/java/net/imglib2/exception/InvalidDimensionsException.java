package net.imglib2.exception;

import net.imglib2.util.Util;

public class InvalidDimensionsException extends IllegalArgumentException
{
	private final long[] dimensions;

	public InvalidDimensionsException( final long[] dimensions, final String message )
	{
		super( message );
		this.dimensions = dimensions.clone();
	}

	public InvalidDimensionsException( final int[] dimensions, final String message )
	{
		this( Util.int2long( dimensions ), message );
	}

	public long[] getDimenionsCopy()
	{
		return this.dimensions.clone();
	}
}
