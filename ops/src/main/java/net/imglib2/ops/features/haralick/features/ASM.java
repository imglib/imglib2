package net.imglib2.ops.features.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.haralick.HaralickCoocMatrix;
import net.imglib2.type.numeric.real.DoubleType;

public class ASM extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	HaralickCoocMatrix< ? > cooc;

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
	public ASM copy()
	{
		return new ASM();
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
				res += matrix.getValueAt( i, j ) * matrix.getValueAt( i, j );
			}
		}

		return new DoubleType( res );
	}

}
