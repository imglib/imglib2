package net.imglib2.exception;

public class InvalidDimensionsException extends IllegalArgumentException
{
	private final long[] dimensions;

	public InvalidDimensionsException( final long[] dimensions, final String message )
	{
		super( message );
		this.dimensions = dimensions.clone();
	}

	public long[] getDimenionsCopy()
	{
		return this.dimensions.clone();
	}
}
