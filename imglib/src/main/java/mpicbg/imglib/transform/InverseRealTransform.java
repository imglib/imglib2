package mpicbg.imglib.transform;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

public final class InverseRealTransform implements InvertibleRealTransform
{
	private final InvertibleRealTransform inverse;
	
	public InverseRealTransform (InvertibleRealTransform transform)
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
	public void apply( double[] source, double[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( float[] source, float[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( RealLocalizable source, RealPositionable target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void applyInverse( double[] source, double[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( float[] source, float[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( RealPositionable source, RealLocalizable target )
	{
		inverse.apply( target, source );
	}

	@Override
	public InvertibleRealTransform inverse()
	{
		return inverse;
	}

}
