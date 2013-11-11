package net.imglib2.descriptors.haralick.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.descriptors.haralick.helpers.CoocParameter;
import net.imglib2.descriptors.haralick.helpers.CoocStdX;
import net.imglib2.ops.data.CooccurrenceMatrix;

// cluster shade (from cellcognition)
// https://github.com/CellCognition/cecog/blob/master/csrc/include/cecog/features.hxx#L495
public class ClusterShade extends AbstractFeatureModule
{

	@ModuleInput
	CoocParameter param;

	@ModuleInput
	CoocccurrenceMatrix cooc;

	@ModuleInput
	CoocStdX coocStdX;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Cluster Shade";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		final int nrGrayLevels = param.nrGrayLevels;
		final double stdx = coocStdX.value();
		final CooccurrenceMatrix matrix = cooc.get();

		double res = 0.0d;
		for ( int j = 0; j < nrGrayLevels; j++ )
		{
			res += Math.pow( 2 * j - 2 * stdx, 3 ) * matrix.getValueAt( j, j );
			for ( int i = j + 1; i < nrGrayLevels; i++ )
			{
				res += 2 * Math.pow( ( i + j - 2 * stdx ), 3 ) * matrix.getValueAt( i, j );
			}
		}

		return res;
	}
}
