package net.imglib2.descriptors.haralick.helpers;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.ops.data.CooccurrenceMatrix;

public class CoocPXPlusY extends AbstractDescriptorModule
{
	@ModuleInput
	CoocParameter param;

	@ModuleInput
	CoocccurrenceMatrix cooc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] recompute()
	{
		int nrGrayLevels = param.nrGrayLevels;
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

	@Override
	public String name()
	{
		return "CoocPXPlusY";
	}

}
