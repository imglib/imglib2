package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.features.AbstractModule;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocStdX extends AbstractModule< DoubleType >
{

	@ModuleInput
	private CoocPX coocPX = new CoocPX();

	@ModuleInput
	private CoocMeanX coocMeanX = new CoocMeanX();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoocStdX copy()
	{
		return new CoocStdX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType calculateDescriptor()
	{
		double res = 0;

		double meanx = coocMeanX.get().get();
		double[] px = coocPX.get();

		for ( int i = 0; i < px.length; i++ )
		{
			res += ( i - meanx ) * ( i - meanx ) * px[ i ];
		}

		res = Math.sqrt( res );

		return new DoubleType( res );
	}

}
