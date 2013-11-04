package net.imglib2.ops.descriptors.haralick.helpers;

import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.type.numeric.real.DoubleType;

public class CoocMeanX extends AbstractModule< DoubleType >
{

	@ModuleInput
	private CoocPX coocPX;


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
