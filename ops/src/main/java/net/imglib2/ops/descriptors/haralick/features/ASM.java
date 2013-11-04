package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.descriptors.AbstractFeature;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.HaralickCoocMatrix;
import net.imglib2.type.numeric.real.DoubleType;

public class ASM extends AbstractFeature
{

	@ModuleInput
	HaralickCoocMatrix cooc;

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
	protected DoubleType compute()
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
