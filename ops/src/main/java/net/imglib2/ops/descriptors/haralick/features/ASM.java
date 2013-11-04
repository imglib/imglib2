package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;

public class ASM extends AbstractFeatureModule
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
		return "ASM";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{
		final int nrGrayLevels = param.nrGrayLevels;
		final CooccurrenceMatrix matrix = cooc.get();

		double res = 0;
		for ( int i = 0; i < nrGrayLevels; i++ )
		{
			for ( int j = 0; j < nrGrayLevels; j++ )
			{
				res += matrix.getValueAt( i, j ) * matrix.getValueAt( i, j );
			}
		}

		return res;
	}

}
