package net.imglib2.ops.features.haralick.helpers;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractModule;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;

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
	protected double[] calculateDescriptor()
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
