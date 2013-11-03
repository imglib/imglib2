package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.ops.features.haralick.helpers.CoocStdX;
import net.imglib2.type.numeric.real.DoubleType;

// cluster shade (from cellcognition)
// https://github.com/CellCognition/cecog/blob/master/csrc/include/cecog/features.hxx#L495
public class ClusterShade extends AbstractFeature
{

	@RequiredInput
	HaralickCoocMatrix cooc;

	@RequiredInput
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
	public ClusterShade copy()
	{
		return new ClusterShade();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType recompute()
	{
		final int nrGrayLevels = cooc.getNrGrayLevels();
		final double stdx = coocStdX.get().get();
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

		return new DoubleType( res );
	}
}
