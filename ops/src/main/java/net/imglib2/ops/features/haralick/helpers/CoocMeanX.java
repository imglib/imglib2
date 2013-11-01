package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocMeanX extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private CoocPX coocPX = new CoocPX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Helper CoocMeanX";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoocMeanX copy()
	{
		return new CoocMeanX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		double res = 0;
		double[] px = coocPX.get();
		for ( int i = 0; i < px.length; i++ )
		{
			res += i * px[ i ];
		}
		return new DoubleType( res );
	}

}
