package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.annotations.RequiredInput;
import net.imglib2.ops.features.datastructures.AbstractFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.type.numeric.real.DoubleType;

public class Entropy extends AbstractFeature
{

	private static final double EPSILON = 0.00000001f;

	@RequiredInput
	private HaralickCoocMatrix cooc;

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
	public IFDM copy()
	{
		return new IFDM();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		final int nrGrayLevels = cooc.getNrGrayLevels();
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

		return new DoubleType( res );
	}
}
