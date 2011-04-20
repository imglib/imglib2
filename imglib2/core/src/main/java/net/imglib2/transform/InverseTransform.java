package net.imglib2.transform;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

public final class InverseTransform implements InvertibleTransform
{
	private final InvertibleTransform inverse;
	
	public InverseTransform (InvertibleTransform transform)
	{
		inverse = transform;
	}

	@Override
	public int numSourceDimensions()
	{
		return inverse.numTargetDimensions();
	}

	@Override
	public int numTargetDimensions()
	{
		return inverse.numSourceDimensions();
	}

	@Override
	public void apply( long[] source, long[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( int[] source, int[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( Localizable source, Positionable target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void applyInverse( long[] source, long[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( int[] source, int[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( Positionable source, Localizable target )
	{
		inverse.apply( target, source );
	}

	@Override
	public InvertibleTransform inverse()
	{
		return inverse;
	}
}
