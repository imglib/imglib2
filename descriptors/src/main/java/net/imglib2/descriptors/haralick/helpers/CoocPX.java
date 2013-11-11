package net.imglib2.descriptors.haralick.helpers;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.haralick.CoocccurrenceMatrix;
import net.imglib2.ops.data.CooccurrenceMatrix;

public class CoocPX extends AbstractDescriptorModule
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

		double[] px = new double[ nrGrayLevels ];
		for ( int i = 0; i < nrGrayLevels; i++ )
		{
			for ( int j = 0; j < nrGrayLevels; j++ )
			{
				px[ i ] += matrix.getValueAt( i, j );
			}
		}

		return px;
	}

	@Override
	public String name()
	{
		return "CoocPX";
	}

}
