package net.imglib2.descriptors.haralick.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.descriptors.haralick.helpers.CoocParameter;
import net.imglib2.ops.data.CooccurrenceMatrix;

public class IFDM extends AbstractFeatureModule
{

	@ModuleInput
	CoocParameter param;

	@ModuleInput
	CoocccurrenceMatrix cooc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "IFDM";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final int nrGrayLevels = param.nrGrayLevels;
		final CooccurrenceMatrix matrix = cooc.get();

		double res = 0;
		for ( int i = 0; i < nrGrayLevels; i++ )
		{
			for ( int j = 0; j < nrGrayLevels; j++ )
			{
				res += 1.0 / ( 1 + ( i - j ) * ( i - j ) ) * matrix.getValueAt( i, j );
			}
		}

		return res;
	}
}
