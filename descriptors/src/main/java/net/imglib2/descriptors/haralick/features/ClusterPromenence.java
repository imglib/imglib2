package net.imglib2.descriptors.haralick.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.descriptors.haralick.helpers.CoocParameter;
import net.imglib2.descriptors.haralick.helpers.CoocStdX;
import net.imglib2.ops.data.CooccurrenceMatrix;

//cluster promenence (from cellcognition)
// https://github.com/CellCognition/cecog/blob/master/csrc/include/cecog/features.hxx#L479
public class ClusterPromenence extends AbstractFeatureModule
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
		return "Cluster Promenence";
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

		double res = 0;
		for ( int j = 0; j < nrGrayLevels; j++ )
		{
			res += Math.pow( 2 * j - 2 * stdx, 4 ) * matrix.getValueAt( j, j );
			for ( int i = j + 1; i < nrGrayLevels; i++ )
			{
				res += 2 * Math.pow( ( i + j - 2 * stdx ), 4 ) * matrix.getValueAt( i, j );
			}
		}
		return res;
	}

}
