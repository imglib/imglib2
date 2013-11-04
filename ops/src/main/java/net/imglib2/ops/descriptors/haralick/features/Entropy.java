package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;

public class Entropy extends AbstractFeatureModule
{

	private static final double EPSILON = 0.00000001f;

	@ModuleInput
	CoocParameter param;

	@ModuleInput
	private CoocccurrenceMatrix cooc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Entropy";
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
				res += matrix.getValueAt( i, j ) * Math.log( matrix.getValueAt( i, j ) + EPSILON );
			}
		}

		res = -res;

		return res;
	}
}
