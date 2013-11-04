package net.imglib2.ops.descriptors.haralick.helpers;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.HaralickCoocMatrix;

public class CoocPX extends AbstractModule< double[] >
{
	@ModuleInput
	private HaralickCoocMatrix cooc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoocPX copy()
	{
		return new CoocPX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] recompute()
	{
		int nrGrayLevels = cooc.getNrGrayLevels();
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

}
