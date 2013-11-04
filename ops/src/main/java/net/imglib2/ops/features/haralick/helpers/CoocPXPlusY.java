package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractModule;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;

public class CoocPXPlusY extends AbstractModule< double[] >
{

	@ModuleInput
	private HaralickCoocMatrix cooc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoocPXPlusY copy()
	{
		return new CoocPXPlusY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] calculateDescriptor()
	{
		int nrGrayLevels = cooc.getNrGrayLevels();
		CooccurrenceMatrix matrix = cooc.get();

		double[] pxplusy = new double[ 2 * nrGrayLevels + 1 ];

		for ( int k = 2; k <= 2 * nrGrayLevels; k++ )
		{
			for ( int i = 0; i < nrGrayLevels; i++ )
			{
				for ( int j = 0; j < nrGrayLevels; j++ )
				{
					if ( ( i + 1 ) + ( j + 1 ) == k )
					{
						pxplusy[ k ] += matrix.getValueAt( i, j );
					}
				}
			}
		}

		return pxplusy;
	}

}
