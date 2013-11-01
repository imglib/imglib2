package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.firstorder.sums.Sum;
import net.imglib2.ops.features.geometric.Area;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class Mean< T extends RealType< T >> extends AbstractFeature< DoubleType >
{

	@RequiredFeature
	private Sum< T > sum;

	@RequiredFeature
	private Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Mean";
	}

	@Override
	public DoubleType recompute()
	{
		return new DoubleType( sum.get().get() / area.get().get() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mean< T > copy()
	{
		return new Mean< T >();
	}
}
